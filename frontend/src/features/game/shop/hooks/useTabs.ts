import { useState } from 'react';
import { type Tab, TABS } from '@/features/game/shop/types/item';

/**
 * 상점 인벤토리 탭 상태를 관리한다.
 * @param defaultTab 초기 활성 탭 (기본값: 첫 번째 탭)
 * @returns 현재 탭과 변경자, 전체 탭 목록
 */
export const useTabs = (defaultTab: Tab = TABS[0]) => {
  const [activeTab, setActiveTab] = useState<Tab>(defaultTab);
  return { activeTab, setActiveTab, TABS };
};
