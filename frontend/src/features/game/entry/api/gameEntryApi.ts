import { http } from '@/shared/services/api/http';
import type {
  GameEntryData,
  GameCharacterData,
} from '@/features/game/entry/types/gameEntryTypes';

/**
 * 홈 진입 시 필요한 캐릭터/방/펫 정보를 조회한다.
 *
 * @returns 현재 세션의 게임 엔트리 데이터
 */
export const getGameEntry = async (): Promise<GameEntryData> => {
  const response = await http.get<GameEntryData>('/v1/game/entry');
  return response.body!;
};

/**
 * 게임 캐릭터를 생성한다.
 *
 * @returns 생성된 캐릭터 정보
 */
export const createGameCharacter = async (
  customerId: number,
): Promise<GameCharacterData> => {
  const response = await http.post<GameCharacterData>('/v1/game/characters', {
    customerId,
  });
  return response.body!;
};
