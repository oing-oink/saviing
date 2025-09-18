/**
 * 펫의 기본 정보와 상태를 나타내는 데이터 타입
 *
 * 펫의 레벨, 경험치, 애정도, 포만감 등 게임에서 사용되는 모든 펫 관련 정보를 포함합니다.
 */
export interface PetData {
  /** 펫의 고유 식별자 */
  petId: number;
  /** 펫 아이템의 ID (인벤토리 연결용) */
  itemId: number;
  /** 펫의 이름 */
  name: string;
  /** 현재 레벨 */
  level: number;
  /** 현재 경험치 */
  exp: number;
  /** 다음 레벨업에 필요한 경험치 */
  requiredExp: number;
  /** 현재 애정도 */
  affection: number;
  /** 최대 애정도 */
  maxAffection: number;
  /** 현재 포만감 */
  energy: number;
  /** 최대 포만감 */
  maxEnergy: number;
  /** 현재 활성 펫 여부 */
  isUsed: boolean;
  /** 펫이 위치한 층 */
  floor: number;
}

/**
 * 펫 상호작용에 사용되는 아이템 인벤토리 타입
 *
 * 펫에게 먹이를 주거나 놀아줄 때 사용하는 아이템들의 수량을 관리합니다.
 */
//TODO: 사용자 아이템에서 feed와 toy 추출해서 사용
export interface PetInventory {
  /** 사료 개수 */
  feed: number;
  /** 장난감 개수 */
  toy: number;
}

/**
 * 펫의 애니메이션 상태를 나타내는 타입
 *
 * 펫이 표시할 수 있는 모든 애니메이션 상태를 정의합니다.
 * 각 상태는 catAnimations.ts의 설정과 연동됩니다.
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
 * CatSprite 컴포넌트에 전달되는 props 타입
 *
 * 스프라이트 렌더링에 필요한 모든 속성을 정의합니다.
 */
export interface PetSpriteProps {
  /** 렌더링할 펫의 ID (스프라이트 경로 결정) */
  petId: number;
  /** 현재 재생할 애니메이션 상태 */
  currentAnimation: PetAnimationState;
  /** 추가적인 CSS 클래스명 */
  className?: string;
  /** 애니메이션 완료 시 호출되는 콜백 함수 */
  onAnimationComplete?: (animation: PetAnimationState) => void;
}

/**
 * 펫의 행동과 애니메이션 상태를 관리하는 타입
 *
 * 펫의 현재 애니메이션을 추적합니다.
 */
export interface PetBehaviorState {
  /** 현재 재생 중인 애니메이션 */
  currentAnimation: PetAnimationState;
}

/**
 * 펫 상호작용 요청 타입
 */
export interface PetInteractionRequest {
  /** 상호작용 타입 */
  type: 'feed' | 'play';
}

/**
 * 소모된 아이템 정보
 */
export interface ConsumptionItem {
  /** 인벤토리 아이템 ID */
  inventoryItemId: number;
  /** 아이템 ID */
  item_id: number;
  /** 아이템 이름 */
  name: string;
  /** 아이템 타입 */
  type: string;
  /** 소모된 개수 */
  count: number;
}

/**
 * 펫 상호작용 응답 타입
 */
export interface PetInteractionResponse {
  /** 업데이트된 펫 정보 */
  pet: PetData;
  /** 사용자가 보유한 펫 관련 아이템 목록 (사용 후 남은 개수 포함) */
  consumption: ConsumptionItem[];
}
