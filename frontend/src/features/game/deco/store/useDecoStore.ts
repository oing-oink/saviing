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

/**
 * 인벤토리에서 드래그를 시작할 때 사용할 수 있는 추가 옵션들.
 *
 * 이 인터페이스는 기본 아이템 정보 외에 드래그 세션에서 사용할
 * 특별한 동작이나 제약사항을 지정하는 데 사용됩니다.
 */
interface StartDragOptions {
  /**
   * 아이템을 배치할 수 있는 레이어 타입 제한.
   * null이면 모든 레이어에 배치 가능, 특정 값이면 해당 레이어만 허용됩니다.
   */
  allowedGridType?: TabId | null;

  /**
   * 아이템의 가로 크기 (그리드 셀 개수).
   * 기본값은 아이템 마스터 데이터의 값을 사용합니다.
   */
  xLength?: number;

  /**
   * 아이템의 세로 크기 (그리드 셀 개수).
   * 기본값은 아이템 마스터 데이터의 값을 사용합니다.
   */
  yLength?: number;

  /**
   * 명시적으로 지정할 점유 셀 ID 배열.
   * 복잡한 모양의 아이템이나 특별한 배치 규칙이 있는 경우 사용됩니다.
   */
  footprintCellIds?: string[];

  /**
   * 아이템의 X축 렌더링 오프셋 (픽셀 단위).
   * 그리드에 정확히 맞지 않는 아이템의 시각적 정렬에 사용됩니다.
   */
  offsetX?: number;

  /**
   * 아이템의 Y축 렌더링 오프셋 (픽셀 단위).
   * 그리드에 정확히 맞지 않는 아이템의 시각적 정렬에 사용됩니다.
   */
  offsetY?: number;

  /**
   * 드래그 중 표시할 커스텀 이미지 URL.
   * 제공되지 않으면 itemId를 기반으로 기본 이미지를 사용합니다.
   */
  imageUrl?: string;

  /**
   * 미리보기 모드 드래그 여부.
   * true인 경우 실제 저장되지 않는 임시 배치로 처리됩니다.
   */
  isPreview?: boolean;

  /**
   * 아이템의 분류 타입 ('DECORATION' 등).
   * 배치 규칙이나 UI 필터링에 사용됩니다.
   */
  itemType?: PlacedItem['itemType'];
}

/**
 * 서버에서 조회한 방 상태를 클라이언트 스토어에 적용하기 위한 데이터 구조.
 *
 * 방 입장 시나 전체 상태 동기화 시에 서버 데이터를 클라이언트 상태로
 * 변환하여 스토어에 주입하는 데 사용됩니다.
 */
interface ApplyServerStatePayload {
  /**
   * 방의 메타데이터 (그리드 크기, 레이어 구성 등).
   * 제공되지 않으면 기본 메타데이터를 사용합니다.
   */
  roomMeta?: RoomMeta;

  /**
   * 서버에 저장된 배치 아이템 목록.
   * 이 데이터가 placedItems와 draftItems의 초기값이 됩니다.
   */
  placedItems: PlacedItem[];
}

/**
 * 데코레이션 시스템의 상태를 변경하는 모든 액션 함수들의 인터페이스.
 *
 * 이 인터페이스는 드래그 앤 드롭, 배치 관리, 상태 동기화 등
 * 데코 시스템의 모든 상태 변경 동작을 정의합니다.
 */
interface DecoActions {
  /**
   * 인벤토리에서 새 아이템의 드래그를 시작합니다.
   *
   * @param itemId - 드래그할 아이템의 마스터 데이터 ID
   * @param options - 드래그 동작을 제어하는 추가 옵션들
   */
  startDragFromInventory: (itemId: string, options?: StartDragOptions) => void;

  /**
   * 이미 배치된 아이템을 선택하여 재배치 드래그를 시작합니다.
   *
   * @param placedId - 재배치할 배치 아이템의 ID
   */
  startDragFromPlaced: (placedId: string) => void;

  /**
   * 드래그 중 마우스 커서가 위치한 셀 ID를 업데이트합니다.
   *
   * @param cellId - 현재 호버 중인 셀의 ID (null이면 호버 해제)
   */
  updateHoverCell: (cellId: string | null) => void;

  /**
   * 현재 드래그 중인 아이템을 지정된 위치에 임시 배치(스테이징)합니다.
   *
   * @param cellId - 배치할 기준 셀의 ID
   * @param footprintCellIds - 명시적 점유 셀 배열 (선택적)
   * @returns 스테이징 성공 여부
   */
  stagePlacement: (cellId: string, footprintCellIds?: string[]) => boolean;

  /**
   * 스테이징된 배치를 확정하여 draftItems에 추가합니다.
   *
   * @returns 확정 성공 여부
   */
  commitPlacement: () => boolean;

  /**
   * 현재 진행 중인 배치를 취소하고 원래 상태로 복원합니다.
   */
  cancelPendingPlacement: () => void;

  /**
   * 현재 드래그 세션을 취소하고 원래 상태로 복원합니다.
   */
  cancelDrag: () => void;

  /**
   * 드래그 중인 아이템을 삭제합니다 (재배치 중단 + 아이템 제거).
   */
  deleteDraggedItem: () => void;

  /**
   * 지정된 ID의 드래프트 아이템을 제거합니다.
   *
   * @param id - 제거할 아이템의 ID
   */
  removeDraftItem: (id: string) => void;

