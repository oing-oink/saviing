import type { PlacementArea } from '@/features/game/room/hooks/useGrid';

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

/** API 표준 아이템 카테고리 기반 탭 식별자. */
export type TabId = PlacementArea | 'CAT';

/** 탭 UI에서 사용하는 메타 정보. */
export interface TabInfo {
  id: TabId;
  name: string;
}

/** API 표준 카테고리 기반 상점 탭 리스트. */
export const TABS: readonly TabInfo[] = [
  { id: 'CAT', name: '냥이' },
  { id: 'LEFT', name: '왼쪽벽' },
  { id: 'RIGHT', name: '오른쪽벽' },
  { id: 'BOTTOM', name: '바닥' },
  { id: 'ROOM_COLOR', name: '배경' },
] as const;

/** TABS 배열에서 파생되는 탭 타입. */
export type Tab = (typeof TABS)[number];

/** API 표준 아이템 타입 정의. */
export type ItemType = 'PET' | 'ACCESSORY' | 'DECORATION' | 'CONSUMPTION';

/** API 표준 아이템 카테고리 정의. */
export type ItemCategory = 'CAT' | 'HAT' | 'LEFT' | 'RIGHT' | 'BOTTOM' | 'ROOM_COLOR' | 'TOY' | 'FOOD';

/** 탭에서 API 카테고리로의 직접 매핑. */
export const TAB_TO_CATEGORY: Record<TabId, ItemCategory> = {
  CAT: 'CAT',
  LEFT: 'LEFT',
  RIGHT: 'RIGHT',
  BOTTOM: 'BOTTOM',
  ROOM_COLOR: 'ROOM_COLOR',
} as const;

/** 아이템 타입에서 카테고리 매핑. */
export const TYPE_TO_CATEGORIES: Record<ItemType, ItemCategory[]> = {
  PET: ['CAT'],
  ACCESSORY: ['HAT'],
  DECORATION: ['LEFT', 'RIGHT', 'BOTTOM', 'ROOM_COLOR'],
  CONSUMPTION: ['TOY', 'FOOD'],
} as const;
