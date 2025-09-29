import { useQuery } from '@tanstack/react-query';
import { getGachaInfo } from '@/features/game/shop/api/itemsApi';
import { gachaKeys } from './gachaKeys';

export const useGachaInfo = () => {
  return useQuery({
    queryKey: gachaKeys.info(),
    queryFn: getGachaInfo,
    staleTime: 5 * 60 * 1000, // 5분
    gcTime: 10 * 60 * 1000, // 10분
  });
};
