import { useQuery } from '@tanstack/react-query';
import { getPet } from '@/features/game/pet/api/petApi';
import type { PetData } from '@/features/game/pet/types/petTypes';
import { petKeys } from './petKeys';

/**
 * 특정 펫의 데이터를 조회하는 React Query 커스텀 훅
 *
 * 펫의 상세 정보(레벨, 경험치, 애정도, 포만감 등)를 비동기로 가져옵니다.
 * 자동으로 에러 처리, 연결 상태, 재시도 등을 관리합니다.
 *
 * @param petId - 조회할 펫의 고유 식별자
 * @returns React Query 결과 객체
 */
export const usePetQuery = (petId: number) => {
  return useQuery<PetData, Error>({
    queryKey: petKeys.detail(petId),
    queryFn: () => getPet(petId),
  });
};
