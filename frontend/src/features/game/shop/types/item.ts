export interface Item {
  id: number;
  name: string;
  image: string;
  category: Tab; // 탭/카테고리 타입 사용
}

// 탭/카테고리 상수 및 타입
export const TABS = ['냥이', '왼쪽벽', '오른쪽벽', '바닥'] as const;

// Tab 타입은 TABS 배열의 값 중 하나임
export type Tab = (typeof TABS)[number];
