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
