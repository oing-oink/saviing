import { useMutation, useQueryClient } from '@tanstack/react-query';
import { interactWithPet } from '@/features/game/pet/api/petApi';
import { petKeys } from '@/features/game/pet/query/petKeys';
import { usePetStore } from '@/features/game/pet/store/usePetStore';
import type {
  PetInteractionRequest,
  PetInteractionType,
} from '@/features/game/pet/types/petTypes';
import { itemsKeys } from '@/features/game/shop/query/itemsKeys';

/**
 * 펫 상호작용(사료 주기, 놀아주기) 기능을 제공하는 커스텀 훅
 *
 * @param petId - 상호작용할 펫의 ID
 * @returns 상호작용 뮤테이션 함수와 상태
 */
export const usePetInteraction = (petId: number) => {
  const queryClient = useQueryClient();
  const { setBehavior, setInventory } = usePetStore();

  return useMutation({
    mutationKey: petKeys.interaction(petId),
    mutationFn: (request: PetInteractionRequest) =>
      interactWithPet(petId, request),
    onSuccess: (data, variables) => {
      // 펫 데이터 캐시 업데이트
      queryClient.setQueryData(petKeys.detail(petId), data.pet);
      queryClient.invalidateQueries({ queryKey: itemsKeys.inventory() });

      const currentInventory = usePetStore.getState().inventory;
      const nextInventory = {
        ...currentInventory,
        items: currentInventory.items.map(item => ({ ...item })),
      };
      (data.consumption ?? []).forEach(item => {
        if (item.type === 'FOOD') {
          nextInventory.feed = item.remaining;
        }
        if (item.type === 'TOY') {
          nextInventory.toy = item.remaining;
        }
        const targetIndex = nextInventory.items.findIndex(
          consumable => consumable.inventoryItemId === item.inventoryItemId,
        );
        if (targetIndex !== -1) {
          nextInventory.items[targetIndex] = {
            ...nextInventory.items[targetIndex],
            count: item.remaining,
          };
        }
      });
      setInventory(nextInventory);

      // 상호작용 타입에 따른 애니메이션 설정
      const interactionType: PetInteractionType = variables.type;
      if (interactionType === 'FEED') {
        setBehavior({ currentAnimation: 'sitting' });
      } else if (interactionType === 'PLAY') {
        setBehavior({ currentAnimation: 'jump' });
      }
    },
    onError: error => {
      let message = '펫 상호작용에 실패했습니다.';

      const apiError = error as {
        response?: {
          data?: { code?: string; message?: string };
          code?: string;
          message?: string;
        };
        code?: string;
        message?: string;
      };

      const errorCode =
        apiError.response?.data?.code || apiError.code || apiError.response?.code;

      if (errorCode === 'PET_INSUFFICIENT_ENERGY') {
        message = '펫이 배고파서 놀 수 없습니다.';
      } else if (apiError.response?.data?.message) {
        message = apiError.response.data.message;
      } else if (apiError.message) {
        message = apiError.message;
      }

      const { setBehavior, showErrorDialog } = usePetStore.getState();
      setBehavior({ currentAnimation: 'idle' });
      showErrorDialog(message);
    },
  });
};
