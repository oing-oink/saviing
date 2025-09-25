import type { PetData } from '@/features/game/pet/types/petTypes';

/**
 * 홈 입장 시 내려오는 게임 엔트리 데이터 구조
 */
export interface GameEntryData {
  /** 현재 접속한 캐릭터 식별자 */
  characterId: number;
  /** 현재 진입한 방 식별자 */
  roomId: number;
  /** 현재 활성화된 펫 정보 */
  pet: GameEntryPet;
}

/**
 * 엔트리 응답에 포함되는 펫 정보
 *
 * 인벤토리에 등록된 펫 한 마리에 대한 요약 정보만 내려온다.
 */
export type GameEntryPet = Omit<PetData, 'isUsed' | 'floor'>;

/**
 * 게임 캐릭터 생성 응답
 */
export interface GameCharacterData {
  characterId: number;
  customerId: number;
  accountId: number;
  connectionStatus: string;
  connectionDate: string | null;
  terminationReason: string | null;
  terminatedAt: string | null;
  coin: number;
  fishCoin: number;
  roomCount: number;
  roomId: number | null;
  isActive: boolean;
  deactivatedAt: string | null;
  createdAt: string;
  updatedAt: string;
}
