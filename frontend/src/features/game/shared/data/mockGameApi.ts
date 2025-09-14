import type { ApiSuccessResponse } from '@/shared/types/api';
import type { UserGameData } from '@/features/game/shared/types/gameTypes';

/**
 * 개발 및 테스트용 모의 게임 데이터
 *
 * 실제 API 응답과 동일한 구조의 사용자 게임 정보를 제공합니다.
 */
export const mockUserGameData: UserGameData = {
  characterId: 5,
  customerId: 11,
  coin: 500,
  fishCoin: 600,
  isActive: true,
  roomCount: 3,
  lastAccessAt: '2025-09-10T21:00:00+09:00',
};

/**
 * 사용자 게임 정보 조회 API를 시뮬레이션하는 모의 함수
 *
 * 실제 네트워크 지연을 시뮬레이션하여 실제 API와 유사한 동작을 제공합니다.
 * 개발 환경에서 백엔드 API 없이도 프론트엔드 개발을 진행할 수 있게 해줍니다.
 *
 * @returns Promise로 래핑된 API 성공 응답 객체
 * @throws 네트워크 오류 등은 시뮬레이션하지 않음
 */
export const mockGetUserGameData = async (): Promise<
  ApiSuccessResponse<UserGameData>
> => {
  // 실제 네트워크 지연 시뮬레이션
  await new Promise(resolve => setTimeout(resolve, 300));

  return {
    success: true,
    status: 200,
    body: mockUserGameData,
  };
};
