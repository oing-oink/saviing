import type { ApiSuccessResponse } from '@/shared/types/api';
import type { PetData } from '@/features/game/pet/types/petTypes';

export const mockPetData: PetData = {
  petId: 1,
  itemId: 1,
  name: '완두',
  level: 5,
  exp: 50,
  requiredExp: 4000,
  affection: 50,
  maxAffection: 100,
  energy: 50,
  maxEnergy: 100,
  isUsed: true,
  floor: 3,
};

/**
 * Mock API 응답을 시뮬레이션하는 함수
 */
export const mockGetPet = async (): Promise<ApiSuccessResponse<PetData>> => {
  // 실제 네트워크 지연 시뮬레이션
  await new Promise(resolve => setTimeout(resolve, 500));

  return {
    success: true,
    status: 200,
    body: mockPetData,
  };
};
