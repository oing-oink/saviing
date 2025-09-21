import { useMemo } from 'react';
import type { PlacedItem } from '@/features/game/deco/types/decoTypes';
import { buildFootprint } from '@/features/game/deco/utils/grid';

export const useOccupancyMap = (items: PlacedItem[]) =>
  useMemo(() => {
    const occupied = new Set<string>();
    items.forEach(item => {
      const footprint =
        item.footprintCellIds && item.footprintCellIds.length > 0
          ? item.footprintCellIds
          : buildFootprint(item.cellId, item.xLength ?? 1, item.yLength ?? 1);
      footprint.forEach(cellId => occupied.add(cellId));
    });
    return occupied;
  }, [items]);
