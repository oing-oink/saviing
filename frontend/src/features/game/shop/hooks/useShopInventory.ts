import { useMemo } from 'react';
import { useGameItems } from '@/features/game/shop/query/useItemsQuery';
import { TAB_TO_CATEGORY, type Tab } from '@/features/game/shop/types/item';

/** 선택된 탭에 맞는 상점 인벤토리 목록을 불러오는 훅. */
export const useShopInventory = (tab: Tab) => {
  const category = useMemo(() => TAB_TO_CATEGORY[tab.id], [tab.id]);
  const itemType = useMemo(() => {
    if (category === 'CAT') {
      return 'PET';
    }
    if (category === 'TOY' || category === 'FOOD') {
      return 'CONSUMPTION';
    }
    return 'DECORATION';
  }, [category]);

  const query = useGameItems(itemType, category);

  return {
    items: query.data?.items ?? [],
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
    refetch: query.refetch,
  };
};
