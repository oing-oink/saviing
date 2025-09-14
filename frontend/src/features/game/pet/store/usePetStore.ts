import { create } from 'zustand';
import type { PetInventory } from '@/features/game/pet/types/petTypes';

/**
 * 펫 관련 인벤토리 상태를 관리하는 인터페이스
 */
interface PetInventoryState {
  inventory: PetInventory;
  useFeed: () => void;
  useToy: () => void;
}

/**
 * 펫 관련 인벤토리를 전역으로 관리
 */
export const usePetStore = create<PetInventoryState>(set => ({
  // TODO: API 연결 후 실제 인벤토리 데이터로 교체 필요
  inventory: { feed: 3, toy: 2 },

  // 먹이 사용 (1개 감소, 음수 방지)
  useFeed: () =>
    set(state => ({
      inventory: {
        ...state.inventory,
        feed: Math.max(0, state.inventory.feed - 1),
      },
    })),

  // 장난감 사용 (1개 감소, 음수 방지)
  useToy: () =>
    set(state => ({
      inventory: {
        ...state.inventory,
        toy: Math.max(0, state.inventory.toy - 1),
      },
    })),
}));
