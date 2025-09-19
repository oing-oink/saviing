import { useQuery } from '@tanstack/react-query';
import { getProducts } from '@/features/savings/product/api/productsApi';
import { productsKeys } from '@/features/savings/product/query/productsKeys';

/**
 * 은행 상품 목록을 조회하는 React Query 훅
 *
 * 상품 정보는 자주 변경되지 않으므로 5분간 stale 상태를 유지하고,
 * 10분간 캐시에 보관합니다.
 *
 * @returns 상품 목록 쿼리 결과 (data, isLoading, error 등)
 */
export const useProductsQuery = () => {
  return useQuery({
    queryKey: productsKeys.list(),
    queryFn: getProducts,
    staleTime: 5 * 60 * 1000, // 5분 - 상품 정보는 자주 변경되지 않음
    gcTime: 10 * 60 * 1000,   // 10분 - 캐시 보관 시간
  });
};