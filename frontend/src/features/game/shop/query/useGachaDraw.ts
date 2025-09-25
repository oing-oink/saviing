import { useMutation } from '@tanstack/react-query';
import { drawGacha } from '@/features/game/shop/api/itemsApi';
import type {
  GachaDrawRequest,
  GachaDrawResponse,
} from '@/features/game/shop/types/item';

export const useGachaDraw = () => {
  return useMutation<GachaDrawResponse, Error, GachaDrawRequest>({
    mutationFn: drawGacha,
  });
};
