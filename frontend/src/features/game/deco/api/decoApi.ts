import { http } from '@/shared/services/api/http';
import type { Item } from '@/features/game/shop/types/item';

/**
 * 상점 아이템 정보를 확장해 데코 인벤토리에서 사용하는 메타데이터를 담는다.
 */
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

/**
 * 플레이어의 데코 인벤토리와 배치 현황을 조회한다.
 * @throws 응답 본문이 비어 있을 때 오류를 발생시킨다.
 */
export const getDecoInventory = async (): Promise<DecoInventoryResponse> => {
  const response = await http.get<DecoInventoryResponse>(
    '/v1/game/deco/inventory',
  );
  if (!response.body) {
    throw new Error('인벤토리 정보를 불러오지 못했습니다.');
  }
  return response.body;
};

/**
 * 전달받은 방 데코 레이아웃을 서버에 저장한다.
 * @throws 응답 본문이 비어 있을 때 오류를 발생시킨다.
 */
export const saveDecoRoom = async (
  payload: SaveDecoRequest,
): Promise<SaveDecoResponse> => {
  const response = await http.patch<SaveDecoResponse>(
    '/v1/game/room/deco',
    payload,
  );
  if (!response.body) {
    throw new Error('방 저장에 실패했습니다.');
  }
  return response.body;
};
