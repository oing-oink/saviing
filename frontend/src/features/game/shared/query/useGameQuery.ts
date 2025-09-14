import { useQuery } from '@tanstack/react-query';
import { getUserGameData } from '@/features/game/shared/api/gameApi';
import type { UserGameData } from '@/features/game/shared/types/gameTypes';
import { gameKeys } from './gameKeys';

/**
 * 사용자 게임 데이터를 조회하는 React Query 커스텀 훅
 *
 * 사용자의 게임 정보(캐릭터, 재화, 방 개수 등)를 비동기로 가져옵니다.
 * 자동으로 에러 처리, 연결 상태, 재시도 등을 관리하며,
 * React Query의 캐싱 기능을 통해 불필요한 재요청을 방지합니다.
 *
 * @returns React Query 결과 객체
 */
export const useGameQuery = (characterId: number) => {
  return useQuery<UserGameData, Error>({
    queryKey: gameKeys.characterData(characterId),
    queryFn: () => getUserGameData(characterId),
  });
};
