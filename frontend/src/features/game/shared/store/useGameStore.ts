import { create } from 'zustand';
import type { CharacterGameData } from '@/features/game/shared/types/gameTypes';

/**
 * 게임 상태를 관리하는 Zustand 스토어 인터페이스
 *
 * 캐릭터의 게임 데이터와 재화 정보를 전역 상태로 관리하고,
 * 재화 사용 및 업데이트를 위한 액션들을 제공합니다.
 */
interface GameState {
  /** 캐릭터 게임 데이터 (null이면 아직 로드되지 않음) */
  gameData: CharacterGameData | null;
  /** 게임 데이터를 스토어에 설정하는 액션 */
  setGameData: (data: CharacterGameData) => void;
  /** 코인을 사용하는 액션 */
  useCoin: (amount: number) => void;
  /** 피시코인을 사용하는 액션 */
  useFishCoin: (amount: number) => void;
  /** 코인을 추가하는 액션 */
  addCoin: (amount: number) => void;
  /** 피시코인을 추가하는 액션 */
  addFishCoin: (amount: number) => void;
}

/**
 * 게임 관련 데이터를 전역 상태로 관리하는 Zustand 스토어
 *
 * 캐릭터의 게임 정보와 보유 재화를 관리하고,
 * 재화 사용 시 수량을 안전하게 변경(음수 방지)합니다.
 */
export const useGameStore = create<GameState>(set => ({
  gameData: null,

  setGameData: (data: CharacterGameData) => set({ gameData: data }),

  useCoin: (amount: number) =>
    set(state => ({
      gameData: state.gameData
        ? {
            ...state.gameData,
            coin: Math.max(0, state.gameData.coin - amount),
          }
        : null,
    })),

  useFishCoin: (amount: number) =>
    set(state => ({
      gameData: state.gameData
        ? {
            ...state.gameData,
            fishCoin: Math.max(0, state.gameData.fishCoin - amount),
          }
        : null,
    })),

  addCoin: (amount: number) =>
    set(state => ({
      gameData: state.gameData
        ? {
            ...state.gameData,
            coin: state.gameData.coin + amount,
          }
        : null,
    })),

  addFishCoin: (amount: number) =>
    set(state => ({
      gameData: state.gameData
        ? {
            ...state.gameData,
            fishCoin: state.gameData.fishCoin + amount,
          }
        : null,
    })),
}));
