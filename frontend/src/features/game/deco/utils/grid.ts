import type { GridCell, GridType } from '@/features/game/room/hooks/useGrid';

interface ParsedCellId {
  gridType: GridType;
  col: number;
  row: number;
}

export const parseCellId = (cellId: string): ParsedCellId | null => {
  const parts = cellId.split('-');
  if (parts.length !== 3) {
    return null;
  }

  const [gridType, colRaw, rowRaw] = parts;
  const col = Number.parseInt(colRaw, 10);
  const row = Number.parseInt(rowRaw, 10);
  if (!Number.isFinite(col) || !Number.isFinite(row)) {
    return null;
  }

  return { gridType: gridType as GridType, col, row };
};

export const buildFootprint = (
  cellId: string,
  xLength: number,
  yLength: number,
): string[] => {
  const parsed = parseCellId(cellId);
  if (!parsed) {
    return [];
  }
  const { gridType, col, row } = parsed;
  const footprint: string[] = [];
  const safeX = Math.max(1, Math.floor(xLength) || 1);
  const safeY = Math.max(1, Math.floor(yLength) || 1);

  for (let dx = 0; dx < safeX; dx += 1) {
    for (let dy = 0; dy < safeY; dy += 1) {
      footprint.push(`${gridType}-${col + dx}-${row + dy}`);
    }
  }

  return footprint;
};

export const findNearestCell = (
  cells: GridCell[],
  px: number,
  py: number,
): GridCell | null => {
  if (!cells.length) {
    return null;
  }

  let nearest: GridCell | null = null;
  let minDistance = Number.POSITIVE_INFINITY;

  for (const cell of cells) {
    const dx = cell.center.x - px;
    const dy = cell.center.y - py;
    const distance = dx * dx + dy * dy;
    if (distance < minDistance) {
      minDistance = distance;
      nearest = cell;
    }
  }

  return nearest;
};

export const getCellBounds = (cells: GridCell[]) => {
  if (!cells.length) {
    return { minX: 0, maxX: 0, minY: 0, maxY: 0 };
  }
  const xs = cells.map(cell => cell.center.x);
  const ys = cells.map(cell => cell.center.y);
  return {
    minX: Math.min(...xs),
    maxX: Math.max(...xs),
    minY: Math.min(...ys),
    maxY: Math.max(...ys),
  };
};

export const isCellFree = (occupied: Set<string>, cellId: string): boolean => {
  return !occupied.has(cellId);
};
