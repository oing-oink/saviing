import { useMutation, useQueryClient } from '@tanstack/react-query';
import { purchaseItem } from '@/features/game/shop/api/itemsApi';
import type { PurchaseRequest } from '@/features/game/shop/types/item';
import { gameKeys } from '@/features/game/shared/query/gameKeys';

/**
 * 아이템 구매를 위한 React Query Mutation 훅
 *
 * 구매 성공 시 게임 데이터를 다시 불러와 코인 잔액을 업데이트합니다.
 *
 * @returns useMutation 결과 객체
 */
export const usePurchase = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (purchaseData: PurchaseRequest) => purchaseItem(purchaseData),
    onSuccess: (data, variables) => {
      // 구매 성공 시 게임 데이터 캐시 무효화하여 최신 코인 잔액 가져오기
      queryClient.invalidateQueries({
        queryKey: gameKeys.characterData(variables.characterId),
      });

      console.log('구매 성공:', data.message);
    },
    onError: error => {
      console.error('구매 실패:', error);
    },
  });
};
