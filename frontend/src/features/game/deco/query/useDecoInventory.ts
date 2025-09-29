import { useCallback } from 'react';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import type { TabInfo } from '@/features/game/shop/types/item';

/** 선택된 탭에 맞는 데코 인벤토리 목록을 스토어에서 가져오는 훅. */
export const useDecoInventory = (tab: TabInfo) => {
  const items = useDecoStore(state => state.inventoryByCategory[tab.id] ?? []);
  const placedItems = useDecoStore(state => state.placedItems);
  const isHydrated = useDecoStore(state => state.isHydrated);
  const error = useDecoStore(state => state.hydrationError);

  const isLoading = !isHydrated && !error;
  const refetch = useCallback(async () => undefined, []);

  return {
    items,
    placedItems,
    isLoading,
    isError: Boolean(error),
    error,
    refetch,
  } as const;
};
