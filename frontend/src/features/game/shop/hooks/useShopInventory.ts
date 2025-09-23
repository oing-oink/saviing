import { useMemo } from 'react';
import { useGameItems } from '@/features/game/shop/query/useItemsQuery';
import { TAB_TO_CATEGORY, type Tab } from '@/features/game/shop/types/item';

/** 선택된 탭에 맞는 상점 인벤토리 목록을 불러오는 훅. */
export const useShopInventory = (tab: Tab) => {
  const category = useMemo(() => TAB_TO_CATEGORY[tab.id], [tab.id]);
  // PET의 경우 type도 함께 사용, 다른 경우는 카테고리만 사용
  const query = useGameItems(
    category === 'CAT' ? 'PET' : 'DECORATION',
    category,
  );

  return {
    items: query.data?.items ?? [],
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
    refetch: query.refetch,
  };
};
