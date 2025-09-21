import { useEffect } from 'react';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import { mockInventoryItems } from '@/features/game/deco/mocks/inventoryMockData';

// 데코 편집 화면용 인벤토리 훅: 실제 API가 준비되기 전까지 목 데이터를 반환한다.
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
