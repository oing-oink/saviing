import { useMutation } from '@tanstack/react-query';
import { saveDecoRoom } from '@/features/game/deco/api/decoApi';
import { decoStore } from '@/features/game/deco/store/useDecoStore';
import type { PlacementArea } from '@/features/game/room/hooks/useGrid';

/** 현재 드래프트 상태를 API 저장 포맷으로 변환한다. */
const toRequestPayload = () => {
  const { draftItems } = decoStore.getState();
  return {
    placedItems: draftItems.map(item => ({
      inventoryItemId: item.inventoryItemId,
      itemId: item.itemId,
      positionX: item.positionX,
      positionY: item.positionY,
      xLength: item.xLength,
      yLength: item.yLength,
      rotation: item.rotation,
      layer: item.layer as PlacementArea | string | undefined,
    })),
  };
};

/** 데코 저장 API를 호출하고 성공 시 드래프트를 확정하는 뮤테이션 훅. */
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
