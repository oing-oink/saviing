import { useEffect } from 'react';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import { mockInventoryItems } from '@/features/game/deco/mocks/inventoryMockData';

/**
 * 데코 편집 화면에서 사용할 인벤토리/배치 데이터를 임시 목업으로 제공한다.
 */
export const useDecoInventory = () => {
  const applyServerState = useDecoStore(state => state.applyServerState);

  useEffect(() => {
    applyServerState({ placedItems: [] });
  }, [applyServerState]);

  return {
    items: mockInventoryItems,
    placedItems: [] as PlacedItem[],
    isLoading: false,
    isError: false,
    error: null,
    refetch: async () => mockInventoryItems,
  } as const;
};
