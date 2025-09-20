import { useMutation } from '@tanstack/react-query';
import { saveDecoRoom } from '@/features/deco/api/decoApi';
import { decoStore } from '@/features/deco/state/deco.store';
import type { GridType } from '@/features/game/room/hooks/useGrid';

const toRequestPayload = () => {
  const { draftItems } = decoStore.getState();
  return {
    placedItems: draftItems.map((item) => ({
      inventoryItemId: item.inventoryItemId,
      itemId: item.itemId,
      positionX: item.positionX,
      positionY: item.positionY,
      xLength: item.xLength,
      yLength: item.yLength,
      rotation: item.rotation,
      layer: item.layer as GridType | string | undefined,
    })),
  };
};

export const useDecoSaveMutation = () => {
  return useMutation({
    mutationFn: async () => {
      const payload = toRequestPayload();
      return saveDecoRoom(payload);
    },
    onSuccess: () => {
      decoStore.getState().markDraftAsSaved();
    },
  });
};
