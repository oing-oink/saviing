import { useState } from 'react';
import { type Tab, TABS } from '@/features/game/shop/types/item';

/**
 * @param defaultTab 초기 활성 탭 (기본값: TABS[0])
 * @returns 탭 상태와 제어 함수들을 포함한 객체
 * 탭 상태 관리 훅
 * - 현재 활성 탭 상태와 변경 함수 제공
 * - 사용 가능한 모든 탭 목록 제공
 */
export const useTabs = (defaultTab: Tab = TABS[0].name) => {
  const [activeTab, setActiveTab] = useState<Tab>(defaultTab);
  return { activeTab, setActiveTab, TABS };
};
