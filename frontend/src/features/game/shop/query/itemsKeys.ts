export const itemsKeys = {
  all: ['gameItems'] as const,
  list: () => [...itemsKeys.all, 'list'] as const,
  listByTypeAndCategory: (type: string, category: string) => [...itemsKeys.list(), type, category] as const,
  details: () => [...itemsKeys.all, 'detail'] as const,
  detail: (itemId: number) => [...itemsKeys.details(), itemId] as const,
};