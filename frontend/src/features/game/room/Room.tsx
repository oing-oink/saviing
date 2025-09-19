import roomImage from '@/assets/room.png';
import { useRef, useEffect } from 'react';
import { useGestures } from './hooks/useGestures';
import { useGrid } from './hooks/useGrid';
import type { TabId } from 'src/features/game/shop/types/item';

// 외부로부터 전달받는 gridType과 transform 값을 전달하는 콜백
interface RoomProps {
  gridType: TabId | null;
  onTransformChange?: (transform: { scale: number; position: { x: number; y: number } }) => void;
}

const Room = ({ gridType, onTransformChange }: RoomProps) => {
  // 방 컴포넌트가 제스처와 그리드에 필요한 모든 로직을 내부적으로 관리
  const containerRef = useRef<HTMLDivElement>(null);
  const imageRef = useRef<HTMLImageElement>(null);

  const { scale, position } = useGestures({
    containerRef,
    targetRef: imageRef,
  });

  // transform 값(크기, 위치)이 변경될 때마다 부모에게 알림
  useEffect(() => {
    if (onTransformChange) {
      onTransformChange({ scale, position });
    }
  }, [scale, position, onTransformChange]);

  // useGrid는 부모로부터 받은 gridType prop으로 제어됨.
  const { gridLines } = useGrid({
    scale,
    position,
    roomImageRef: imageRef,
    containerRef,
    gridType,
  });

  return (
    <div className="relative">
      {/* SVG 그리드 오버레이 */}
      <svg
        className="pointer-events-none absolute z-1"
        style={{
          width: '300%',
          height: '300%',
          left: '-100%',
          top: '-100%',
          stroke: 'rgba(255, 38, 38, 0.7)',
          strokeWidth: 1,
        }}
      >
        {gridLines.map((line, index) => (
          <line key={index} {...line} />
        ))}
      </svg>

      {/* 방 이미지와 제스처를 위한 컨테이너 */}
      <div
        ref={containerRef}
        className="touch-none"
        style={{ cursor: scale > 1 ? 'grab' : 'default' }}
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
      </div>
    </div>
  );
};

export default Room;
