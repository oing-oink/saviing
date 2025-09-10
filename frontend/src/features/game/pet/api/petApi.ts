import { api } from '@/shared/services/api/axiosClient';
import type {
  PetApiResponse,
  PetData,
} from '@/features/game/pet/types/petTypes';
import { defaultPetData } from '@/features/game/pet/types/petTypes';

// Mock 데이터 (실제 API 연결 전까지 사용)
const mockPetApiResponse: PetApiResponse = {
  success: true,
  status: 200,
  body: {
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
  },
};

// 개발 모드에서는 mock 데이터 사용 여부 결정
const USE_MOCK = import.meta.env.MODE === 'development';

/**
 * 펫 정보 조회 API
 */
export const getPet = async (petId: number): Promise<PetData> => {
  try {
    if (USE_MOCK) {
      // Mock 데이터 반환 (실제 네트워크 지연 시뮬레이션)
      await new Promise(resolve => setTimeout(resolve, 500));
      return mockPetApiResponse.body;
    }

    // 실제 API 호출
    const response = await api.get<PetApiResponse>(`/v1/game/pets/${petId}`);

    if (!response.data.success) {
      throw new Error('Failed to fetch pet data');
    }

    return response.data.body;
  } catch (error) {
    console.error('Error fetching pet data:', error);
    // 에러 발생 시 기본값 반환
    return defaultPetData;
  }
};
