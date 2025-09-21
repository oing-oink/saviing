import { useState } from 'react';
import type { Item } from '@/features/game/shop/types/item';

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
