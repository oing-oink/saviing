import { useMutation, useQueryClient } from '@tanstack/react-query';
import { renamePetName } from '@/features/game/pet/api/petApi';
import { petKeys } from '@/features/game/pet/query/petKeys';
import type {
  PetData,
  PetRenameRequest,
} from '@/features/game/pet/types/petTypes';

export const usePetRename = (petId: number) => {
  const queryClient = useQueryClient();

  return useMutation<PetData, Error, PetRenameRequest>({
    mutationKey: petKeys.rename(petId),
    mutationFn: payload => renamePetName(petId, payload),
    onSuccess: updated => {
      queryClient.setQueryData(petKeys.detail(petId), updated);
    },
  });
};
