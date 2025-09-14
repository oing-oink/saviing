import { create } from 'zustand';
import type { PetInventory } from '@/features/game/pet/types/petTypes';

/**
 * 펫 관련 인벤토리 상태를 관리하는 Zustand 스토어 인터페이스
 *
 * 펫에게 주는 사료과 장난감의 개수를 추적하고,
 * 사용 시 수량을 감소시키는 액션들을 제공합니다.
 */
interface PetInventoryState {
  /** 현재 인벤토리 상태 (사료, 장난감 개수) */
  inventory: PetInventory;
  /** 사료을 1개 사용하는 액션 */
  useFeed: () => void;
  /** 장난감을 1개 사용하는 액션 */
  useToy: () => void;
}

/**
 * 펫 관련 인벤토리를 전역 상태로 관리하는 Zustand 스토어
 *
 * 펫에게 주는 사료과 장난감의 수량을 관리하고,
 * 사용 시 수량을 안전하게 감소(음수 방지)시킵니다.
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
