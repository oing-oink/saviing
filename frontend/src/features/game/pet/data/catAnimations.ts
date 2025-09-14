/**
 * 애니메이션 설정 인터페이스
 *
 * 스프라이트 애니메이션의 프레임 정보와 타이밍을 정의합니다.
 */
export interface AnimationConfig {
  /** 프레임 개수 */
  frames: number;
  /** 프레임당 머무는 시간(ms) */
  duration: number;
  /** 반복 여부 */
  loop?: boolean;
}

/**
 * 고양이 애니메이션 타입
 *
 * 사용 가능한 모든 고양이 애니메이션 상태를 정의합니다.
 */
export type CatAnimationType =
  | 'idle'
  | 'idle2'
  | 'sitting'
  | 'liking'
  | 'run'
  | 'jump'
  | 'sleep';

/**
 * 고양이 애니메이션 설정 매핑
 *
 * 각 애니메이션 타입별로 프레임 수, 지속 시간, 반복 여부를 정의합니다.
 */
export const CAT_ANIMATIONS: Record<CatAnimationType, AnimationConfig> = {
  idle: { frames: 7, duration: 200, loop: true },
  idle2: { frames: 14, duration: 200, loop: true },
  sitting: { frames: 3, duration: 250, loop: true },
  liking: { frames: 18, duration: 150, loop: true },
  run: { frames: 7, duration: 150, loop: true },
  jump: { frames: 13, duration: 150, loop: true },
  sleep: { frames: 3, duration: 500, loop: true },
};

/**
 * 고양이 스프라이트 경로 매핑
 *
 * 각 펫 ID별로 스프라이트 이미지가 위치한 경로를 정의합니다.
 */
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

/**
 * 애니메이션 타입에 해당하는 파일명을 반환하는 함수
 *
 * 애니메이션 타입을 받아 해당하는 스프라이트 시트 파일명을 반환합니다.
 *
 * @param animation - 변환할 애니메이션 타입
 * @returns 스프라이트 시트 파일명
 */
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
