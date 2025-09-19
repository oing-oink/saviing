import { Card } from '@/shared/components/ui/card';
import { Badge } from '@/shared/components/ui/badge';
import type { Product } from '@/features/savings/product/types/productTypes';
import {
  getCategoryLabel,
  getCategoryColorClass,
  formatInterestRateRange,
} from '@/features/savings/product/types/productTypes';

interface ProductCardProps {
  product: Product;
}

const ProductCard = ({ product }: ProductCardProps) => {
  return (
    <Card className="saving w-full rounded-2xl bg-white p-6 shadow">
      <div className="flex flex-col gap-4">
        {/* 상품명과 카테고리 */}
        <div className="flex items-start justify-between">
          <h3 className="text-xl font-bold text-primary">{product.productName}</h3>
          <Badge className={getCategoryColorClass(product.productCategory)}>
            {getCategoryLabel(product.productCategory)}
          </Badge>
        </div>

        {/* 상품 설명 */}
        <p className="text-sm text-gray-600 leading-relaxed">
          {product.description}
        </p>

        {/* 금리 정보 */}
        <div className="flex items-center justify-between rounded-lg bg-violet-50 p-3">
          <span className="text-sm font-medium text-gray-700">연이자율</span>
          <span className="text-lg font-bold text-primary">
            {formatInterestRateRange(product.minInterestRateBps, product.maxInterestRateBps)}
          </span>
        </div>

        {/* 상품 코드 */}
        <div className="text-xs text-gray-400">
          상품코드: {product.productCode}
        </div>
      </div>
    </Card>
  );
};

export default ProductCard;