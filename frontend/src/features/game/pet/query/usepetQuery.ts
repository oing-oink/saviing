import { useQuery } from '@tanstack/react-query';
import { getPet } from '@/features/game/pet/api/petApi';
import type { PetData } from '@/features/game/pet/types/petTypes';
import { petKeys } from './petKeys';

/**
 * Pet 데이터 조회 쿼리 훅
 */
export const usePetQuery = (petId: number) => {
  return useQuery<PetData, Error>({
    queryKey: petKeys.detail(petId),
    queryFn: () => getPet(petId),
  });
};
