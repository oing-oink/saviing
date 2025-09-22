import type { ApiSuccessResponse } from '@/shared/types/api';
import type {
  PetData,
  PetInteractionResponse,
  PetInteractionRequest,
} from '@/features/game/pet/types/petTypes';

/**
 * 개발 및 테스트용 모의 펫 데이터
 *
 * 실제 API 응답과 동일한 구조의 데이터를 제공합니다.
 */
export const mockPetData: PetData = {
  petId: 9,
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

/**
 * 펫 상호작용 API를 시뮬레이션하는 모의 함수
 *
 * @param petId - 상호작용할 펫의 ID
 * @param request - 상호작용 요청 데이터
 * @returns Promise로 래핑된 API 성공 응답 객체
 */
export const mockPetInteraction = async (
  _petId: number,
  request: PetInteractionRequest,
): Promise<ApiSuccessResponse<PetInteractionResponse>> => {
  // 실제 네트워크 지연 시뮬레이션
  await new Promise(resolve => setTimeout(resolve, 300));

  const updatedPet: PetData = {
    ...mockPetData,
    // 상호작용에 따른 능력치 변화 시뮬레이션
    affection: Math.min(mockPetData.maxAffection, mockPetData.affection + 10),
    energy:
      request.type === 'feed'
        ? Math.min(mockPetData.maxEnergy, mockPetData.energy + 20)
        : Math.max(0, mockPetData.energy - 10),
    exp: mockPetData.exp + 5,
  };

  // 사용자가 보유한 모든 펫 관련 아이템 (사용 후 남은 개수)
  const mockCurrentInventory = { feed: 3, toy: 2 };

  const consumption = [
    {
      inventoryItemId: 4,
      item_id: 4,
      name: '사료',
      type: 'feed',
      count:
        request.type === 'feed'
          ? Math.max(0, mockCurrentInventory.feed - 1) // 사료 사용 시 -1
          : mockCurrentInventory.feed, // 놀이 시 그대로
    },
    {
      inventoryItemId: 5,
      item_id: 5,
      name: '장난감',
      type: 'play',
      count:
        request.type === 'play'
          ? Math.max(0, mockCurrentInventory.toy - 1) // 놀이 시 -1
          : mockCurrentInventory.toy, // 사료 시 그대로
    },
  ];

  return {
    success: true,
    status: 200,
    body: {
      pet: updatedPet,
      consumption,
    },
  };
};

/**
 * 펫 이름 변경 모의 API
 */
export const mockRenamePetName = async (
  _petId: number,
  name: string,
): Promise<ApiSuccessResponse<PetData>> => {
  await new Promise(resolve => setTimeout(resolve, 300));

  // mock 상태 업데이트
  Object.assign(mockPetData, { name });

  return {
    success: true,
    status: 200,
    body: { ...mockPetData },
  };
};
