export interface AnimationConfig {
  frames: number;
  duration: number; // ms per frame
  loop?: boolean;
}

export interface CatAnimations {
  idle: AnimationConfig;
  idle2: AnimationConfig;
  sitting: AnimationConfig;
  liking: AnimationConfig;
  run: AnimationConfig;
  jump: AnimationConfig;
  sleep: AnimationConfig;
}

export type CatAnimationType = keyof CatAnimations;

export const CAT_ANIMATIONS: CatAnimations = {
  idle: {
    frames: 7,
    duration: 150,
    loop: true,
  },
  idle2: {
    frames: 14,
    duration: 120,
    loop: true,
  },
  sitting: {
    frames: 3,
    duration: 200,
    loop: false,
  },
  liking: {
    frames: 18,
    duration: 100,
    loop: false,
  },
  run: {
    frames: 7,
    duration: 80,
    loop: true,
  },
  jump: {
    frames: 13,
    duration: 60,
    loop: false,
  },
  sleep: {
    frames: 3,
    duration: 800,
    loop: true,
  },
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
  const fileNames: Record<CatAnimationType, string> = {
    idle: 'Idle.png',
    idle2: 'Idle2.png',
    sitting: 'Sitting.png',
    liking: 'Liking.png',
    run: 'Run.png',
    jump: 'Jump.png',
    sleep: 'Sleep.png',
  };
  
  return fileNames[animation];
};