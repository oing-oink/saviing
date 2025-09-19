import { Loader2 } from 'lucide-react';
import ProductCard from '@/features/savings/product/components/ProductCard';
import { useProductsQuery } from '@/features/savings/product/query/useProductsQuery';

const ProductsPage = () => {
  const { data: products, isLoading, error } = useProductsQuery();

  // 로딩 상태
  if (isLoading) {
    return (
      <div className="px-5 py-4">
        <div className="flex flex-col items-center gap-4">
          <div className="flex h-32 items-center justify-center">
            <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
            <span className="ml-2 text-sm text-muted-foreground">
              상품 정보를 불러오는 중...
            </span>
          </div>
        </div>
      </div>
    );
  }

  // 에러 상태
  if (error || !products) {
    return (
      <div className="px-5 py-4">
        <div className="flex flex-col items-center gap-4">
          <div className="text-center text-red-500">
            상품 정보를 불러오는 데 실패했습니다.
          </div>
        </div>
      </div>
    );
  }

  // 카테고리별로 상품 분류
  const savingsProducts = products.filter(
    product => product.productCategory === 'INSTALLMENT_SAVINGS'
  );
  const demandProducts = products.filter(
    product => product.productCategory === 'DEMAND_DEPOSIT'
  );

  return (
    <div className="px-5 py-4">
      <div className="flex flex-col gap-6">
        {/* 페이지 제목 */}
        <div>
          <h1 className="text-2xl font-bold text-primary">금융상품</h1>
          <p className="text-sm text-gray-600 mt-1">
            다양한 금융상품을 확인해보세요
          </p>
        </div>

        {/* 적금 상품 섹션 */}
        {savingsProducts.length > 0 && (
          <div className="flex flex-col gap-3">
            <h2 className="text-lg font-semibold text-gray-800">적금</h2>
            <div className="flex flex-col gap-4">
              {savingsProducts.map(product => (
                <ProductCard key={product.productId} product={product} />
              ))}
            </div>
          </div>
        )}

        {/* 입출금 상품 섹션 */}
        {demandProducts.length > 0 && (
          <div className="flex flex-col gap-3">
            <h2 className="text-lg font-semibold text-gray-800">입출금통장</h2>
            <div className="flex flex-col gap-4">
              {demandProducts.map(product => (
                <ProductCard key={product.productId} product={product} />
              ))}
            </div>
          </div>
        )}

        {/* 상품이 없는 경우 */}
        {products.length === 0 && (
          <div className="flex h-32 items-center justify-center text-gray-500">
            등록된 상품이 없습니다.
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductsPage;