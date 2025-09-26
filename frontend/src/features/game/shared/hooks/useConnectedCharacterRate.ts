import { useGameQuery } from '@/features/game/shared/query/useGameQuery';
import { useCalculatedInterestRate } from './useCalculatedInterestRate';
import { useGameEntryQuery } from '@/features/game/entry/query/useGameEntryQuery';

/**
 * 적금 계좌와 연결된 캐릭터의 계산된 이자율을 반환하는 훅
 *
 * 현재 사용자의 캐릭터가 특정 적금 계좌와 연결되어 있는지 확인하고,
 * 연결되어 있다면 해당 캐릭터의 계산된 이자율을 반환합니다.
 *
 * @param accountId - 적금 계좌 ID
 * @returns 연결된 캐릭터의 계산된 이자율 또는 null
 */
export const useConnectedCharacterRate = (accountId?: number) => {
  const { data: gameEntry } = useGameEntryQuery();
  const characterId = gameEntry?.characterId;

  // 현재 캐릭터의 게임 데이터 조회
  const { data: gameData } = useGameQuery(characterId);

  // 현재 캐릭터가 해당 적금 계좌와 연결되어 있는지 확인
  const isConnected =
    gameData?.connectionStatus === 'CONNECTED' &&
    gameData?.accountId === accountId;

  // 추가 비교 디버깅
  if (process.env.NODE_ENV === 'development' && accountId && gameData) {
    console.log('🔍 Connection Comparison Debug:', {
      requestedAccountId: accountId,
      requestedAccountIdType: typeof accountId,
      gameDataAccountId: gameData?.accountId,
      gameDataAccountIdType: typeof gameData?.accountId,
      connectionStatus: gameData?.connectionStatus,
      strictEquals: gameData?.accountId === accountId,
      looseEquals: gameData?.accountId == accountId,
      bothNumbers:
        typeof accountId === 'number' &&
        typeof gameData?.accountId === 'number',
      isConnected,
    });
  }

  // 연결되어 있다면 계산된 이자율 반환
  const { finalRate, isLoading, error } = useCalculatedInterestRate(
    isConnected ? characterId : undefined,
  );

  // 디버깅용 로그 (개발 환경에서만)
  console.log('🔥 useConnectedCharacterRate Debug - ALWAYS SHOW:', {
    accountId,
    characterId,
    gameDataAccountId: gameData?.accountId,
    connectionStatus: gameData?.connectionStatus,
    isConnected,
    finalRate,
    isLoading,
    error,
    // gameData 전체 구조 확인
    fullGameData: gameData,
  });

  return {
    calculatedRate: isConnected ? finalRate : null,
    isConnected,
    isLoading,
    error,
  };
};
