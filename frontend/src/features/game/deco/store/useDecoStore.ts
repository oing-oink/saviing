import { createStore } from 'zustand/vanilla';
import { useStore } from 'zustand';
import type {
  DecoState,
  DragSession,
  PlacedItem,
  RoomMeta,
} from '@/features/game/deco/types/decoTypes';
import type { GridType } from '@/features/game/room/hooks/useGrid';
import type { TabId } from '@/features/game/shop/types/item';
import { buildFootprint, parseCellId } from '@/features/game/deco/utils/grid';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';

interface StartDragOptions {
  allowedGridType?: TabId | null;
  xLength?: number;
  yLength?: number;
  footprintCellIds?: string[];
  offsetX?: number;
  offsetY?: number;
  imageUrl?: string;
  isPreview?: boolean;
  itemType?: PlacedItem['itemType'];
}

interface ApplyServerStatePayload {
  roomMeta?: RoomMeta;
  placedItems: PlacedItem[];
}

interface DecoActions {
  startDragFromInventory: (itemId: string, options?: StartDragOptions) => void;
  startDragFromPlaced: (placedId: string) => void;
  updateHoverCell: (cellId: string | null) => void;
  stagePlacement: (cellId: string, footprintCellIds?: string[]) => boolean;
  commitPlacement: () => boolean;
  cancelPendingPlacement: () => void;
  cancelDrag: () => void;
  deleteDraggedItem: () => void;
  removeDraftItem: (id: string) => void;
  applyServerState: (payload: ApplyServerStatePayload) => void;
  resetToLastSaved: () => void;
  markDraftAsSaved: () => void;
  setScale: (scale: number) => void;
}

type DecoStore = DecoState & DecoActions;

const initialRoomMeta: RoomMeta = {
  cellSize: 1,
  layers: ['floor', 'leftWall', 'rightWall'],
};

const buildPlacedItemFromSession = (
  session: DragSession,
  cellId: string,
  footprintCellIds?: string[],
): PlacedItem | null => {
  const parsed = parseCellId(cellId);
  if (!parsed) {
    return null;
  }

  if (session.allowedGridType && session.allowedGridType !== parsed.gridType) {
    return null;
  }

  const resolvedFootprint =
    footprintCellIds && footprintCellIds.length > 0
      ? footprintCellIds
      : session.footprintCellIds && session.footprintCellIds.length > 0
        ? session.footprintCellIds
        : buildFootprint(cellId, session.xLength, session.yLength);

  if (!resolvedFootprint.length) {
    return null;
  }

  const id = session.originPlacedId ?? `draft-${session.itemId}-${Date.now()}`;

  return {
    id,
    inventoryItemId: session.originalItem?.inventoryItemId,
    itemId: Number(session.itemId),
    cellId,
    positionX: parsed.col,
    positionY: parsed.row,
    rotation: session.originalItem?.rotation ?? 0,
    layer: parsed.gridType,
    xLength: session.xLength,
    yLength: session.yLength,
    footprintCellIds: resolvedFootprint,
    offsetX: session.offsetX ?? 0,
    offsetY: session.offsetY ?? 0,
    imageUrl:
      session.imageUrl ??
      session.originalItem?.imageUrl ??
      getItemImage(Number(session.itemId)),
    itemType:
      session.itemType ?? session.originalItem?.itemType ?? 'DECORATION',
    isPreview: session.isPreview ?? session.originalItem?.isPreview ?? false,
  };
};

const createDragSession = (
  itemId: string,
  overrides?: Partial<DragSession>,
): DragSession => ({
  itemId,
  xLength: 1,
  yLength: 1,
  hoverCellId: null,
  allowedGridType: null,
  offsetX: 0,
  offsetY: 0,
  imageUrl: undefined,
  itemType: 'DECORATION',
  isPreview: false,
  ...overrides,
});

const withInstanceId = (items: PlacedItem[]): PlacedItem[] =>
  items.map((item, index) => ({
    id:
      item.id ??
      (item.inventoryItemId !== undefined
        ? String(item.inventoryItemId)
        : `${item.itemId}-${index}`),
    inventoryItemId: item.inventoryItemId,
    itemId: item.itemId,
    cellId: item.cellId,
    positionX: item.positionX,
    positionY: item.positionY,
    rotation: item.rotation ?? 0,
    layer: item.layer,
    xLength: item.xLength ?? 1,
    yLength: item.yLength ?? 1,
    footprintCellIds: item.footprintCellIds,
    offsetX: item.offsetX,
    offsetY: item.offsetY,
    imageUrl: item.imageUrl ?? getItemImage(item.itemId),
    itemType: item.itemType,
  }));

const findDraftIndex = (items: PlacedItem[], id: string) =>
  items.findIndex(item => item.id === id);

