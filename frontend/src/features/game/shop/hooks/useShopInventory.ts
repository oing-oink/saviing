import { useMemo } from 'react';
import { useGameItems } from '@/features/game/shop/query/useItemsQuery';
import { TAB_TO_API_PARAMS, type Tab } from '@/features/game/shop/types/item';

export const useShopInventory = (tab: Tab) => {
  const params = useMemo(() => TAB_TO_API_PARAMS[tab.name], [tab.name]);
  const query = useGameItems(params.type, params.category);

  return {
    items: query.data?.items ?? [],
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
    refetch: query.refetch,
  };
};
