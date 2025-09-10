import type { ApiSuccessResponse } from '@/shared/types/api';
import type { PetData, PetInventory } from '@/features/game/pet/types/petTypes';

export const mockPetData: ApiSuccessResponse<PetData> = {
  success: true,
  status: 200,
  body: {
    petId: 1,
    itemId: 1,
    name: '완두',
    level: 6,
    exp: 500,
    requiredExp: 4000,
    affection: 50,
    maxAffection: 100,
    energy: 50,
    maxEnergy: 100,
    isUsed: true,
    floor: 3,
  },
};

export const mockInventoryData: PetInventory = {
  feed: 10,
  toy: 32,
};
