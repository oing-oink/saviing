import type { GridType } from '@/features/game/room/hooks/useGrid';
import type { TabId } from '@/features/game/shop/types/item';
import type { Item } from '@/features/game/shop/types/item';

/** 방 그리드 크기 및 레이어 구성을 표현하는 메타 정보. */
export interface RoomMeta {
  cellSize?: number;
  layers?: GridType[];
}

/** 방에 배치된 아이템의 좌표, 크기, 회전 정보를 담는 엔티티. */
export interface PlacedItem {
  id: string;
  inventoryItemId?: number;
  itemId: number;
  cellId: string;
  positionX: number;
  positionY: number;
  rotation: 0 | 90 | 180 | 270;
  layer?: GridType | string;
  xLength: number;
  yLength: number;
  footprintCellIds?: string[];
  offsetX?: number;
  offsetY?: number;
  imageUrl?: string;
  itemType?: string;
  isPreview?: boolean;
}

/** 드래그 중인 아이템의 상태를 추적하기 위한 세션 정보. */
export interface DragSession {
  itemId: string;
  originPlacedId?: string;
  originalItem?: PlacedItem;
  hoverCellId: string | null;
  allowedGridType: TabId | null;
  xLength: number;
  yLength: number;
  footprintCellIds?: string[];
  offsetX?: number;
  offsetY?: number;
  imageUrl?: string;
  itemType?: string;
  isPreview?: boolean;
}

/** 데코 스토어가 관리하는 전체 상태 구조. */
export interface DecoState {
  roomMeta: RoomMeta;
  placedItems: PlacedItem[];
  draftItems: PlacedItem[];
  dragSession: DragSession | null;
  pendingPlacement: PlacedItem | null;
  scale: number;
}

/** 상점 아이템 정보를 확장해 데코 인벤토리에서 사용하는 메타데이터를 담는다. */
export interface DecoInventoryItem extends Item {
  inventoryItemId: number;
  quantity: number;
  equipped?: boolean;
}

/** 인벤토리 목록과 이미 배치된 아이템 정보를 함께 내려주는 응답 형식. */
export interface DecoInventoryResponse {
  items: DecoInventoryItem[];
  placedItems: DecoPlacedItemResponse[];
}

/** 서버가 반환하는 배치 아이템 좌표 및 회전 정보. */
export interface DecoPlacedItemResponse {
  inventoryItemId: number;
  itemId: number;
  positionX: number;
  positionY: number;
  xLength: number;
  yLength: number;
  rotation?: 0 | 90 | 180 | 270;
  layer?: string;
  offsetX?: number;
  offsetY?: number;
}

/** 현재 방 데코 상태를 저장하기 위한 요청 페이로드. */
export interface SaveDecoRequest {
  placedItems: {
    inventoryItemId?: number;
    itemId: number;
    positionX: number;
    positionY: number;
    xLength: number;
    yLength: number;
    rotation?: 0 | 90 | 180 | 270;
    layer?: string;
  }[];
}

/** 방 데코 저장 성공 여부만을 전달하는 응답. */
export interface SaveDecoResponse {
  success: boolean;
}
