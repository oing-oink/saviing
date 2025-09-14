export interface AnimationConfig {
  /** 프레임 개수 */
  frames: number;
  /** 프레임당 머무는 시간(ms) */
  duration: number;
  /** 반복 여부 */
  loop?: boolean;
}

export type CatAnimationType =
  | 'idle'
  | 'idle2'
  | 'sitting'
  | 'liking'
  | 'run'
  | 'jump'
  | 'sleep';

export const CAT_ANIMATIONS: Record<CatAnimationType, AnimationConfig> = {
  idle: { frames: 7, duration: 200, loop: true },
  idle2: { frames: 14, duration: 200, loop: true },
  sitting: { frames: 3, duration: 250, loop: true },
  liking: { frames: 18, duration: 150, loop: true },
  run: { frames: 7, duration: 150, loop: true },
  jump: { frames: 13, duration: 150, loop: true },
  sleep: { frames: 3, duration: 500, loop: true },
};

export const CAT_SPRITE_PATHS = {
  1: '/game/pet/cat1',
  2: '/game/pet/cat2',
  3: '/game/pet/cat3',
  4: '/game/pet/cat4',
  5: '/game/pet/cat5',
  6: '/game/pet/cat6',
  7: '/game/pet/cat7',
  8: '/game/pet/cat8',
  9: '/game/pet/cat9',
  10: '/game/pet/cat10',
} as const;

export const getAnimationFileName = (animation: CatAnimationType): string => {
  const map: Record<CatAnimationType, string> = {
    idle: 'Idle.png',
    idle2: 'Idle2.png',
    sitting: 'Sitting.png',
    liking: 'Liking.png',
    run: 'Run.png',
    jump: 'Jump.png',
    sleep: 'Sleep.png',
  };
  return map[animation];
};
