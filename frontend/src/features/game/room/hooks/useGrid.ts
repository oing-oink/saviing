import type { RefObject } from 'react';
import { useState, useEffect } from 'react';
import type { TabId } from 'src/features/game/shop/types/item';

// [타입 정의]
export type GridType = 'floor' | 'leftWall' | 'rightWall';

interface Point {
  x: number;
  y: number;
}

interface UseGridProps {
  scale: number;
  position: { x: number; y: number };
  roomImageRef: RefObject<HTMLImageElement | null>;
  containerRef: RefObject<HTMLDivElement | null>;
  gridType: TabId | null;
}

interface Line {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

// [상수 정의]
const GRID_DIVISIONS = 12; // 각 영역을 몇 개의 셀로 나눌지

// 각 영역의 꼭짓점 4개를 이미지 크기 비율로 정의 (TL, TR, BR, BL)
const AREAS_CONFIG: Partial<Record<GridType, [Point, Point, Point, Point]>> = {
  leftWall: [
    { x: 0.07, y: 0.33 }, // 좌상단
    { x: 0.5, y: 0.04 }, // 우상단
    { x: 0.5, y: 0.39 }, // 우하단
    { x: 0.07, y: 0.67 }, // 좌하단
  ],
  rightWall: [
    { x: 0.505, y: 0.04 }, // 좌상단
    { x: 0.935, y: 0.32 }, // 우상단
    { x: 0.935, y: 0.675 }, // 우하단
    { x: 0.505, y: 0.395 }, // 좌하단
  ],
  floor: [
    { x: 0.065, y: 0.68 }, // 좌
    { x: 0.5, y: 0.395 }, // 상
    { x: 0.935, y: 0.68 }, // 우
    { x: 0.5, y: 0.96 }, // 하
  ],
};

// 선형 보간(Linear Interpolation) 헬퍼 함수
const lerp = (p1: Point, p2: Point, t: number): Point => ({
  x: p1.x + (p2.x - p1.x) * t,
  y: p1.y + (p2.y - p1.y) * t,
});

export const useGrid = ({
  scale,
  position,
  roomImageRef,
  containerRef,
  gridType,
}: UseGridProps) => {
  const [gridLines, setGridLines] = useState<Line[]>([]);

  useEffect(() => {
    const corners = gridType ? AREAS_CONFIG[gridType as GridType] : undefined;

    if (!corners || !roomImageRef.current || !containerRef.current) {
      setGridLines([]);
      return;
    }

    const imageElement = roomImageRef.current;
    const containerElement = containerRef.current;
    const newLines: Line[] = [];

    // 1. 이미지 자체를 기준으로 한 꼭짓점들의 상대 좌표 계산
    const [tl_rel, tr_rel, br_rel, bl_rel] = corners.map(p => ({
      x: imageElement.offsetWidth * p.x,
      y: imageElement.offsetHeight * p.y,
    }));

    // 2. 상대 좌표를 기준으로 그리드 라인들을 생성
    for (let i = 0; i <= GRID_DIVISIONS; i++) {
      const t = i / GRID_DIVISIONS;

      const p1 = lerp(bl_rel, tl_rel, t);
      const p2 = lerp(br_rel, tr_rel, t);
      newLines.push({ x1: p1.x, y1: p1.y, x2: p2.x, y2: p2.y });

      const p3 = lerp(tl_rel, tr_rel, t);
      const p4 = lerp(bl_rel, br_rel, t);
      newLines.push({ x1: p3.x, y1: p3.y, x2: p4.x, y2: p4.y });
    }

    const imageWidth = imageElement.offsetWidth;
    const imageHeight = imageElement.offsetHeight;
    const containerWidth = containerElement.offsetWidth;
    const containerHeight = containerElement.offsetHeight;

    // 3. 생성된 모든 라인에 scale, position 및 화면 위치 변환을 적용
    const finalLines = newLines.map(line => ({
      x1:
        (line.x1 - imageWidth / 2) * scale +
        imageWidth / 2 +
        position.x +
        containerWidth,
      y1:
        (line.y1 - imageHeight / 2) * scale +
        imageHeight / 2 +
        position.y +
        containerHeight,
      x2:
        (line.x2 - imageWidth / 2) * scale +
        imageWidth / 2 +
        position.x +
        containerWidth,
      y2:
        (line.y2 - imageHeight / 2) * scale +
        imageHeight / 2 +
        position.y +
        containerHeight,
    }));

    setGridLines(finalLines);
  }, [gridType, scale, position, roomImageRef, containerRef]);

  return { gridLines };
};
