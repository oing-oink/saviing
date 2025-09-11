import { create } from 'zustand';
import type { PetInventory } from '@/features/game/pet/types/petTypes';

interface PetUIState {
  // UI 상태만 관리
  isStatusCardOpen: boolean;
  openStatusCard: () => void;
  closeStatusCard: () => void;

  inventory: PetInventory;
}

export const usePetStore = create<PetUIState>(set => ({
  // UI 상태
  isStatusCardOpen: false,
  openStatusCard: () => set({ isStatusCardOpen: true }),
  closeStatusCard: () => set({ isStatusCardOpen: false }),

  // TODO: api 연결 후 수정 필요
  inventory: { feed: 3, toy: 2 },
}));
