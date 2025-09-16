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

/**
 * 펫 상호작용 API를 시뮬레이션하는 모의 함수
 *
 * @param petId - 상호작용할 펫의 ID
 * @param request - 상호작용 요청 데이터
 * @returns Promise로 래핑된 API 성공 응답 객체
 */
export const mockPetInteraction = async (
  petId: number,
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

  const consumption =
    request.type === 'feed'
      ? [
          {
            inventoryItemId: 4,
            item_id: 4,
            name: '사료',
            type: 'feed',
            count: 1,
          },
        ]
      : [
          {
            inventoryItemId: 5,
            item_id: 5,
            name: '장난감',
            type: 'play',
            count: 1,
          },
        ];

  // 현재 인벤토리에서 소모된 아이템만큼 차감한 업데이트된 인벤토리
  // 실제로는 서버에서 현재 사용자의 인벤토리 정보를 가져와서 계산해야 함
  const mockCurrentInventory = { feed: 3, toy: 2 }; // TODO: 실제 사용자 인벤토리로 교체
  const updatedInventory =
    request.type === 'feed'
      ? {
          ...mockCurrentInventory,
          feed: Math.max(0, mockCurrentInventory.feed - 1),
        }
      : {
          ...mockCurrentInventory,
          toy: Math.max(0, mockCurrentInventory.toy - 1),
        };

  return {
    success: true,
    status: 200,
    body: {
      pet: updatedPet,
      consumption,
      updatedInventory,
    },
  };
};
