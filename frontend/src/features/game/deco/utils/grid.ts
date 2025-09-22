import type { GridCell, PlacementArea } from '@/features/game/room/hooks/useGrid';

/** 셀 ID를 해석했을 때 얻을 수 있는 그리드 정보. */
interface ParsedCellId {
  placementArea: PlacementArea;
  col: number;
  row: number;
}

/** 셀 ID 문자열을 그리드 타입과 좌표로 파싱한다. */
export const parseCellId = (cellId: string): ParsedCellId | null => {
  const parts = cellId.split('-');
  if (parts.length !== 3) {
    return null;
  }

  const [placementArea, colRaw, rowRaw] = parts;
  const col = Number.parseInt(colRaw, 10);
  const row = Number.parseInt(rowRaw, 10);
  if (!Number.isFinite(col) || !Number.isFinite(row)) {
    return null;
  }

  return { placementArea: placementArea as PlacementArea, col, row };
};

/** 기준 셀에서 시작해 x/y 길이만큼의 footprint ID 목록을 만든다. */
export const buildFootprint = (
  cellId: string,
  xLength: number,
  yLength: number,
): string[] => {
  const parsed = parseCellId(cellId);
  if (!parsed) {
    return [];
  }
  const { placementArea, col, row } = parsed;
  const footprint: string[] = [];
  const safeX = Math.max(1, Math.floor(xLength) || 1);
  const safeY = Math.max(1, Math.floor(yLength) || 1);

  for (let dx = 0; dx < safeX; dx += 1) {
    for (let dy = 0; dy < safeY; dy += 1) {
      footprint.push(`${placementArea}-${col + dx}-${row + dy}`);
    }
  }

  return footprint;
};

/** 주어진 좌표와 가장 가까운 셀을 찾는다. */
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

/** 셀 집합의 중심 좌표를 기준으로 최소/최대 범위를 구한다. */
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

/** 특정 셀이 점유 목록에 포함돼 있는지 여부를 확인한다. */
export const isCellFree = (occupied: Set<string>, cellId: string): boolean => {
  return !occupied.has(cellId);
};
