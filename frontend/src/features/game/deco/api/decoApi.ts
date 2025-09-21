import { http } from '@/shared/services/api/http';
import type {
  DecoInventoryResponse,
  SaveDecoRequest,
  SaveDecoResponse,
} from '@/features/game/deco/types/decoTypes';

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
