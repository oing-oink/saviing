/**
 * 캐릭터 게임 정보 데이터 타입
 *
 * 게임 내에서 사용되는 캐릭터의 기본 정보와 보유 재화 정보를 포함합니다.
 */
export interface CharacterGameData {
  /** 캐릭터 ID */
  characterId: number;
  /** 고객 ID */
  customerId: number;
  /** 보유 코인 */
  coin: number;
  /** 보유 피시 코인 */
  fishCoin: number;
  /** 계정 활성화 여부 */
  isActive: boolean;
  /** 보유 방 개수 */
  roomCount: number;
  /** 마지막 접속 시간 */
  lastAccessAt: string;
}

/**
 * 캐릭터 이자율 계산 통계 데이터 타입
 *
 * 펫 레벨과 인벤토리 레어리티 통계를 기반으로 한 이자율 계산 정보를 포함합니다.
 */
export interface CharacterStatistics {
  /** 캐릭터 ID */
  characterId: number;
  /** 상위 10마리 펫의 레벨 합 */
  topPetLevelSum: number;
  /** 인벤토리 레어리티 통계 */
  inventoryRarityStatistics: {
    /** 펫 통계 */
    pet: {
      /** CAT 카테고리 레어리티 점수 */
      CAT: number;
    };
    /** 장식 아이템 통계 */
    decoration: {
      /** LEFT 카테고리 레어리티 점수 */
      LEFT: number;
      /** RIGHT 카테고리 레어리티 점수 */
      RIGHT: number;
      /** BOTTOM 카테고리 레어리티 점수 */
      BOTTOM: number;
    };
  };
  /** 계산된 최종 이자율 */
  calculatedInterestRate: number;
}
