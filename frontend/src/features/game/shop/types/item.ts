import type { GridType } from '@/features/game/room/hooks/useGrid';

/** 상점과 인벤토리에서 사용하는 기본 아이템 정보. */
export interface Item {
  itemId: number;
  itemName: string;
  itemDescription: string;
  itemType: string;
  itemCategory: string;
  rarity: string;
  xLength: number;
  yLength: number;
  coin: number;
  fishCoin: number;
  imageUrl: string;
  isAvailable: boolean;
  createdAt: string;
  updatedAt: string;
}

/** 아이템 목록 조회 응답. */
export interface ItemsResponse {
  items: Item[];
  totalCount: number;
}

/** 결제 수단 종류. */
export type PaymentMethod = 'COIN' | 'FISH_COIN';

/** 아이템 구매 요청 페이로드. */
export interface PurchaseRequest {
  characterId: number;
  itemId: number;
  paymentMethod: PaymentMethod;
}

/** 아이템 구매 응답 구조. */
export interface PurchaseResponse {
  success: boolean;
  message: string;
  remainingBalance?: {
    coin: number;
    fishCoin: number;
  };
}

/** 상점/데코 탭에서 사용하는 식별자. */
export type TabId = GridType | 'cat';

/** 탭 UI에서 사용하는 메타 정보. */
export interface TabInfo {
  id: TabId;
  name: string;
}

/** 상점 탭 리스트. */
export const TABS: readonly TabInfo[] = [
  { id: 'cat', name: '냥이' },
  { id: 'leftWall', name: '왼쪽벽' },
  { id: 'rightWall', name: '오른쪽벽' },
  { id: 'floor', name: '바닥' },
] as const;

/** TABS 배열에서 파생되는 탭 타입. */
export type Tab = (typeof TABS)[number];

/** 각 탭을 API 요청 파라미터로 변환하는 매핑. */
export const TAB_TO_API_PARAMS: Record<
  string,
  { type: string; category: string }
> = {
  냥이: { type: 'PET', category: 'CAT' },
  왼쪽벽: { type: 'DECORATION', category: 'LEFT' },
  오른쪽벽: { type: 'DECORATION', category: 'RIGHT' },
  바닥: { type: 'DECORATION', category: 'BOTTOM' },
} as const;
