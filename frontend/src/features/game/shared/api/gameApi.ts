import { http } from '@/shared/services/api/http';
import type { UserGameData } from '@/features/game/shared/types/gameTypes';
import { mockGetUserGameData } from '@/features/game/shared/data/mockGameApi';

/**
 * 개발 환경에서 mock 데이터 사용 여부를 결정하는 플래그
 *
 * development 모드에서는 true, production에서는 false로 설정됩니다.
 * 이를 통해 백엔드 API 없이도 프론트엔드 개발을 진행할 수 있습니다.
 */
const USE_MOCK = import.meta.env.MODE === 'development';

/**
 * 특정 캐릭터의 게임 정보를 조회하는 API 함수
 *
 * 개발 환경에서는 mock 데이터를, 프로덕션 환경에서는 실제 API를 호출합니다.
 * 사용자의 캐릭터 정보, 보유 재화, 방 개수 등 게임 진행에 필요한 정보를 반환합니다.
 *
 * @param characterId - 조회할 캐릭터의 고유 식별자
 * @returns 사용자의 게임 정보가 담긴 UserGameData 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getUserGameData = async (
  characterId: number,
): Promise<UserGameData> => {
  if (USE_MOCK) {
    // Mock 데이터 사용
    const mockResponse = await mockGetUserGameData();
    return mockResponse.body!;
  }

  // 실제 API 호출
  const response = await http.get<UserGameData>(
    `/v1/games/character/${characterId}`,
  );
  return response.body!;
};