export const decoStore = createStore<DecoStore>(set => ({
  roomMeta: initialRoomMeta,
  placedItems: [],
  draftItems: [],
  dragSession: null,
  pendingPlacement: null,
  scale: 1,

  startDragFromInventory: (itemId, options: StartDragOptions = {}) =>
    set(() => ({
      pendingPlacement: null,
      dragSession: createDragSession(itemId, {
        allowedGridType: options.allowedGridType ?? null,
        xLength: options.xLength ?? 1,
        yLength: options.yLength ?? 1,
        footprintCellIds: options.footprintCellIds,
        offsetX: options.offsetX ?? 0,
        offsetY: options.offsetY ?? 0,
        // 서버 이미지가 준비되기 전까지 로컬 asset을 항상 사용한다.
        imageUrl: getItemImage(Number(itemId)),
        itemType: options.itemType ?? 'DECORATION',
        isPreview: options.isPreview ?? false,
      }),
    })),

  startDragFromPlaced: placedId =>
    set(state => {
      const index = findDraftIndex(state.draftItems, placedId);
      if (index === -1) {
        return state;
      }

      const target = state.draftItems[index];
      const remainingDraft = state.draftItems.filter(
        item => item.id !== placedId,
      );
      const parsed = parseCellId(target.cellId);
      const inferredGridType = (target.layer ?? parsed?.gridType) as
        | GridType
        | undefined;
      const resolvedFootprint =
        target.footprintCellIds && target.footprintCellIds.length > 0
          ? target.footprintCellIds
          : buildFootprint(
              target.cellId,
              target.xLength ?? 1,
              target.yLength ?? 1,
            );

      return {
        draftItems: remainingDraft,
        dragSession: createDragSession(String(target.itemId), {
          originPlacedId: target.id,
          originalItem: target,
          hoverCellId: target.cellId,
          allowedGridType: inferredGridType ?? null,
          xLength: target.xLength ?? 1,
          yLength: target.yLength ?? 1,
          footprintCellIds: resolvedFootprint,
          offsetX: target.offsetX ?? 0,
          offsetY: target.offsetY ?? 0,
          imageUrl: target.imageUrl ?? getItemImage(target.itemId),
          itemType: target.itemType ?? 'DECORATION',
          isPreview: target.isPreview ?? false,
        }),
        pendingPlacement: null,
      };
    }),

  updateHoverCell: cellId =>
    set(state => {
      if (!state.dragSession) {
        return state;
      }

      return {
        dragSession: {
          ...state.dragSession,
          hoverCellId: cellId,
        },
      };
    }),

  stagePlacement: (cellId, footprintCellIds) => {
    let staged = false;
    set(state => {
      if (!state.dragSession) {
        return state;
      }

      const nextItem = buildPlacedItemFromSession(
        state.dragSession,
        cellId,
        footprintCellIds,
      );
      if (!nextItem) {
        return {
          dragSession: null,
          pendingPlacement: null,
        };
      }

      staged = true;
      return {
        pendingPlacement: nextItem,
      };
    });

    return staged;
  },

  commitPlacement: () => {
    let committed = false;
    set(state => {
      if (!state.pendingPlacement) {
        return state;
      }

      const nextItem = state.pendingPlacement;
      committed = true;
      return {
        draftItems: [
          ...state.draftItems.filter(item => item.id !== nextItem.id),
          nextItem,
        ],
        dragSession: null,
        pendingPlacement: null,
      };
    });

    return committed;
  },

  cancelPendingPlacement: () =>
    set(state => {
      if (!state.dragSession) {
        return state.pendingPlacement ? { pendingPlacement: null } : state;
      }

      const originalItem = state.dragSession.originalItem;
      return {
        draftItems: originalItem
          ? [
              ...state.draftItems.filter(item => item.id !== originalItem.id),
              originalItem,
            ]
          : state.draftItems,
        dragSession: null,
        pendingPlacement: null,
      };
    }),

  cancelDrag: () =>
    set(state => {
      if (!state.dragSession) {
        return state.pendingPlacement ? { pendingPlacement: null } : state;
      }

      const originalItem = state.dragSession.originalItem;
      return {
        draftItems: originalItem
          ? [
              ...state.draftItems.filter(item => item.id !== originalItem.id),
              originalItem,
            ]
          : state.draftItems,
        dragSession: null,
        pendingPlacement: null,
      };
    }),

  deleteDraggedItem: () =>
    set(() => ({
      dragSession: null,
      pendingPlacement: null,
    })),

  removeDraftItem: id =>
    set(state => ({
      draftItems: state.draftItems.filter(item => item.id !== id),
    })),

  // 서버에서 내려온 방 상태를 store 형식으로 맞춰 초기화한다.
  applyServerState: ({ roomMeta, placedItems }) =>
    set(() => {
      const normalized = withInstanceId(placedItems);
      return {
        roomMeta: roomMeta ?? initialRoomMeta,
        placedItems: [...normalized],
        draftItems: [...normalized],
        dragSession: null,
        pendingPlacement: null,
      };
    }),

  resetToLastSaved: () =>
    set(state => ({
      draftItems: [...state.placedItems],
      dragSession: null,
      pendingPlacement: null,
    })),

  markDraftAsSaved: () =>
    set(state => ({
      placedItems: [...state.draftItems],
    })),

  setScale: scale => set(() => ({ scale })),
}));

export const useDecoStore = <T>(selector: (state: DecoStore) => T) =>
  useStore(decoStore, selector);
