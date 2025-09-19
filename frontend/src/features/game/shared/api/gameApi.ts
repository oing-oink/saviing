import { http } from '@/shared/services/api/http';
import type { CharacterGameData } from '@/features/game/shared/types/gameTypes';
import { mockGetCharacterGameData } from '@/features/game/shared/data/mockGameApi';

/**
 * 개발 환경에서 mock 데이터 사용 여부를 결정하는 플래그
 *
 * development 모드에서는 true, production에서는 false로 설정됩니다.
 * 이를 통해 백엔드 API 없이도 프론트엔드 개발을 진행할 수 있습니다.
 */
// const USE_MOCK = import.meta.env.MODE === 'development';
const USE_MOCK = true;

/**
 * 특정 캐릭터의 게임 정보를 조회하는 API 함수
 *
 * 개발 환경에서는 mock 데이터를, 프로덕션 환경에서는 실제 API를 호출합니다.
 * 사용자의 캐릭터 정보, 보유 재화, 방 개수 등 게임 진행에 필요한 정보를 반환합니다.
 *
 * @param characterId - 조회할 캐릭터의 고유 식별자
 * @returns 캐릭터의 게임 정보가 담긴 CharacterGameData 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getCharacterGameData = async (
  characterId: number,
): Promise<CharacterGameData> => {
  if (USE_MOCK) {
    // Mock 데이터 사용
    const mockResponse = await mockGetCharacterGameData();
    return mockResponse.body!;
  }

  // 실제 API 호출
  const response = await http.get<CharacterGameData>(
    `/v1/games/character/${characterId}`,
  );
  return response.body!;
};
