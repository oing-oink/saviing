import { http } from '@/shared/services/api/http';
import type {
  Product,
  ProductDetail,
} from '@/features/savings/product/types/productTypes';

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

/**
 * 특정 은행 상품의 상세 정보를 조회하는 API 함수
 *
 * 상품 코드를 기반으로 해당 상품의 상세 정보를 반환합니다.
 * 금리, 적금/입출금 설정, 조건 등의 상세 정보가 포함됩니다.
 *
 * @param productCode - 조회할 상품의 고유 코드
 * @returns 상품의 상세 정보가 담긴 ProductDetail 객체
 * @throws API 호출 실패 시 네트워크 오류 또는 HTTP 오류 발생
 */
export const getProductDetail = async (
  productCode: string,
): Promise<ProductDetail> => {
  const response = await http.get<ProductDetail>(`/v1/products/${productCode}`);
  return response.body!;
};
