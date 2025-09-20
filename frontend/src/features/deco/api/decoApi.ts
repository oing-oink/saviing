import { http } from '@/shared/services/api/http';
import type { Item } from '@/features/game/shop/types/item';

export interface DecoInventoryItem extends Item {
  inventoryItemId: number;
  quantity: number;
  equipped?: boolean;
}

export interface DecoInventoryResponse {
  items: DecoInventoryItem[];
  placedItems: DecoPlacedItemResponse[];
}

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

export interface SaveDecoResponse {
  success: boolean;
}

export const getDecoInventory = async (): Promise<DecoInventoryResponse> => {
  const response = await http.get<DecoInventoryResponse>('/v1/game/deco/inventory');
  if (!response.body) {
    throw new Error('인벤토리 정보를 불러오지 못했습니다.');
  }
  return response.body;
};

export const saveDecoRoom = async (payload: SaveDecoRequest): Promise<SaveDecoResponse> => {
  const response = await http.patch<SaveDecoResponse>('/v1/game/room/deco', payload);
  if (!response.body) {
    throw new Error('방 저장에 실패했습니다.');
  }
  return response.body;
};
