import { http } from '@/shared/services/api/http';
import type { PetData } from '@/features/game/pet/types/petTypes';
import { mockGetPet } from '@/features/game/pet/data/mockPetApi';

/**
 * 개발 환경에서 mock 데이터 사용 여부를 결정하는 플래그
 *
 * development 모드에서는 true, production에서는 false로 설정됩니다.
 */
const USE_MOCK = import.meta.env.MODE === 'development';

/**
 * 특정 펫의 상세 정보를 조회하는 API 함수
 *
 * 개발 환경에서는 mock 데이터를, 프로덕션 환경에서는 실제 API를 호출합니다.
 * 펫의 레벨, 경험치, 애정도, 포만감 등 모든 상태 정보를 반환합니다.
 *
 * @param petId - 조회할 펫의 고유 식별자
 * @returns 펫의 상세 정보가 담긴 PetData 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
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
