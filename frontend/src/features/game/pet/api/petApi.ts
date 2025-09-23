import { http } from '@/shared/services/api/http';
import type {
  PetData,
  PetInteractionRequest,
  PetInteractionResponse,
} from '@/features/game/pet/types/petTypes';
import {
  mockGetPet,
  mockPetInteraction,
  mockRenamePetName,
} from '@/features/game/pet/data/mockPetApi';

/**
 * 개발 환경에서 mock 데이터 사용 여부를 결정하는 플래그
 *
 * development 모드에서는 true, production에서는 false로 설정됩니다.
 */
//const USE_MOCK = import.meta.env.MODE === 'development';

const USE_MOCK = true;

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

/**
 * 펫과 상호작용하는 API 함수
 *
 * 개발 환경에서는 mock 데이터를, 프로덕션 환경에서는 실제 API를 호출합니다.
 * 사료 주기(feed) 또는 놀아주기(play) 상호작용을 수행합니다.
 *
 * @param petId - 상호작용할 펫의 고유 식별자
 * @param request - 상호작용 요청 데이터 (type: 'feed' | 'play')
 * @returns 업데이트된 펫 정보와 소모된 아이템 정보
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const interactWithPet = async (
  petId: number,
  request: PetInteractionRequest,
): Promise<PetInteractionResponse> => {
  if (USE_MOCK) {
    // Mock 데이터 사용
    const mockResponse = await mockPetInteraction(petId, request);
    return mockResponse.body!;
  }

  // 실제 API 호출
  const response = await http.post<PetInteractionResponse>(
    `/v1/game/pets/${petId}/interaction`,
    request,
  );
  return response.body!;
};

/**
 * 펫 이름 변경 API
 * @param petId - 펫 ID
 * @param name - 변경할 이름
 * @returns 업데이트된 펫 정보
 */
export const renamePetName = async (
  petId: number,
  name: string,
): Promise<PetData> => {
  if (USE_MOCK) {
    const mockResponse = await mockRenamePetName(petId, name);
    return mockResponse.body!;
  }

  const response = await http.patch<PetData>(`/v1/game/pets/${petId}/name`, {
    name,
  });
  return response.body!;
};
