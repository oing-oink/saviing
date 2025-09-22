import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getRoomDetail } from '@/features/game/room/api/roomApi';
import type { PlacementArea } from '@/features/game/room/hooks/useGrid';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import { buildFootprint } from '@/features/game/deco/utils/grid';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';

/** API 카테고리를 배치 영역으로 변환한다. */
const getCategoryPlacementArea = (
  category?: string | null,
): PlacementArea | undefined => {
  if (category === 'LEFT') return 'LEFT';
  if (category === 'RIGHT') return 'RIGHT';
  if (category === 'BOTTOM' || category === 'ROOM_COLOR') return 'BOTTOM';
  return undefined;
};

/** 서버 응답을 Room에서 사용하는 PlacedItem 형태로 정규화한다. */
const mapPlacedItem = (item: {
  inventoryItemId: number;
  positionX: number;
  positionY: number;
  xLength: number;
  yLength: number;
  rotation?: 0 | 90 | 180 | 270;
  layer?: string;
  itemInfo: {
    itemId: number;
    category: string;
    type: string;
    image?: string;
  };
}): PlacedItem => {
  const placementArea =
    (item.layer as PlacementArea | undefined) ??
    getCategoryPlacementArea(item.itemInfo.category);
  const cellId = placementArea
    ? `${placementArea}-${item.positionX}-${item.positionY}`
    : `BOTTOM-${item.positionX}-${item.positionY}`;

  const footprint = buildFootprint(
    cellId,
    item.xLength ?? 1,
    item.yLength ?? 1,
  );
  const imageUrl = getItemImage(item.itemInfo.itemId);

  return {
    id: String(
      item.inventoryItemId ??
        `${item.itemInfo.itemId}-${item.positionX}-${item.positionY}`,
    ),
    inventoryItemId: item.inventoryItemId,
    itemId: item.itemInfo.itemId,
    cellId,
    positionX: item.positionX,
    positionY: item.positionY,
    rotation: item.rotation ?? 0,
    layer: placementArea,
    xLength: item.xLength ?? 1,
    yLength: item.yLength ?? 1,
    footprintCellIds: footprint,
    imageUrl,
    itemType: item.itemInfo.type,
  };
};

const ROOM_QUERY_KEY = ['game', 'room', 'detail'] as const;

/** 방 상세 정보를 불러오고 데코 스토어 상태로 동기화하는 React Query 훅. */
export const useRoomState = () => {
  const applyServerState = useDecoStore(state => state.applyServerState);

  const query = useQuery({
    queryKey: ROOM_QUERY_KEY,
    queryFn: getRoomDetail,
    staleTime: 60 * 1000,
    retry: false,
  });

  useEffect(() => {
    if (!query.data) {
      return;
    }

    const normalized = query.data.placedItems.map(item => mapPlacedItem(item));
    applyServerState({ placedItems: normalized });
  }, [applyServerState, query.data]);

  return query;
};
