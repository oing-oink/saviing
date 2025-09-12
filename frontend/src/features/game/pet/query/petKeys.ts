/**
 * Pet 관련 React Query 쿼리 키를 생성하는 객체
 * ```
 */
export const petKeys = {
  /**
   * 모든 펫 관련 쿼리의 기본 키
   * @returns ['pet'] - 펫 관련 쿼리의 루트 키
   */
  all: ['pet'] as const,

  /**
   * 특정 펫의 상세 정보 쿼리 키 생성
   * @param petId - 펫의 고유 식별자
   * @returns ['pet', petId] - 특정 펫의 쿼리 키
   */
  detail: (petId: number) => [...petKeys.all, petId] as const,
};
