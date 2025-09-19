import { useQuery } from '@tanstack/react-query';
import { getGameItems, getGameItemDetail } from '@/features/game/shop/api/itemsApi';
import { itemsKeys } from '@/features/game/shop/query/itemsKeys';

/**
 * 게임 아이템 목록을 조회하는 React Query 훅
 * @param type - 아이템 타입 (PET, DECORATION)
 * @param category - 아이템 카테고리 (CAT, LEFT, RIGHT, BOTTOM)
 */
export const useGameItems = (type: string, category: string) => {
  return useQuery({
    queryKey: itemsKeys.listByTypeAndCategory(type, category),
    queryFn: () => getGameItems(type, category),
    staleTime: 1000 * 60, // 1분
    gcTime: 1000 * 60 * 5, // 5분
  });
};

/**
 * 특정 아이템의 상세 정보를 조회하는 React Query 훅
 * @param itemId - 아이템 ID
 */
export const useGameItemDetail = (itemId: number | null) => {
  return useQuery({
    queryKey: itemsKeys.detail(itemId!),
    queryFn: () => getGameItemDetail(itemId!),
    enabled: !!itemId, // itemId가 있을 때만 실행
    staleTime: 1000 * 60, // 1분
    gcTime: 1000 * 60 * 5, // 5분
  });
};