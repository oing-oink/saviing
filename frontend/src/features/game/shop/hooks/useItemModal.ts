import { useState } from 'react';
import type { Item } from '@/features/game/shop/types/item';

/** 상점 아이템 상세 모달의 열림 상태와 선택 아이템을 관리하는 훅. */
export const useItemModal = () => {
  const [selectedItemId, setSelectedItemId] = useState<number | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleItemClick = (item: Item) => {
    setSelectedItemId(item.itemId);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedItemId(null);
  };

  return {
    selectedItemId,
    isModalOpen,
    handleItemClick,
    handleCloseModal,
  };
};
