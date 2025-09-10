import { useState } from 'react';
import { type Tab, TABS } from '@/features/game/shop/types/item';
export const useTabs = (defaultTab: Tab = TABS[0]) => {
  const [activeTab, setActiveTab] = useState<Tab>(defaultTab);
  return { activeTab, setActiveTab, TABS };
};
