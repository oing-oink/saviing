import { useMutation, useQueryClient } from '@tanstack/react-query';
import { interactWithPet } from '@/features/game/pet/api/petApi';
import { petKeys } from '@/features/game/pet/query/petKeys';
import { usePetStore } from '@/features/game/pet/store/usePetStore';
import type { PetInteractionRequest } from '@/features/game/pet/types/petTypes';

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

      // 서버 응답의 업데이트된 인벤토리로 동기화 (서버 진실 기반)
      setInventory(data.updatedInventory);

      // 상호작용 타입에 따른 애니메이션 설정
      if (variables.type === 'feed') {
        // 사료를 주면 sitting 애니메이션
        setBehavior({
          currentAnimation: 'sitting',
        });
      } else if (variables.type === 'play') {
        // 놀아주면 jump 애니메이션
        setBehavior({
          currentAnimation: 'jump',
        });
      }
    },
    onError: error => {
      console.error('펫 상호작용 실패:', error);
    },
  });
};
