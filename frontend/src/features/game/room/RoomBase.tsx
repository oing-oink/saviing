import roomImage from '@/assets/room.png';
import { useEffect, useMemo, useRef } from 'react';
import type { ReactNode, RefObject } from 'react';
import { useGestures } from './hooks/useGestures';
import { type TabId } from '@/features/game/shop/types/item';
import { type GridCell, type SurfacePolygon, useGrid } from './hooks/useGrid';

export type RoomMode = 'readonly' | 'preview' | 'edit';

export interface RoomTransform {
  scale: number;
  position: { x: number; y: number };
}

export interface RoomRenderContext {
  containerRef: RefObject<HTMLDivElement | null>;
  imageRef: RefObject<HTMLImageElement | null>;
  scale: number;
  position: { x: number; y: number };
  gridLines: { x1: number; y1: number; x2: number; y2: number }[];
  gridCells: GridCell[];
  surfacePolygon: SurfacePolygon | null;
}

export type RoomRenderable = ReactNode | ((context: RoomRenderContext) => ReactNode);

export interface RoomBaseProps {
  mode?: RoomMode;
  gridType: TabId | null;
  initialTransform?: Partial<RoomTransform>;
  onTransformChange?: (transform: RoomTransform) => void;
  children?: RoomRenderable;
  panEnabled?: boolean;
}

const renderRoomChildren = (
  renderable: RoomRenderable | undefined,
  context: RoomRenderContext,
): ReactNode => {
  if (!renderable) {
    return null;
  }
  return typeof renderable === 'function' ? renderable(context) : renderable;
};

const RoomBase = ({
  mode = 'readonly',
  gridType,
  onTransformChange,
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

  useEffect(() => {
    if (!onTransformChange) {
      return;
    }
    onTransformChange({ scale, position });
  }, [scale, position, onTransformChange]);

  const { gridLines, gridCells, surfacePolygon } = useGrid({
    scale,
    position,
    roomImageRef: imageRef,
    containerRef,
    gridType,
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
