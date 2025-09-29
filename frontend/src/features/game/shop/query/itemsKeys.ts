/** 상점 아이템 관련 React Query 키 팩토리. */
export const itemsKeys = {
  all: ['gameItems'] as const,
  list: () => [...itemsKeys.all, 'list'] as const,
  listByTypeAndCategory: (type: string, category: string) =>
    [...itemsKeys.list(), type, category] as const,
  details: () => [...itemsKeys.all, 'detail'] as const,
  detail: (itemId: number) => [...itemsKeys.details(), itemId] as const,
  inventory: () => [...itemsKeys.all, 'inventory'] as const,
  inventoryByTypeAndCategory: (
    characterId: number,
    type: string,
    category: string,
    isUsed?: boolean,
  ) => [...itemsKeys.inventory(), characterId, type, category, isUsed] as const,
};
