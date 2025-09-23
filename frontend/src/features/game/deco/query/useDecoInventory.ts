import { useEffect, useMemo } from 'react';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import { useInventoryItems } from '@/features/game/shop/query/useItemsQuery';
import { TAB_TO_CATEGORY, type TabInfo } from '@/features/game/shop/types/item';

/** 선택된 탭에 맞는 데코 인벤토리 목록을 불러오는 훅. */
export const useDecoInventory = (tab: TabInfo) => {
  // Todo: 나중에 실제 캐릭터 ID로 수정
  const characterId = 1;

  const applyServerState = useDecoStore(state => state.applyServerState);

  const category = useMemo(() => TAB_TO_CATEGORY[tab.id], [tab.id]);
  // PET의 경우 type도 함께 사용, 다른 경우는 카테고리만 사용
  const query = useInventoryItems(
    characterId,
    category === 'CAT' ? 'PET' : 'DECORATION',
    category,
  );

  useEffect(() => {
    applyServerState({ placedItems: [] });
  }, [applyServerState]);

  return {
    items: query.data?.items ?? [],
    placedItems: [] as PlacedItem[],
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
    refetch: query.refetch,
  } as const;
};
