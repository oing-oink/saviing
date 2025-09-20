import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getRoomDetail } from '@/features/game/room/api/roomApi';
import type { GridType } from '@/features/game/room/hooks/useGrid';
import type { PlacedItem } from '@/features/deco/types/deco.types';
import { useDecoStore } from '@/features/deco/state/deco.store';
import { buildFootprint } from '@/features/deco/utils/grid';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';

// API의 카테고리 값을 Room에서 사용하는 GridType으로 매핑한다.
const mapCategoryToGridType = (category?: string | null): GridType | undefined => {
  switch (category) {
    case 'LEFT':
      return 'leftWall';
    case 'RIGHT':
      return 'rightWall';
    case 'BOTTOM':
      return 'floor';
    default:
      return undefined;
  }
};

// 서버에서 내려온 배치 정보를 화면에서 사용하는 PlacedItem 형태로 변환한다.
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
  const gridType = (item.layer as GridType | undefined) ?? mapCategoryToGridType(item.itemInfo.category);
  const cellId = gridType
    ? `${gridType}-${item.positionX}-${item.positionY}`
    : `floor-${item.positionX}-${item.positionY}`;

  const footprint = buildFootprint(cellId, item.xLength ?? 1, item.yLength ?? 1);
  const imageUrl = getItemImage(item.itemInfo.itemId);

  return {
    id: String(item.inventoryItemId ?? `${item.itemInfo.itemId}-${item.positionX}-${item.positionY}`),
    inventoryItemId: item.inventoryItemId,
    itemId: item.itemInfo.itemId,
    cellId,
    positionX: item.positionX,
    positionY: item.positionY,
    rotation: item.rotation ?? 0,
    layer: gridType,
    xLength: item.xLength ?? 1,
    yLength: item.yLength ?? 1,
    footprintCellIds: footprint,
    imageUrl,
    itemType: item.itemInfo.type,
  };
};

const ROOM_QUERY_KEY = ['game', 'room', 'detail'] as const;

export const useRoomState = () => {
  const applyServerState = useDecoStore((state) => state.applyServerState);

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

    const normalized = query.data.placedItems.map((item) => mapPlacedItem(item));
    applyServerState({ placedItems: normalized });
  }, [applyServerState, query.data]);

  return query;
};
