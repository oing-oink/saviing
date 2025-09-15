import type { GridType } from '../../room/hooks/useGrid';

export interface Item {
  id: number;
  name: string;
  image: string;
  category: Tab; // 탭/카테고리 타입 사용
  description: string;
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

// Tab 타입은 TABS 배열에 있는 name들 중 하나임
export type Tab = (typeof TABS)[number]['name'];
