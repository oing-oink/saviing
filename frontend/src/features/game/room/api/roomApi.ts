import { http } from '@/shared/services/api/http';

export interface RoomItemInfo {
  itemId: number;
  name: string;
  description: string;
  type: string;
  category: string;
  image?: string;
  rarity: string;
  xLength?: number;
  yLength?: number;
}

export interface RoomPlacedItemResponse {
  inventoryItemId: number;
  positionX: number;
  positionY: number;
  xLength: number;
  yLength: number;
  rotation?: 0 | 90 | 180 | 270;
  layer?: string;
  itemInfo: RoomItemInfo;
}

export interface RoomPetEquipmentResponse {
  inventoryItemId: number;
  itemInfo: RoomItemInfo;
}

export interface RoomPetResponse {
  petId: number;
  itemId: number;
  name: string;
  level: number;
  exp: number;
  requiredExp: number;
  affection: number;
  maxAffection: number;
  energy: number;
  maxEnergy: number;
  equipments: RoomPetEquipmentResponse[];
}

export interface RoomDetailResponse {
  characterId: number;
  roomId: number;
  roomNumber: number;
  placedItems: RoomPlacedItemResponse[];
  pets: RoomPetResponse[];
}

export const getRoomDetail = async (): Promise<RoomDetailResponse> => {
  const response = await http.get<RoomDetailResponse>('/v1/game/room');
  if (!response.body) {
    throw new Error('방 정보를 불러오지 못했습니다.');
  }
  return response.body;
};
