import { useState } from 'react';
import type { Item } from '@/features/game/shop/types/item';

export const useItemModal = () => {
  const [selectedItem, setSelectedItem] = useState<Item | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleItemClick = (item: Item) => {
    setSelectedItem(item);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedItem(null);
  };

  return {
    selectedItem,
    isModalOpen,
    handleItemClick,
    handleCloseModal,
  };
};
