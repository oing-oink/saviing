import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Wallet, Landmark, ChevronDown, ChevronUp } from 'lucide-react';
import { Card } from '@/shared/components/ui/card';
import { Badge } from '@/shared/components/ui/badge';
import { Button } from '@/shared/components/ui/button';
import type { Product } from '@/features/savings/product/types/productTypes';
import {
  getCategoryLabel,
  getCategoryColorClass,
  formatInterestRateRange,
  formatInterestRate,
  getCompoundingTypeLabel,
  getPaymentCycleLabel,
  getTermUnitLabel,
  formatAmount,
} from '@/features/savings/product/types/productTypes';
import { useProductDetail } from '@/features/savings/product/query/useProductsQuery';
import { PAGE_PATH } from '@/shared/constants/path';
import { ACCOUNT_TYPES } from '@/features/savings/constants/accountTypes';

interface ProductCardProps {
  product: Product;
}

const ProductCard = ({ product }: ProductCardProps) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const navigate = useNavigate();

  const { data: productDetail, isLoading: isDetailLoading } = useProductDetail(
    isExpanded ? product.productCode : undefined,
  );

  const handleCardClick = () => {
    setIsExpanded(!isExpanded);
  };

  const handleAccountCreation = () => {
    const accountType =
      product.productCategory === 'INSTALLMENT_SAVINGS'
        ? ACCOUNT_TYPES.SAVINGS
        : ACCOUNT_TYPES.CHECKING;

    navigate(`${PAGE_PATH.ACCOUNT_CREATION}/start?type=${accountType}&from=products`);
  };

  const getAccountCreationButton = () => {
    if (product.productCategory === 'INSTALLMENT_SAVINGS') {
      return {
        icon: Landmark,
        label: '자유적금 개설',
      };
    } else {
      return {
        icon: Wallet,
        label: '입출금계좌 개설',
      };
    }
  };

  const buttonConfig = getAccountCreationButton();
  const ButtonIcon = buttonConfig.icon;

  return (
    <Card className="p-0 shadow transition-all duration-300">
      <div className="flex flex-col">
        {/* 기본 상품 정보 (클릭 가능) */}
        <div
          className="cursor-pointer p-6 transition-colors"
          onClick={handleCardClick}
        >
          <div className="flex flex-col gap-4">
            {/* 상품명과 카테고리 */}
            <div className="flex items-start justify-between">
              <h3 className="text-xl font-bold text-primary">
                {product.productName}
              </h3>
              <div className="flex items-center gap-2">
                <Badge
                  className={getCategoryColorClass(product.productCategory)}
                >
                  {getCategoryLabel(product.productCategory)}
                </Badge>
                {isExpanded ? (
                  <ChevronUp className="h-5 w-5 text-gray-400" />
                ) : (
                  <ChevronDown className="h-5 w-5 text-gray-400" />
                )}
              </div>
            </div>

            {/* 상품 설명 */}
            <p className="text-sm leading-relaxed text-gray-600">
              {product.description}
            </p>

            {/* 금리 정보 */}
            <div className="flex items-center justify-between rounded-lg bg-violet-50 p-3">
              <span className="text-sm font-medium text-gray-700">
                연이자율
              </span>
              <span className="text-lg font-bold text-primary">
                {formatInterestRateRange(
                  product.minInterestRateBps,
                  product.maxInterestRateBps,
                )}
              </span>
            </div>
          </div>
        </div>

        {/* 확장된 상세 정보 */}
        {isExpanded && (
          <div className="border-t border-gray-100 p-6 pt-4">
            {isDetailLoading ? (
              <div className="flex items-center justify-center py-6">
                <div className="h-6 w-6 animate-spin rounded-full border-2 border-primary border-t-transparent"></div>
                <span className="ml-2 text-sm text-gray-500">
                  상세 정보 로딩 중...
                </span>
              </div>
            ) : productDetail ? (
              <div className="space-y-4">
                {/* 상세 금리 정보 */}
                <div className="rounded-lg bg-gray-50 p-4">
                  <h4 className="mb-3 font-semibold text-gray-800">
                    상세 금리 정보
                  </h4>
                  <div className="grid grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="text-gray-600">금리 범위:</span>
                      <span className="ml-2 font-medium">
                        {formatInterestRate(productDetail.interestRate)}
                      </span>
                    </div>
                    <div>
                      <span className="text-gray-600">복리 계산:</span>
                      <span className="ml-2 font-medium">
                        {getCompoundingTypeLabel(productDetail.compoundingType)}
                      </span>
                    </div>
                  </div>
                </div>

                {/* 적금 상품 상세 정보 */}
                {productDetail.savingsConfig && (
                  <div className="rounded-lg bg-green-50 p-4">
                    <h4 className="mb-3 font-semibold text-gray-800">
                      적금 상품 조건
                    </h4>
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span className="text-gray-600">기본 납입 주기:</span>
                        <span className="font-medium">
                          {getPaymentCycleLabel(
                            productDetail.savingsConfig.defaultPaymentCycle,
                          )}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">최소 납입금액:</span>
                        <span className="font-medium">
                          {formatAmount(
                            productDetail.savingsConfig.minPaymentAmount,
                          )}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">최대 납입금액:</span>
                        <span className="font-medium">
                          {formatAmount(
                            productDetail.savingsConfig.maxPaymentAmount,
                          )}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">가입 기간:</span>
                        <span className="font-medium">
                          {productDetail.savingsConfig.termConstraints.minValue}
                          {getTermUnitLabel(
                            productDetail.savingsConfig.termConstraints.unit,
                          )}{' '}
                          ~{' '}
                          {productDetail.savingsConfig.termConstraints.maxValue}
                          {getTermUnitLabel(
                            productDetail.savingsConfig.termConstraints.unit,
                          )}
                        </span>
                      </div>
                    </div>
                  </div>
                )}

                {/* 입출금 상품 상세 정보 */}
                {productDetail.demandDepositConfig && (
                  <div className="rounded-lg bg-blue-50 p-4">
                    <h4 className="mb-3 font-semibold text-gray-800">
                      입출금 상품 조건
                    </h4>
                    <div className="text-sm">
                      <div className="flex justify-between">
                        <span className="text-gray-600">최소 잔고:</span>
                        <span className="font-medium">
                          {formatAmount(
                            productDetail.demandDepositConfig.minimumBalance,
                          )}
                        </span>
                      </div>
                    </div>
                  </div>
                )}

                {/* 계좌 개설 버튼 */}
                <Button
                  onClick={handleAccountCreation}
                  className="w-full bg-primary text-white"
                >
                  <ButtonIcon className="mr-2 h-4 w-4" />
                  {buttonConfig.label}
                </Button>
              </div>
            ) : (
              <div className="py-6 text-center text-gray-500">
                상세 정보를 불러올 수 없습니다.
              </div>
            )}
          </div>
        )}
      </div>
    </Card>
  );
};

export default ProductCard;
