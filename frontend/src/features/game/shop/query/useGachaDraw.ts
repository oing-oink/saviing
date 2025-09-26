import { useMutation, useQueryClient } from '@tanstack/react-query';
import { drawGacha } from '@/features/game/shop/api/itemsApi';
import type {
  GachaDrawRequest,
  GachaDrawResponse,
} from '@/features/game/shop/types/item';
import { itemsKeys } from '@/features/game/shop/query/itemsKeys';
import { roomSnapshotKeys } from '@/features/game/room/query/roomSnapshotKeys';
import { decoStore } from '@/features/game/deco/store/useDecoStore';
import { fetchRoomSnapshot } from '@/features/game/deco/api/fetchRoomSnapshot';

export const useGachaDraw = () => {
  const queryClient = useQueryClient();

  return useMutation<GachaDrawResponse, Error, GachaDrawRequest>({
    mutationFn: drawGacha,
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: itemsKeys.inventory(),
      });
      queryClient.invalidateQueries({
        queryKey: itemsKeys.list(),
      });
      queryClient.invalidateQueries({
        queryKey: roomSnapshotKeys.all,
      });

      const roomContext = decoStore.getState().roomContext;
      if (
        roomContext &&
        typeof roomContext.roomId === 'number' &&
        typeof roomContext.characterId === 'number'
      ) {
        const characterId =
          typeof variables?.characterId === 'number'
            ? variables.characterId
            : roomContext.characterId;

        void fetchRoomSnapshot({
          roomId: roomContext.roomId,
          characterId,
        })
          .then(snapshot => {
            decoStore.getState().loadRoomSnapshot(snapshot);
          })
          .catch(error => {
            console.error('방 스냅샷 갱신 실패:', error);
          });
      }
    },
  });
};
