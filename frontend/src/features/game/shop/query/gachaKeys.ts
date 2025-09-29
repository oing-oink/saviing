export const gachaKeys = {
  all: ['gacha'] as const,
  info: () => [...gachaKeys.all, 'info'] as const,
  draw: () => [...gachaKeys.all, 'draw'] as const,
} as const;
