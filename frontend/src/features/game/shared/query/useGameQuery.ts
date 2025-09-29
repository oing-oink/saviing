import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getCharacterGameData,
  getCharacterStatistics,
  connectCharacterToAccount,
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
export const useGameQuery = (characterId?: number) => {
  const queryKey =
    typeof characterId === 'number'
      ? gameKeys.characterData(characterId)
      : (['character', 'unknown'] as const);

  return useQuery<CharacterGameData, Error>({
    queryKey,
    queryFn: async () => {
      const result = await getCharacterGameData(characterId as number);

      // 디버깅용 로그 (개발 환경에서만)
      console.log('🚀 useGameQuery API Response - ALWAYS SHOW:', {
        characterId,
        result,
        accountId: result.accountId,
        accountIdType: typeof result.accountId,
        connectionStatus: result.connectionStatus,
        fullResult: result,
      });

      return result;
    },
    enabled: typeof characterId === 'number',
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
export const useCharacterStatistics = (characterId?: number) => {
  return useQuery<CharacterStatistics, Error>({
    queryKey:
      typeof characterId === 'number'
        ? gameKeys.characterStatistics(characterId)
        : (['character', 'unknown', 'statistics'] as const),
    queryFn: () => getCharacterStatistics(characterId as number),
    enabled: typeof characterId === 'number', // characterId가 있을 때만 실행
  });
};

/**
 * 게임 캐릭터와 적금 계좌를 연결하는 React Query mutation 훅
 *
 * 캐릭터와 적금 계좌를 연결한 후 관련 캐시를 무효화하여 최신 상태를 반영합니다.
 *
 * @returns React Query mutation 객체
 */
export const useConnectCharacterToAccount = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      characterId,
      accountId,
    }: {
      characterId: number;
      accountId: number;
    }) => connectCharacterToAccount(characterId, accountId),
    onSuccess: (result, variables) => {
      // 디버깅용 로그 (개발 환경에서만)
      if (process.env.NODE_ENV === 'development') {
        console.log('connectCharacterToAccount Success:', {
          result,
          variables,
          characterId: variables.characterId,
          accountId: variables.accountId,
        });
      }

      // 캐릭터 게임 데이터 캐시 무효화하여 최신 connectionStatus 반영
      queryClient.invalidateQueries({
        queryKey: gameKeys.characterData(variables.characterId),
      });
      // 전체 계좌 목록 캐시 무효화 (업데이트된 이자율 반영)
      queryClient.invalidateQueries({
        queryKey: ['accounts'],
      });
      // 특정 적금 계좌 상세 정보도 캐시 무효화
      queryClient.invalidateQueries({
        queryKey: ['savings'],
      });

      // 디버깅용 로그 (개발 환경에서만)
      if (process.env.NODE_ENV === 'development') {
        console.log('Cache invalidated for:', {
          characterData: gameKeys.characterData(variables.characterId),
          accounts: ['accounts'],
          savings: ['savings'],
        });
      }
    },
  });
};
