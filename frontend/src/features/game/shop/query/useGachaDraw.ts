import { useMutation } from '@tanstack/react-query';
import { drawGacha } from '@/features/game/shop/api/itemsApi';
import type { GachaDrawRequest } from '@/features/game/shop/types/item';

export const useGachaDraw = () => {
  return useMutation({
    mutationFn: (drawData: GachaDrawRequest) => drawGacha(drawData),
  });
};
