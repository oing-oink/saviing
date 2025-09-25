import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createGameCharacter } from '@/features/game/entry/api/gameEntryApi';
import { gameEntryKeys } from '@/features/game/entry/query/gameEntryKeys';

export const useCreateGameCharacter = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createGameCharacter,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: gameEntryKeys.all });
    },
  });
};
