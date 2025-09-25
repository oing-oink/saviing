import { create } from 'zustand';
import type {
  PetInventory,
  PetBehaviorState,
} from '@/features/game/pet/types/petTypes';

/**
 * 펫 관련 상태를 관리하는 Zustand 스토어 인터페이스
 *
 * 펫에게 주는 사료과 장난감의 개수, 펫의 행동 상태, 방에 배치된 펫들과
 * 현재 선택된 펫을 추적하고, 서버 기반 인벤토리 동기화를 제공합니다.
 */
interface PetStoreState {
  /** 현재 인벤토리 상태 (사료, 장난감 개수) */
  inventory: PetInventory;
  /** 펫의 행동 상태 */
  behavior: PetBehaviorState;
  /** 방에 배치된 모든 펫 ID들 (최대 2개) */
  placedPetIds: number[];
  /** 현재 선택된 펫 ID (상호작용 API용) */
  selectedPetId: number | null;
  /** 인벤토리 전체를 설정하는 액션 */
  setInventory: (inventory: PetInventory) => void;
  /** 펫의 행동 상태를 설정하는 액션 */
  setBehavior: (behavior: PetBehaviorState) => void;
  /** 방에 배치된 펫 ID들을 설정하는 액션 */
  setPlacedPetIds: (petIds: number[]) => void;
  /** 현재 선택된 펫 ID를 설정하는 액션 */
  setSelectedPetId: (petId: number | null) => void;
}

/**
 * 펫 관련 상태를 전역 상태로 관리하는 Zustand 스토어
 *
 * 펫에게 주는 사료과 장난감의 수량, 펫의 행동 상태, 방에 배치된 펫들과
 * 현재 선택된 펫을 관리하고, 서버 기반 인벤토리 동기화를 제공합니다.
 */
export const usePetStore = create<PetStoreState>(set => ({
  // TODO: 사용자 데이터로 변경 필요
  inventory: { feed: 3, toy: 2 },

  // 펫의 초기 행동 상태
  behavior: {
    currentAnimation: 'idle',
  },

  // 방에 배치된 펫 ID들 초기값
  placedPetIds: [],

  // 현재 선택된 펫 ID 초기값
  selectedPetId: null,

  // 인벤토리 전체 설정 (서버 응답 기반)
  setInventory: (inventory: PetInventory) =>
    set(() => ({
      inventory,
    })),

  // 펫의 행동 상태 설정
  setBehavior: (behavior: PetBehaviorState) =>
    set(() => ({
      behavior,
    })),

  // 방에 배치된 펫 ID들 설정
  setPlacedPetIds: (petIds: number[]) =>
    set(() => ({
      placedPetIds: petIds,
    })),

  // 현재 선택된 펫 ID 설정
  setSelectedPetId: (petId: number | null) =>
    set(() => ({
      selectedPetId: petId,
    })),
}));
