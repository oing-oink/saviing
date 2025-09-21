import type { GridType } from '@/features/game/room/hooks/useGrid';
import type { TabId } from '@/features/game/shop/types/item';

export interface RoomMeta {
  cellSize?: number;
  layers?: GridType[];
}

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

export interface DecoState {
  roomMeta: RoomMeta;
  placedItems: PlacedItem[];
  draftItems: PlacedItem[];
  dragSession: DragSession | null;
  pendingPlacement: PlacedItem | null;
  scale: number;
}
