import { getRoomPlacements } from '@/features/game/room/api/roomApi';
import type { RoomPlacementItem } from '@/features/game/room/api/roomApi';
import { getInventoryItems } from '@/features/game/shop/api/itemsApi';
import type { Item } from '@/features/game/shop/types/item';

export interface FetchRoomSnapshotParams {
  roomId: number;
  characterId: number;
}

export interface RoomSnapshotResult {
  roomId: number;
  characterId: number;
  placements: RoomPlacementItem[];
  inventoryItems: Item[];
}

export const fetchRoomSnapshot = async ({
  roomId,
  characterId,
}: FetchRoomSnapshotParams): Promise<RoomSnapshotResult> => {
  const [placementsResponse, inventoryResponse] = await Promise.all([
    getRoomPlacements(roomId),
    getInventoryItems(characterId),
  ]);

  return {
    roomId,
    characterId,
    placements: placementsResponse.placements,
    inventoryItems: inventoryResponse.items ?? [],
  };
};
