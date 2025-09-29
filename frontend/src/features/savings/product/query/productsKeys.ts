/**
 * Products 관련 React Query 키 팩토리
 *
 * 쿼리 키를 중앙 집중식으로 관리하여 일관성을 보장하고
 * 캐시 무효화 작업을 쉽게 수행할 수 있도록 합니다.
 */
export const productsKeys = {
  /**
   * 모든 products 관련 쿼리의 루트 키
   */
  all: ['products'] as const,

  /**
   * 상품 목록 관련 쿼리 키들
   */
  lists: () => [...productsKeys.all, 'list'] as const,

  /**
   * 상품 목록 조회 쿼리 키
   */
  list: () => [...productsKeys.lists()] as const,

  /**
   * 상품 상세 관련 쿼리 키들
   */
  details: () => [...productsKeys.all, 'detail'] as const,

  /**
   * 특정 상품 상세 조회 쿼리 키
   */
  detail: (productCode: string) =>
    [...productsKeys.details(), productCode] as const,
} as const;
