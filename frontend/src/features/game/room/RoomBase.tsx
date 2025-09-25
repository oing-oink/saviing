import roomImage from '@/assets/room.png';
import { useEffect, useMemo, useRef } from 'react';
import type { ReactNode, RefObject } from 'react';
import { useGestures } from './hooks/useGestures';
import { type TabId } from '@/features/game/shop/types/item';
import { type GridCell, type SurfacePolygon, useGrid } from './hooks/useGrid';

/** Room 컴포넌트가 지원하는 렌더링 모드. */
export type RoomMode = 'readonly' | 'preview' | 'edit';

/** 확대/이동 상태를 표현하는 변환 정보. */
export interface RoomTransform {
  scale: number;
  position: { x: number; y: number };
}

/** 하위 레이어가 Room 정보를 참조할 수 있도록 제공되는 렌더 컨텍스트. */
export interface RoomRenderContext {
  containerRef: RefObject<HTMLDivElement | null>;
  imageRef: RefObject<HTMLImageElement | null>;
  scale: number;
  position: { x: number; y: number };
  gridLines: { x1: number; y1: number; x2: number; y2: number }[];
  gridCells: GridCell[];
  surfacePolygon: SurfacePolygon | null;
}

/** Room 내부에 렌더링될 콘텐츠 (정적 요소 또는 컨텍스트 기반 렌더 함수). */
export type RoomRenderable =
  | ReactNode
  | ((context: RoomRenderContext) => ReactNode);

/** RoomBase 컴포넌트에 전달되는 속성. */
export interface RoomBaseProps {
  mode?: RoomMode;
  placementArea: TabId | null;
  initialTransform?: Partial<RoomTransform>;
  children?: RoomRenderable;
  panEnabled?: boolean;
}

/** Room 하위 콘텐츠를 렌더링 가능한 형태로 변환하는 보조 함수. */
const renderRoomChildren = (
  renderable: RoomRenderable | undefined,
  context: RoomRenderContext,
): ReactNode => {
  if (!renderable) {
    return null;
  }
  return typeof renderable === 'function' ? renderable(context) : renderable;
};

/** Room 배경 이미지와 제스처 인터랙션을 처리하는 기반 컴포넌트. */
const RoomBase = ({
  mode = 'readonly',
  placementArea,
  children,
  panEnabled = true,
}: RoomBaseProps) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const imageRef = useRef<HTMLImageElement | null>(null);

  const { scale, position } = useGestures({
    containerRef,
    targetRef: imageRef,
    panEnabled,
  });

  const { gridLines, gridCells, surfacePolygon } = useGrid({
    scale,
    position,
    roomImageRef: imageRef,
    containerRef,
    placementArea,
  });

  const context = useMemo<RoomRenderContext>(
    () => ({
      containerRef,
      imageRef,
      scale,
      position,
      gridLines,
      gridCells,
      surfacePolygon,
    }),
    [gridCells, gridLines, position, scale, surfacePolygon],
  );

  const cursorStyle = mode === 'readonly' && scale <= 1 ? 'default' : 'grab';

  return (
    <div className="relative">
      {/* 격자선은 내부 계산에만 사용하고 화면에는 노출하지 않는다. */}

      <div
        ref={containerRef}
        className="relative touch-none"
        style={{ cursor: cursorStyle }}
      >
        <img
          ref={imageRef}
          src={roomImage}
          alt="room"
          className="block h-auto w-full origin-center px-4"
          style={{
            transform: `translate(${position.x}px, ${position.y}px) scale(${scale})`,
            willChange: 'transform',
          }}
          draggable="false"
        />

        {/* 하위 오버레이(RoomCanvas 등)가 격자선보다 위에 오도록 z-index를 높인다 */}
        <div className="absolute inset-0 z-[2]">
          {renderRoomChildren(children, context)}
        </div>
      </div>
    </div>
  );
};

export default RoomBase;
