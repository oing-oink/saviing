import type { Item } from '@/features/game/shop/types/item';

const GRID_SIZE = 4;
const MIN_SLOTS = GRID_SIZE * 2; // 최소 12칸

export interface Slot {
  id: string;
  item: Item | null;
}

/**
 * @param items 인벤토리에 표시할 아이템 배열
 * @returns 고유 ID를 가진 슬롯 배열 (최소 12개)
 * 슬롯 배열 생성 (최소 12칸, 아이템이 많으면 확장)
 * - 아이템이 부족하면 null로 빈칸 채움
 * - 아이템이 12개 이상이면 아이템 개수만큼 슬롯 생성
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
