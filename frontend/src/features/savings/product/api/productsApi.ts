import { http } from '@/shared/services/api/http';
import type { Product } from '@/features/savings/product/types/productTypes';

/**
 * 은행 상품 목록을 조회하는 API 함수
 *
 * 모든 은행 상품(적금, 입출금통장)의 목록을 반환합니다.
 * 상품명, 설명, 금리 정보 등이 포함됩니다.
 *
 * @returns 은행 상품 목록이 담긴 Product 배열
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getProducts = async (): Promise<Product[]> => {
  const response = await http.get<Product[]>('/v1/products');
  return response.body!;
};