  /**
   * 서버에서 조회한 방 상태를 스토어에 적용합니다.
   *
   * @param payload - 서버 상태 데이터
   */
  applyServerState: (payload: ApplyServerStatePayload) => void;

  /**
   * 현재 편집 중인 드래프트 상태를 마지막 저장된 상태로 되돌립니다.
   */
  resetToLastSaved: () => void;

  /**
   * 현재 드래프트 상태를 저장된 상태로 표시합니다 (저장 완료 후 호출).
   */
  markDraftAsSaved: () => void;

  /**
   * 캔버스의 확대/축소 비율을 설정합니다.
   *
   * @param scale - 새로운 스케일 값 (1.0이 기본 크기)
   */
  setScale: (scale: number) => void;
}

type DecoStore = DecoState & DecoActions;

/**
 * 방 시스템의 기본 메타데이터 설정값.
 *
 * 서버에서 메타데이터가 제공되지 않거나 초기화 시에 사용되는
 * 기본 그리드 구성과 레이어 설정입니다.
 */
const initialRoomMeta: RoomMeta = {
  /** 그리드 셀의 기본 크기 단위 */
  cellSize: 1,
  /** 기본 사용 가능한 배치 레이어들 */
  layers: ['floor', 'leftWall', 'rightWall'],
};

/**
 * 드래그 세션의 정보를 사용하여 실제 배치 아이템 객체를 생성하는 팩토리 함수.
 *
 * 이 함수는 드래그 중인 아이템의 임시 정보를 바탕으로 실제 배치될
 * PlacedItem 객체를 생성합니다. 레이어 제한, footprint 계산,
 * ID 생성 등의 복잡한 로직을 처리합니다.
 *
 * @param session - 현재 드래그 세션 정보
 * @param cellId - 배치할 기준 셀의 ID
 * @param footprintCellIds - 명시적으로 지정할 점유 셀 배열 (선택적)
 * @returns 생성된 배치 아이템 객체 또는 null (생성 실패 시)
 */
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

/**
 * 새로운 드래그 세션 객체를 생성하는 팩토리 함수.
 *
 * 기본 드래그 세션 구조를 생성하고 필요에 따라 특정 속성을
 * 오버라이드할 수 있습니다. 인벤토리 드래그와 재배치 드래그
 * 모두에서 사용됩니다.
 *
 * @param itemId - 드래그할 아이템의 마스터 데이터 ID
 * @param overrides - 기본값을 대체할 속성들
 * @returns 초기화된 드래그 세션 객체
 */
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

/**
 * 서버에서 조회한 배치 아이템 배열에 클라이언트 고유 ID를 보장하는 함수.
 *
 * 서버 데이터에서 ID가 누락된 아이템들에 대해 inventoryItemId나
 * itemId를 기반으로 클라이언트에서 사용할 수 있는 고유 ID를 생성합니다.
 * 또한 누락된 기본값들도 함께 보완합니다.
 *
 * @param items - 서버에서 조회한 배치 아이템 배열
 * @returns ID와 기본값이 보장된 배치 아이템 배열
 */
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

/**
 * 드래프트 아이템 배열에서 지정된 ID를 가진 아이템의 인덱스를 찾는 유틸리티 함수.
 *
 * @param items - 검색할 드래프트 아이템 배열
 * @param id - 찾을 아이템의 고유 ID
 * @returns 해당 아이템의 인덱스 (없으면 -1)
 */
const findDraftIndex = (items: PlacedItem[], id: string) =>
  items.findIndex(item => item.id === id);

/**
 * 방 데코레이션 시스템의 중앙 상태 관리를 위한 Zustand 스토어.
 *
 * 이 스토어는 드래그 앤 드롭, 아이템 배치, 서버 동기화 등
 * 데코 시스템의 모든 상태와 액션을 관리합니다. vanilla 스토어로
 * 생성되어 React 외부에서도 접근 가능합니다.
 */
export const decoStore = createStore<DecoStore>(set => ({
  /** 방의 그리드 구성 및 레이어 정보 */
  roomMeta: initialRoomMeta,
  /** 서버에 저장된 확정 배치 아이템들 */
  placedItems: [],
  /** 현재 편집 중인 아이템들 (미저장 상태 포함) */
  draftItems: [],
  /** 현재 진행 중인 드래그 세션 정보 */
  dragSession: null,
  /** 배치 확정 대기 중인 아이템 정보 */
  pendingPlacement: null,
  /** 캔버스의 현재 확대/축소 비율 */
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

/**
 * React 컴포넌트에서 데코 스토어를 구독하기 위한 커스텀 훅.
 *
 * Zustand의 useStore를 래핑하여 타입 안전성을 보장하고
 * 선택적 구독을 통한 성능 최적화를 제공합니다.
 *
 * @param selector - 스토어에서 필요한 상태만 선택하는 함수
 * @returns 선택된 상태 값 (변경 시 리렌더링 트리거)
 *
 * @example
 * ```tsx
 * const dragSession = useDecoStore(state => state.dragSession);
 * const { draftItems, commitPlacement } = useDecoStore(state => ({
 *   draftItems: state.draftItems,
 *   commitPlacement: state.commitPlacement
 * }));
 * ```
 */
export const useDecoStore = <T>(selector: (state: DecoStore) => T) =>
  useStore(decoStore, selector);
