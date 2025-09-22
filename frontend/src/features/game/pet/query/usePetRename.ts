import { useMutation, useQueryClient } from '@tanstack/react-query';
import { renamePetName } from '@/features/game/pet/api/petApi';
import { petKeys } from '@/features/game/pet/query/petKeys';
import type { PetData } from '@/features/game/pet/types/petTypes';

export const usePetRename = (petId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationKey: petKeys.rename(petId),
    mutationFn: (name: string) => renamePetName(petId, name),
    onSuccess: (updated: PetData) => {
      queryClient.setQueryData(petKeys.detail(petId), updated);
    },
  });
};

