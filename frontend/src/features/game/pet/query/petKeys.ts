/**
 * Pet 쿼리 키 팩토리
 */
export const petKeys = {
  all: ['pet'] as const,
  detail: (petId: number) => [...petKeys.all, petId] as const,
};
