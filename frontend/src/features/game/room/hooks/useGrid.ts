import { useEffect, useState } from 'react';
import type { RefObject } from 'react';
import type { TabId } from '@/features/game/shop/types/item';

/** API 표준 아이템 카테고리 기반 배치 영역. */
export type PlacementArea = 'LEFT' | 'RIGHT' | 'BOTTOM' | 'ROOM_COLOR';

/** 2차원 좌표를 나타내는 구조체. */
export interface Point {
  x: number;
  y: number;
}

/** 격자 셀의 정보와 꼭짓점 좌표. */
export interface GridCell {
  id: string;
  placementArea: PlacementArea;
  center: Point;
  vertices: [Point, Point, Point, Point];
}

/** Room 표면을 감싸는 사각형 폴리곤. */
export type SurfacePolygon = [Point, Point, Point, Point];

/** Room 이미지와 스케일 정보를 기반으로 격자를 계산하기 위한 옵션. */
interface UseGridProps {
  scale: number;
  position: { x: number; y: number };
  roomImageRef: RefObject<HTMLImageElement | null>;
  containerRef: RefObject<HTMLDivElement | null>;
  placementArea: TabId | null;
}

interface Line {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

export const GRID_DIVISIONS = 24;

const AREAS_CONFIG: Partial<
  Record<PlacementArea, [Point, Point, Point, Point]>
> = {
  LEFT: [
    { x: 0.06, y: 0.325 },
    { x: 0.5, y: 0.035 },
    { x: 0.5, y: 0.39 },
    { x: 0.06, y: 0.675 },
  ],
  RIGHT: [
    { x: 0.505, y: 0.04 },
    { x: 0.94, y: 0.32 },
    { x: 0.94, y: 0.675 },
    { x: 0.505, y: 0.395 },
  ],
  BOTTOM: [
    { x: 0.06, y: 0.678 },
    { x: 0.5, y: 0.392 },
    { x: 0.94, y: 0.678 },
    { x: 0.5, y: 0.96 },
  ],
  ROOM_COLOR: [
    { x: 0.06, y: 0.678 },
    { x: 0.5, y: 0.392 },
    { x: 0.94, y: 0.678 },
    { x: 0.5, y: 0.96 },
  ],
};

/** 두 점 사이를 t 비율만큼 선형 보간한다. */
const lerp = (p1: Point, p2: Point, t: number): Point => ({
  x: p1.x + (p2.x - p1.x) * t,
  y: p1.y + (p2.y - p1.y) * t,
});

/** Room 이미지 크기와 이동 값에 맞춰 격자선과 셀 좌표를 계산하는 훅. */
export const useGrid = ({
  scale,
  position,
  roomImageRef,
  containerRef,
  placementArea,
}: UseGridProps) => {
  const [gridLines, setGridLines] = useState<Line[]>([]);
  const [gridCells, setGridCells] = useState<GridCell[]>([]);
  const [surfacePolygon, setSurfacePolygon] = useState<SurfacePolygon | null>(
    null,
  );

  useEffect(() => {
    const corners = placementArea
      ? AREAS_CONFIG[placementArea as PlacementArea]
      : undefined;

    const imageElement = roomImageRef.current;
    const containerElement = containerRef.current;

    if (!corners || !imageElement || !containerElement) {
      setGridLines([]);
      setGridCells([]);
      setSurfacePolygon(null);
      return;
    }

    const computeGrid = () => {
      if (!roomImageRef.current || !containerRef.current) {
        return;
      }

      const imgWidth = roomImageRef.current.offsetWidth;
      const imgHeight = roomImageRef.current.offsetHeight;

      if (imgWidth === 0 || imgHeight === 0) {
        return;
      }

      const newLines: Line[] = [];
      const newCells: GridCell[] = [];

      const [tl_rel, tr_rel, br_rel, bl_rel] = corners.map(point => ({
        x: imgWidth * point.x,
        y: imgHeight * point.y,
      }));

      for (let i = 0; i <= GRID_DIVISIONS; i++) {
        const t = i / GRID_DIVISIONS;

        const p1 = lerp(bl_rel, tl_rel, t);
        const p2 = lerp(br_rel, tr_rel, t);
        newLines.push({ x1: p1.x, y1: p1.y, x2: p2.x, y2: p2.y });

        const p3 = lerp(tl_rel, tr_rel, t);
        const p4 = lerp(bl_rel, br_rel, t);
        newLines.push({ x1: p3.x, y1: p3.y, x2: p4.x, y2: p4.y });
      }

      for (let i = 0; i < GRID_DIVISIONS; i++) {
        for (let j = 0; j < GRID_DIVISIONS; j++) {
          const tColStart = i / GRID_DIVISIONS;
          const tColEnd = (i + 1) / GRID_DIVISIONS;
          const tRowStart = j / GRID_DIVISIONS;
          const tRowEnd = (j + 1) / GRID_DIVISIONS;

          const colStartTop = lerp(tl_rel, tr_rel, tColStart);
          const colStartBottom = lerp(bl_rel, br_rel, tColStart);
          const colEndTop = lerp(tl_rel, tr_rel, tColEnd);
          const colEndBottom = lerp(bl_rel, br_rel, tColEnd);

          const tl = lerp(colStartBottom, colStartTop, tRowEnd);
          const tr = lerp(colEndBottom, colEndTop, tRowEnd);
          const bl = lerp(colStartBottom, colStartTop, tRowStart);
          const br = lerp(colEndBottom, colEndTop, tRowStart);

          newCells.push({
            id: `${placementArea}-${i + 1}-${j + 1}`,
            placementArea: placementArea as PlacementArea,
            center: { x: 0, y: 0 },
            vertices: [tl, tr, br, bl],
          });
        }
      }

      const imageWidth = imgWidth;
      const imageHeight = imgHeight;
      const containerWidth = containerElement.offsetWidth;
      const containerHeight = containerElement.offsetHeight;

      const transformPoint = (point: Point): Point => ({
        x:
          (point.x - imageWidth / 2) * scale +
          imageWidth / 2 +
          position.x +
          containerWidth,
        y:
          (point.y - imageHeight / 2) * scale +
          imageHeight / 2 +
          position.y +
          containerHeight,
      });

      const finalLines = newLines.map(line => ({
        x1: transformPoint({ x: line.x1, y: line.y1 }).x,
        y1: transformPoint({ x: line.x1, y: line.y1 }).y,
        x2: transformPoint({ x: line.x2, y: line.y2 }).x,
        y2: transformPoint({ x: line.x2, y: line.y2 }).y,
      }));

      const finalCells = newCells.map(cell => {
        const [tl, tr, br, bl] = cell.vertices.map(transformPoint) as [
          Point,
          Point,
          Point,
          Point,
        ];

        return {
          ...cell,
          vertices: [tl, tr, br, bl] as [Point, Point, Point, Point],
          center: {
            x: (tl.x + tr.x + br.x + bl.x) / 4,
            y: (tl.y + tr.y + br.y + bl.y) / 4,
          },
        };
      });

      const polygon = [tl_rel, tr_rel, br_rel, bl_rel].map(
        transformPoint,
      ) as SurfacePolygon;

      setGridLines(finalLines);
      setGridCells(finalCells);
      setSurfacePolygon(polygon);
    };

    if (
      !imageElement.complete ||
      imageElement.naturalWidth === 0 ||
      imageElement.offsetWidth === 0
    ) {
      const handleLoad = () => {
        computeGrid();
      };

      imageElement.addEventListener('load', handleLoad);

      return () => {
        imageElement.removeEventListener('load', handleLoad);
      };
    }

    computeGrid();
  }, [placementArea, scale, position, roomImageRef, containerRef]);

  return { gridLines, gridCells, surfacePolygon };
};
