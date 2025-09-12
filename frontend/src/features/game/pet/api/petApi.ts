import { http } from '@/shared/services/api/http';
import type { PetData } from '@/features/game/pet/types/petTypes';
import { mockGetPet } from '@/features/game/pet/data/mockPetApi';

// 개발 모드에서는 mock 데이터 사용 여부 결정
const USE_MOCK = import.meta.env.MODE === 'development';

/**
 * 펫 정보 조회 API
 */
export const getPet = async (petId: number): Promise<PetData> => {
  if (USE_MOCK) {
    // Mock 데이터 사용
    const mockResponse = await mockGetPet();
    return mockResponse.body!;
  }

  // 실제 API 호출
  const response = await http.get<PetData>(`/v1/game/pets/${petId}`);
  return response.body!;
};
