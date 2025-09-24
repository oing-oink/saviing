import { http } from '@/shared/services/api/http';
import type { GameEntryData } from '@/features/game/entry/types/gameEntryTypes';

/**
 * 홈 진입 시 필요한 캐릭터/방/펫 정보를 조회한다.
 *
 * @returns 현재 세션의 게임 엔트리 데이터
 */
export const getGameEntry = async (): Promise<GameEntryData> => {
  const response = await http.get<GameEntryData>('/v1/game/entry');
  return response.body!;
};
