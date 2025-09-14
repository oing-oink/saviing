import type { ApiSuccessResponse } from '@/shared/types/api';
import type { PetData } from '@/features/game/pet/types/petTypes';

/**
 * 개발 및 테스트용 모의 펫 데이터
 *
 * 실제 API 응답과 동일한 구조의 데이터를 제공합니다.
 */
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
 * 펫 정보 조회 API를 시뮬레이션하는 모의 함수
 *
 * 실제 네트워크 지연을 시뮬레이션하여 실제 API와 유사한 동작을 제공합니다.
 * 개발 환경에서 백엔드 API 없이도 프론트엔드 개발을 진행할 수 있게 해줍니다.
 *
 * @returns Promise로 래핑된 API 성공 응답 객체
 * @throws 네트워크 오류 등은 시뮬레이션하지 않음
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
