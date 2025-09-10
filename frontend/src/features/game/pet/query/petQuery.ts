import { useQuery } from '@tanstack/react-query';
import { getPet } from '@/features/game/pet/api/petApi';
import type { PetData } from '@/features/game/pet/types/petTypes';

/**
 * Pet 쿼리 키 팩토리
 */
export const petKeys = {
  all: ['pet'] as const,
  detail: (petId: number) => [...petKeys.all, petId] as const,
};

/**
 * Pet 데이터 조회 쿼리 훅
 */
export const usePetQuery = (petId: number) => {
  return useQuery<PetData, Error>({
    queryKey: petKeys.detail(petId),
    queryFn: () => getPet(petId),
    staleTime: 5 * 60 * 1000, // 5분간 fresh
    gcTime: 10 * 60 * 1000, // 10분간 캐시 유지
    retry: 2,
    retryDelay: attemptIndex => Math.min(1000 * 2 ** attemptIndex, 30000),
  });
};
