/**
 * Pet 데이터 타입
 */
export interface PetData {
  petId: number;
  itemId: number;
  name: string;
  level: number;
  exp: number;
  requiredExp: number;
  affection: number;
  maxAffection: number;
  energy: number;
  maxEnergy: number;
  isUsed: boolean;
  floor: number;
}

/**
 * 펫 관련 아이템 타입
 */
//TODO: 사용자 아이템에서 feed와 toy 추출해서 사용
export interface PetInventory {
  feed: number;
  toy: number;
}

/**
 * 펫 애니메이션 상태 타입
 */
export type PetAnimationState =
  | 'idle'
  | 'idle2'
  | 'sitting'
  | 'liking'
  | 'run'
  | 'jump'
  | 'sleep';

/**
 * 펫 스프라이트 Props 타입
 */
export interface PetSpriteProps {
  petId: number;
  currentAnimation: PetAnimationState;
  className?: string;
  onAnimationComplete?: (animation: PetAnimationState) => void;
}

/**
 * 펫 행동 상태 타입
 */
export interface PetBehaviorState {
  currentAnimation: PetAnimationState;
  isAnimating: boolean;
  lastAnimationChange: number;
}
