import { http } from '@/shared/services/api/http';

/** 방에 배치 가능한 아이템의 기본 정보. */
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

/** 방에 배치된 아이템 정보를 담는 서버 응답 구조. */
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

/** 방 안의 펫이 장착한 장비 정보를 표현. */
export interface RoomPetEquipmentResponse {
  inventoryItemId: number;
  itemInfo: RoomItemInfo;
}

/** 방 안에 존재하는 펫의 상태와 장비 목록. */
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

/** 방 상세 조회 응답 전체 구조. */
export interface RoomDetailResponse {
  characterId: number;
  roomId: number;
  roomNumber: number;
  placedItems: RoomPlacedItemResponse[];
  pets: RoomPetResponse[];
}

/** 방 배치 정보 단일 아이템. */
export interface RoomPlacementItem {
  placementId: number;
  inventoryItemId: number;
  itemId: number; // 아이템 마스터 데이터 ID 추가
  positionX: number;
  positionY: number;
  category: string;
}

/** 방 배치 정보 응답 구조. */
export interface RoomPlacementsResponse {
  roomId: number;
  placements: RoomPlacementItem[];
}

/** 방 상세 정보를 조회한다. */
export const getRoomDetail = async (): Promise<RoomDetailResponse> => {
  const response = await http.get<RoomDetailResponse>('/v1/game/room');
  if (!response.body) {
    throw new Error('방 정보를 불러오지 못했습니다.');
  }
  return response.body;
};

/** 방 배치 저장 요청 아이템. */
export interface SaveRoomPlacementItem {
  roomId: number;
  inventoryItemId: number;
  itemId: number;
  positionX: number;
  positionY: number;
  xLength: number;
  yLength: number;
  category: string;
}

/** 방 배치 저장 요청 구조. */
export interface SaveRoomPlacementsRequest {
  characterId: number;
  placedItems: SaveRoomPlacementItem[];
}

/** 특정 방의 배치 정보를 조회한다. */
export const getRoomPlacements = async (
  roomId: number,
): Promise<RoomPlacementsResponse> => {
  const response = await http.get<RoomPlacementsResponse>(
    `/v1/game/rooms/${roomId}/placements`,
  );
  if (!response.body) {
    throw new Error('방 배치 정보를 불러오지 못했습니다.');
  }
  return response.body;
};

/** 특정 방의 배치 정보를 저장한다. */
export const saveRoomPlacements = async (
  roomId: number,
  payload: SaveRoomPlacementsRequest,
): Promise<void> => {
  const response = await http.put(
    `/v1/game/rooms/${roomId}/placements`,
    payload,
  );
  if (!response.success) {
    throw new Error('방 배치 정보를 저장하지 못했습니다.');
  }
};
