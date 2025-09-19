import { http } from '@/shared/services/api/http';
import type { ItemsResponse, Item } from '@/features/game/shop/types/item';

/**
 * 게임 아이템 전체 목록을 조회하는 API
 * @param type - 아이템 타입 (PET, DECORATION)
 * @param category - 아이템 카테고리 (CAT, LEFT, RIGHT, BOTTOM)
 * @returns ItemsResponse 아이템 목록과 총 개수
 */
export const getGameItems = async (type: string, category: string): Promise<ItemsResponse> => {
  const params: Record<string, string | boolean> = {
    type: type,
    category: category,
    sort: 'NAME',
    order: 'ASC',
  };

  const response = await http.get<ItemsResponse>('/v1/game/items', {
    params,
  });

  if (!response.body) {
    return { items: [], totalCount: 0 };
  }

  return response.body;
};

/**
 * 특정 아이템의 상세 정보를 조회하는 API
 * @param itemId - 아이템 ID
 * @returns Item 아이템 상세 정보
 */
export const getGameItemDetail = async (itemId: number): Promise<Item> => {
  const response = await http.get<Item>(`/v1/game/items/${itemId}`);

  if (!response.body) {
    throw new Error('아이템 정보를 찾을 수 없습니다.');
  }

  return response.body;
};
