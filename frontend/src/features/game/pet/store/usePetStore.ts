import { create } from 'zustand';
import type { PetInventory } from '@/features/game/pet/types/petTypes';

interface PetUIState {
  // UI 상태만 관리
  isStatusCardOpen: boolean;
  openStatusCard: () => void;
  closeStatusCard: () => void;

  // 인벤토리 (임시로 유지, 추후 별도 API로 분리 예정)
  inventory: PetInventory;
}

export const usePetStore = create<PetUIState>(set => ({
  // UI 상태
  isStatusCardOpen: false,
  openStatusCard: () => set({ isStatusCardOpen: true }),
  closeStatusCard: () => set({ isStatusCardOpen: false }),

  // 임시 인벤토리 데이터 (추후 별도 관리)
  inventory: { feed: 3, toy: 2 },
}));
