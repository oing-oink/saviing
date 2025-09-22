import { useQuery } from '@tanstack/react-query';
import {
  getGameItems,
  getGameItemDetail,
  getInventoryItems,
} from '@/features/game/shop/api/itemsApi';
import { itemsKeys } from '@/features/game/shop/query/itemsKeys';

/**
 * 게임 아이템 목록을 조회하는 React Query 훅.
 * @param type 아이템 타입 (예: PET, DECORATION)
 * @param category 아이템 카테고리 (예: CAT, LEFT, RIGHT, BOTTOM)
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
 * 특정 아이템의 상세 정보를 조회하는 React Query 훅.
 * @param itemId 조회할 아이템 ID
 */
export const useGameItemDetail = (itemId: number | null) => {
  return useQuery({
    queryKey: itemsKeys.detail(itemId!),
    queryFn: () => getGameItemDetail(itemId!),
    enabled: Boolean(itemId), // itemId가 있을 때만 실행
    staleTime: 1000 * 60, // 1분
    gcTime: 1000 * 60 * 5, // 5분
  });
};

/**
 * 캐릭터의 인벤토리 아이템 목록을 조회하는 React Query 훅.
 * @param characterId 캐릭터 ID
 * @param type 인벤토리 타입 (예: PET, DECORATION)
 * @param category 아이템 카테고리 (예: CAT, LEFT, RIGHT, BOTTOM)
 * @param isUsed 사용 여부 (옵셔널)
 */
export const useInventoryItems = (
  characterId: number,
  type: string,
  category: string,
  isUsed?: boolean,
) => {
  return useQuery({
    queryKey: itemsKeys.inventoryByTypeAndCategory(characterId, type, category, isUsed),
    queryFn: () => getInventoryItems(characterId, type, category, isUsed),
    staleTime: 1000 * 60, // 1분
    gcTime: 1000 * 60 * 5, // 5분
  });
};
