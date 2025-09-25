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
  sitting: { frames: 3, duration: 400, loop: false },
  liking: { frames: 18, duration: 150, loop: true },
  run: { frames: 7, duration: 150, loop: true },
  jump: { frames: 13, duration: 180, loop: false },
  sleep: { frames: 3, duration: 500, loop: true },
};

/**
 * 고양이 스프라이트 경로 매핑
 *
 * 각 펫 아이템 ID(게임 마스터 데이터 기준)별로 스프라이트 이미지가 위치한 경로를 정의합니다.
 */
export const CAT_SPRITE_PATHS: Record<number, string> = {
  1001: '/game/pet/cat1',
  1002: '/game/pet/cat2',
  1003: '/game/pet/cat3',
  1004: '/game/pet/cat4',
  1005: '/game/pet/cat5',
  1006: '/game/pet/cat6',
  1007: '/game/pet/cat7',
  1008: '/game/pet/cat8',
  1009: '/game/pet/cat9',
  1010: '/game/pet/cat10',
};

/**
 * 백엔드에서 내려준 펫 아이템 ID를 스프라이트 경로로 변환한다.
 *
 * @param itemId - 게임 마스터 데이터의 펫 아이템 ID
 * @returns 스프라이트 이미지 디렉터리 경로
 */
export const resolveSpritePath = (itemId: number): string => {
  if (CAT_SPRITE_PATHS[itemId]) {
    return CAT_SPRITE_PATHS[itemId];
  }

  const normalized = itemId % 1000;
  if (normalized > 0 && CAT_SPRITE_PATHS[normalized]) {
    return CAT_SPRITE_PATHS[normalized];
  }

  return CAT_SPRITE_PATHS[1];
};

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
