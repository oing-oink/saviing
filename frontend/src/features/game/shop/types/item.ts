import type { GridType } from '@/features/game/room/hooks/useGrid';

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

export interface ItemsResponse {
  items: Item[];
  totalCount: number;
}

// 탭/카테고리 상수 및 타입
export type TabId = GridType | 'cat';

export interface TabInfo {
  id: TabId;
  name: string;
}

export const TABS: readonly TabInfo[] = [
  { id: 'cat', name: '냥이' },
  { id: 'leftWall', name: '왼쪽벽' },
  { id: 'rightWall', name: '오른쪽벽' },
  { id: 'floor', name: '바닥' },
] as const;

// Tab 타입은 TABS 배열의 값 중 하나임
export type Tab = (typeof TABS)[number];

// 탭과 API 파라미터 매핑
export const TAB_TO_API_PARAMS: Record<
  string,
  { type: string; category: string }
> = {
  '냥이': { type: 'PET', category: 'CAT' },
  '왼쪽벽': { type: 'DECORATION', category: 'LEFT' },
  '오른쪽벽': { type: 'DECORATION', category: 'RIGHT' },
  '바닥': { type: 'DECORATION', category: 'BOTTOM' },
} as const;
