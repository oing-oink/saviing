/**
 * Game 관련 React Query 쿼리 키를 생성하는 객체
 * 
 * React Query에서 사용되는 키들을 일관성 있게 관리하고,
 * 캐시 무효화 및 쿼리 식별을 위한 표준화된 키 구조를 제공합니다.
 */
export const gameKeys = {
  /**
   * 캐릭터 게임 데이터 쿼리 키
   * @param characterId - 캐릭터 ID
   * @returns ['character', characterId] - 특정 캐릭터의 게임 정보 쿼리 키
   */
  characterData: (characterId: number) => ['character', characterId] as const,
};