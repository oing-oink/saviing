// API 응답 타입
export interface PetApiResponse {
  success: boolean;
  status: number;
  body: {
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
    isUsed: boolean;
    floor: number;
  };
}

// UI에서 사용할 Pet 데이터 타입
export interface PetData {
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
  isUsed: boolean;
  floor: number;
}

// 기본값
export const defaultPetData: PetData = {
  petId: 1,
  itemId: 1,
  name: '검정냥이',
  level: 1,
  exp: 0,
  requiredExp: 100,
  affection: 0,
  maxAffection: 100,
  energy: 0,
  maxEnergy: 100,
  isUsed: false,
  floor: 1,
};

// 인벤토리 타입
export interface PetInventory {
  feed: number;
  toy: number;
}

export const defaultInventory: PetInventory = {
  feed: 0,
  toy: 0,
};