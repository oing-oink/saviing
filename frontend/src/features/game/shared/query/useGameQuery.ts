import { useQuery } from '@tanstack/react-query';
import {
  getCharacterGameData,
  getCharacterStatistics,
} from '@/features/game/shared/api/gameApi';
import type {
  CharacterGameData,
  CharacterStatistics,
} from '@/features/game/shared/types/gameTypes';
import { gameKeys } from './gameKeys';

/**
 * 캐릭터 게임 데이터를 조회하는 React Query 커스텀 훅
 *
 * 캐릭터의 게임 정보(캐릭터, 재화, 방 개수 등)를 비동기로 가져옵니다.
 * 자동으로 에러 처리, 연결 상태, 재시도 등을 관리하며,
 * React Query의 캐싱 기능을 통해 불필요한 재요청을 방지합니다.
 *
 * @returns React Query 결과 객체
 */
export const useGameQuery = (characterId: number) => {
  return useQuery<CharacterGameData, Error>({
    queryKey: gameKeys.characterData(characterId),
    queryFn: () => getCharacterGameData(characterId),
  });
};

/**
 * 캐릭터 통계 데이터를 조회하는 React Query 커스텀 훅
 *
 * 캐릭터의 이자율 계산 통계(펫 레벨 합, 인벤토리 레어리티 등)를 비동기로 가져옵니다.
 * 자동으로 에러 처리, 연결 상태, 재시도 등을 관리하며,
 * React Query의 캐싱 기능을 통해 불필요한 재요청을 방지합니다.
 *
 * @param characterId - 조회할 캐릭터 ID
 * @returns React Query 결과 객체
 */
export const useCharacterStatistics = (characterId: number) => {
  return useQuery<CharacterStatistics, Error>({
    queryKey: gameKeys.characterStatistics(characterId),
    queryFn: () => getCharacterStatistics(characterId),
    enabled: Boolean(characterId), // characterId가 있을 때만 실행
  });
};
