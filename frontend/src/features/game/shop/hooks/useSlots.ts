import type { Item } from '@/features/game/shop/types/item';

const GRID_SIZE = 3;
const MIN_SLOTS = GRID_SIZE * 3; // 최소 9칸

/** 인벤토리 그리드의 단일 슬롯을 나타내는 구조. */
export interface Slot {
  id: string;
  item: Item | null;
}

/**
 * 인벤토리에 표시할 아이템을 슬롯 배열 형태로 변환한다.
 * @param items 화면에 노출할 아이템 목록
 * @returns 항상 최소 9칸을 유지하며 필요한 만큼 확장된 슬롯 배열
 */
export const useSlots = (items: Item[]): Slot[] => {
  const slots: Slot[] = [];
  const slotCount = Math.max(MIN_SLOTS, items.length);

  for (let i = 0; i < slotCount; i++) {
    slots.push({
      id: `slot-${i}`,
      item: items[i] ?? null,
    });
  }

  return slots;
};
