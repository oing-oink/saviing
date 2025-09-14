import roomImage from '@/assets/room.png';
import { useRef } from 'react';
import { useGestures } from './hooks/useGestures';

const Room = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const imageRef = useRef<HTMLImageElement>(null);

  const { scale, position, gestureHandlers } = useGestures({
    containerRef,
    targetRef: imageRef,
    minScale: 1,
    maxScale: 4,
  });

  return (
    <div
      ref={containerRef}
      className="touch-none"
      style={{ cursor: scale > 1 ? 'grab' : 'default' }}
      {...gestureHandlers}
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
      />
    </div>
  );
};

export default Room;
