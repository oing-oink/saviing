import { useMutation } from '@tanstack/react-query';
// import { saveDecoRoom } from '@/features/game/deco/api/decoApi';
import { saveRoomPlacements } from '@/features/game/room/api/roomApi';
import type { SaveRoomPlacementsRequest } from '@/features/game/room/api/roomApi';
import {
  decoStore,
  useDecoStore,
} from '@/features/game/deco/store/useDecoStore';
// import type { PlacementArea } from '@/features/game/room/hooks/useGrid';

/** 현재 드래프트 상태를 기존 API 저장 포맷으로 변환한다. */
// const toRequestPayload = () => {
//   const { draftItems } = decoStore.getState();
//   return {
//     placedItems: draftItems.map(item => ({
//       inventoryItemId: item.inventoryItemId,
//       itemId: item.itemId,
//       positionX: item.positionX,
//       positionY: item.positionY,
//       xLength: item.xLength,
//       yLength: item.yLength,
//       rotation: item.rotation,
//       layer: item.layer as PlacementArea | string | undefined,
//     })),
//   };
// };

/** 현재 드래프트 상태를 새 방 배치 API 포맷으로 변환한다. */
const toRoomPlacementsPayload = (
  characterId: number,
  roomId: number,
): SaveRoomPlacementsRequest => {
  const { draftItems } = decoStore.getState();
  return {
    characterId,
    placedItems: draftItems
      .filter(item => item.inventoryItemId !== undefined)
      .map(item => ({
        roomId: roomId, // roomId 추가
        inventoryItemId: item.inventoryItemId!,
        itemId: item.itemId,
        positionX: item.positionX,
        positionY: item.positionY,
        xLength: item.xLength || 1,
        yLength: item.yLength || 1,
        category:
          item.itemType === 'PET'
            ? 'PET'
            : typeof item.layer === 'string'
              ? item.layer
              : 'BOTTOM',
      })),
  };
};

/** 데코 저장 API를 호출하고 성공 시 드래프트를 확정하는 뮤테이션 훅. */
export const useDecoSaveMutation = () => {
  const roomContext = useDecoStore(state => state.roomContext);

  return useMutation({
    mutationFn: async () => {
      const roomId = roomContext?.roomId;
      const characterId = roomContext?.characterId;

      if (typeof roomId !== 'number' || typeof characterId !== 'number') {
        throw new Error('방 또는 캐릭터 정보를 불러오지 못했습니다.');
      }

      const payload = toRoomPlacementsPayload(characterId, roomId);
      return saveRoomPlacements(roomId, payload);
    },
    onSuccess: () => {
      decoStore.getState().markDraftAsSaved();
    },
  });
};
