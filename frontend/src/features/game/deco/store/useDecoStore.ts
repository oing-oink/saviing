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

/** 인벤토리에서 드래그를 시작할 때 필요한 옵션 값. */
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

/** 서버에서 내려온 방 상태를 스토어에 주입할 때 사용하는 페이로드. */
interface ApplyServerStatePayload {
  roomMeta?: RoomMeta;
  placedItems: PlacedItem[];
}

/** 데코 편집 상태를 조작하기 위한 액션 모음. */
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

/** 초깃값으로 사용하는 방 메타 정보. */
const initialRoomMeta: RoomMeta = {
  cellSize: 1,
  layers: ['floor', 'leftWall', 'rightWall'],
};

/** 드래그 세션 정보를 기반으로 배치 아이템 엔티티를 생성한다. */
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

/** 드래그를 추적하기 위한 세션 객체를 생성한다. */
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

/** 서버에서 내려온 아이템에 고유 ID가 없을 경우 인스턴스 ID를 부여한다. */
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

/** 드래프트 아이템 배열에서 특정 ID의 인덱스를 찾는다. */
const findDraftIndex = (items: PlacedItem[], id: string) =>
  items.findIndex(item => item.id === id);

/** 방 데코 편집 전용 Zustand 스토어 인스턴스. */
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

/** 컴포넌트에서 데코 스토어를 구독하기 위한 커스텀 훅. */
export const useDecoStore = <T>(selector: (state: DecoStore) => T) =>
  useStore(decoStore, selector);
