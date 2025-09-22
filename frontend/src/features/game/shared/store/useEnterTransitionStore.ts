import { create } from 'zustand';

interface SpritePosition {
  x: number;
  y: number;
  scale: number;
}

interface EnterTransitionState {
  origin?: SpritePosition;
  isTransitioningToGame: boolean;
  setOrigin: (pos: SpritePosition) => void;
  startTransitionToGame: () => void;
  finishTransitionToGame: () => void;
  reset: () => void;
}

export const useEnterTransitionStore = create<EnterTransitionState>(set => ({
  origin: undefined,
  isTransitioningToGame: false,
  setOrigin: (pos: SpritePosition) => set({ origin: pos }),
  startTransitionToGame: () => set({ isTransitioningToGame: true }),
  finishTransitionToGame: () =>
    set({
      origin: undefined,
      isTransitioningToGame: false,
    }),
  reset: () => set({ origin: undefined, isTransitioningToGame: false }),
}));

