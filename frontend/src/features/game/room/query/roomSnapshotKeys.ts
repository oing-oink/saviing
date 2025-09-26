export const roomSnapshotKeys = {
  all: ['roomSnapshot'] as const,
  byRoom: (roomId: number, characterId: number) =>
    [...roomSnapshotKeys.all, roomId, characterId] as const,
};
