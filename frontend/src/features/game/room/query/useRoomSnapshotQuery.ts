import { useQuery, type UseQueryOptions } from '@tanstack/react-query';
import {
  fetchRoomSnapshot,
  type RoomSnapshotResult,
} from '@/features/game/deco/api/fetchRoomSnapshot';
import { roomSnapshotKeys } from './roomSnapshotKeys';

export const useRoomSnapshotQuery = (
  roomId?: number,
  characterId?: number,
  options?: Pick<
    UseQueryOptions<RoomSnapshotResult, Error>,
    'enabled' | 'retry' | 'staleTime' | 'gcTime'
  >,
) => {
  const canFetch =
    typeof roomId === 'number' && typeof characterId === 'number';
  const enabled = (options?.enabled ?? true) && canFetch;

  return useQuery<RoomSnapshotResult, Error>({
    queryKey: canFetch
      ? roomSnapshotKeys.byRoom(roomId as number, characterId as number)
      : roomSnapshotKeys.all,
    queryFn: () =>
      fetchRoomSnapshot({
        roomId: roomId as number,
        characterId: characterId as number,
      }),
    enabled,
    retry: options?.retry ?? 0,
    staleTime: options?.staleTime ?? 0,
    gcTime: options?.gcTime ?? 0,
  });
};
