import { useCallback, useEffect, useMemo, useRef } from 'react';
import type { MouseEvent as ReactMouseEvent, TouchEvent as ReactTouchEvent } from 'react';
import type { RoomRenderContext } from '@/features/game/room/RoomBase';
import { useDecoStore } from '@/features/deco/state/deco.store';
import { usePlacementController } from '@/features/deco/hooks/usePlacementController';
import { buildFootprint } from '@/features/deco/utils/grid';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import PlacedItem from './PlacedItem';
import GhostItem from './GhostItem';
import { useGrid } from '@/features/game/room/hooks/useGrid';

interface RoomCanvasProps {
  context: RoomRenderContext;
  onAutoPlacementFail?: () => void;
  allowItemPickup?: boolean;
}

// RoomCanvas는 드래그 중/확정된 아이템 스프라이트와 하이라이트를 관리하는 캔버스 레이어다.
const RoomCanvas = ({ context, onAutoPlacementFail, allowItemPickup = true }: RoomCanvasProps) => {
  const { containerRef, gridCells, scale } = context;

  const allLeft = useGrid({
    scale,
    position: context.position,
    roomImageRef: context.imageRef,
    containerRef: context.containerRef,
    gridType: 'leftWall',
  });
  const allRight = useGrid({
    scale,
    position: context.position,
    roomImageRef: context.imageRef,
    containerRef: context.containerRef,
    gridType: 'rightWall',
  });
  const allFloor = useGrid({
    scale,
    position: context.position,
    roomImageRef: context.imageRef,
    containerRef: context.containerRef,
    gridType: 'floor',
  });

  const dragSession = useDecoStore((state) => state.dragSession);
  const draftItems = useDecoStore((state) => state.draftItems);
  const startDragFromPlaced = useDecoStore((state) => state.startDragFromPlaced);
  const cancelDrag = useDecoStore((state) => state.cancelDrag);
  const setScale = useDecoStore((state) => state.setScale);

  useEffect(() => {
    setScale(scale);
  }, [scale, setScale]);

  const handlePlacementFail = useCallback(() => {
    cancelDrag();
    onAutoPlacementFail?.();
  }, [cancelDrag, onAutoPlacementFail]);

  const { ghost, handlePointerMove, commitPlacement } = usePlacementController({
    gridCells,
    onAutoPlacementFail: handlePlacementFail,
  });

  // 그리드 탭을 이동하더라도 기존 배치 좌표를 잃지 않기 위해
  // 모든 면(left/right/floor)의 셀 정보를 모아 Map으로 보관한다.
  const cellMap = useMemo(() => {
    const map = new Map<string, typeof gridCells[number]>();
    [...allLeft.gridCells, ...allRight.gridCells, ...allFloor.gridCells].forEach((cell) => {
      map.set(cell.id, cell);
    });
    return map;
  }, [allFloor.gridCells, allLeft.gridCells, allRight.gridCells, gridCells]);

  // footprint 셀 ID 배열을 받아 실제 이미지 배치에 필요한 좌표/크기를 계산한다.
  const computeSprite = useCallback(
    ({
      id,
      footprintIds,
      rotation,
      itemId,
      imageUrl,
      xLength,
      yLength,
    }: {
      id: string;
      footprintIds: string[];
      rotation: number;
      itemId: number;
      imageUrl?: string;
      xLength: number;
      yLength: number;
    }) => {
      const footprintCells = footprintIds
        .map((cellId) => cellMap.get(cellId))
        .filter((cell): cell is typeof gridCells[number] => Boolean(cell));

      if (!footprintCells.length) {
        return null;
      }

      const vertices = footprintCells.flatMap((cell) => cell.vertices);
      const xs = vertices.map((v) => v.x);
      const ys = vertices.map((v) => v.y);
      const minX = Math.min(...xs);
      const maxX = Math.max(...xs);
      const minY = Math.min(...ys);
      const maxY = Math.max(...ys);

      const firstCell = footprintCells[0];
      const baseWidth = Math.abs(firstCell.vertices[1].x - firstCell.vertices[0].x);
      const baseHeight = Math.abs(firstCell.vertices[0].y - firstCell.vertices[3].y);
      const width = Math.max(maxX - minX, baseWidth * Math.max(xLength, 1));
      const height = Math.max(maxY - minY, baseHeight * Math.max(yLength, 1));
      const spriteImage = imageUrl ?? getItemImage(itemId);
      const centerX = minX + width / 2;
      const centerY = minY + height / 2;

      return {
        id,
        imageUrl: spriteImage,
        x: minX,
        y: minY,
        width,
        height,
        rotation,
        centerX,
        centerY,
      };
    },
    [cellMap, gridCells],
  );

  const ghostPolygons = useMemo(() => {
    if (!ghost.footprintCellIds.length) {
      return [];
    }
    return ghost.footprintCellIds
      .map((cellId) => cellMap.get(cellId))
      .filter((cell): cell is typeof gridCells[number] => Boolean(cell))
      .map((cell) => cell.vertices.map(({ x, y }) => `${x},${y}`).join(' '));
  }, [cellMap, ghost.footprintCellIds]);

  const draftPolygons = useMemo(() => {
    return draftItems.map((item) => {
      const footprint =
        item.footprintCellIds && item.footprintCellIds.length > 0
          ? item.footprintCellIds
          : buildFootprint(item.cellId, item.xLength, item.yLength);
      const polygons = footprint
        .map((cellId) => cellMap.get(cellId))
        .filter((cell): cell is typeof gridCells[number] => Boolean(cell))
        .map((cell) => cell.vertices.map(({ x, y }) => `${x},${y}`).join(' '));
      return {
        id: item.id,
        polygons,
      };
    });
  }, [cellMap, draftItems]);

  const spriteData = useMemo(() => {
    return draftItems
      .map((item) => {
        const footprintIds =
          item.footprintCellIds && item.footprintCellIds.length > 0
            ? item.footprintCellIds
            : buildFootprint(item.cellId, item.xLength, item.yLength);
        return computeSprite({
          id: item.id,
          footprintIds,
          rotation: item.rotation ?? 0,
          itemId: item.itemId,
          imageUrl: item.imageUrl,
          xLength: item.xLength ?? 1,
          yLength: item.yLength ?? 1,
        });
      })
      .filter((sprite): sprite is {
        id: string;
        imageUrl: string;
        x: number;
        y: number;
        width: number;
        height: number;
        rotation: number;
        centerX: number;
        centerY: number;
      } => Boolean(sprite));
  }, [computeSprite, draftItems]);

  // 드래그 중에는 hover 상태의 footprint를 사용해 임시 스프라이트를 만든다.
  const ghostSprite = useMemo(() => {
    if (!dragSession) {
      return null;
    }
    const footprintIds = ghost.footprintCellIds.length
      ? ghost.footprintCellIds
      : ghost.ghostCellId
        ? buildFootprint(ghost.ghostCellId, dragSession.xLength ?? 1, dragSession.yLength ?? 1)
        : [];

    if (!footprintIds.length) {
      return null;
    }

    return computeSprite({
      id: `${dragSession.itemId}-dragging`,
      footprintIds,
      rotation: dragSession.originalItem?.rotation ?? 0,
      itemId: Number(dragSession.itemId),
      imageUrl: dragSession.imageUrl,
      xLength: dragSession.xLength ?? 1,
      yLength: dragSession.yLength ?? 1,
    });
  }, [computeSprite, dragSession, ghost.footprintCellIds, ghost.ghostCellId]);

  const projectToOverlay = useCallback(
    (clientX: number, clientY: number) => {
      const rect = containerRef.current?.getBoundingClientRect();
      const width = containerRef.current?.clientWidth ?? 0;
      const height = containerRef.current?.clientHeight ?? 0;
      const x = rect ? clientX - rect.left + width : clientX;
      const y = rect ? clientY - rect.top + height : clientY;
      handlePointerMove(x, y);
    },
    [containerRef, handlePointerMove],
  );

  const isPointerActiveRef = useRef(false);

  // 마우스로 고스트를 집어 들 때 실행된다.
  const handleMouseDown = (event: ReactMouseEvent<HTMLDivElement>) => {
    if (!dragSession) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    isPointerActiveRef.current = true;
    projectToOverlay(event.clientX, event.clientY);
  };

  // 드래그 중 위치를 계속 업데이트한다.
  const handleMouseMove = (event: ReactMouseEvent<HTMLDivElement>) => {
    if (!dragSession || !isPointerActiveRef.current) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    projectToOverlay(event.clientX, event.clientY);
  };

  // 드래그가 끝나면 commitPlacement를 호출해 확정하거나
  // 실패 시 cancelDrag로 상태를 롤백한다.
  const finalizePlacement = () => {
    if (!dragSession) {
      return;
    }
    const placed = commitPlacement();
    if (!placed) {
      cancelDrag();
    }
    isPointerActiveRef.current = false;
  };

  const handleMouseUp = (event: ReactMouseEvent<HTMLDivElement>) => {
    if (!dragSession) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    finalizePlacement();
  };

  // 포인터가 캔버스를 벗어나면 드래그 상태만 해제한다.
  const handleMouseLeave = () => {
    isPointerActiveRef.current = false;
  };

  const activeTouchIdRef = useRef<number | null>(null);

  // 터치 환경에서도 마우스와 동일한 흐름으로 고스트를 제어한다.
  const handleTouchStart = (event: ReactTouchEvent<HTMLDivElement>) => {
    if (!dragSession) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    const touch = event.changedTouches[0];
    activeTouchIdRef.current = touch.identifier;
    projectToOverlay(touch.clientX, touch.clientY);
  };

  // 멀티 터치 중 현재 드래그에 해당하는 터치를 조회한다.
  const findActiveTouch = (touches: TouchList) => {
    if (activeTouchIdRef.current === null) {
      return null;
    }
    return Array.from(touches).find((touch) => touch.identifier === activeTouchIdRef.current);
  };

  const handleTouchMove = (event: ReactTouchEvent<HTMLDivElement>) => {
    if (!dragSession) {
      return;
    }
    const touch = findActiveTouch(event.changedTouches);
    if (!touch) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    projectToOverlay(touch.clientX, touch.clientY);
  };

  const handleTouchEnd = (event: ReactTouchEvent<HTMLDivElement>) => {
    if (!dragSession) {
      return;
    }
    const touch = findActiveTouch(event.changedTouches);
    if (!touch) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    finalizePlacement();
    activeTouchIdRef.current = null;
  };

  const handleTouchCancel = () => {
    activeTouchIdRef.current = null;
    isPointerActiveRef.current = false;
  };

  const handlePickPlaced = ({ id, clientX, clientY }: { id: string; clientX: number; clientY: number }) => {
    if (!allowItemPickup) {
      return;
    }
    startDragFromPlaced(id);
    isPointerActiveRef.current = true;
    projectToOverlay(clientX, clientY);
  };

  return (
    <div
      className="absolute inset-0"
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseLeave}
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
      onTouchEnd={handleTouchEnd}
      onTouchCancel={handleTouchCancel}
    >
      <svg
        className="absolute inset-0"
        style={{
          width: '300%',
          height: '300%',
          left: '-100%',
          top: '-100%',
        }}
      >
        {/* 확정된 아이템 스프라이트 */}
        {spriteData.map((sprite) => (
          <image
            key={`${sprite.id}-image`}
            href={sprite.imageUrl}
            xlinkHref={sprite.imageUrl}
            x={sprite.x}
            y={sprite.y}
            width={sprite.width}
            height={sprite.height}
            preserveAspectRatio="xMidYMax meet"
            transform={
              sprite.rotation
                ? `rotate(${sprite.rotation}, ${sprite.centerX}, ${sprite.centerY})`
                : undefined
            }
            style={{ pointerEvents: 'none' }}
          />
        ))}
        {/* 드래그 중 고스트 스프라이트 (유효할 때만 표시) */}
        {ghostSprite && ghost.isValid ? (
          <image
            key={`${ghostSprite.id}-image`}
            href={ghostSprite.imageUrl}
            xlinkHref={ghostSprite.imageUrl}
            x={ghostSprite.x}
            y={ghostSprite.y}
            width={ghostSprite.width}
            height={ghostSprite.height}
            preserveAspectRatio="xMidYMax meet"
            transform={
              ghostSprite.rotation
                ? `rotate(${ghostSprite.rotation}, ${ghostSprite.centerX}, ${ghostSprite.centerY})`
                : undefined
            }
            opacity={0.6}
            style={{ pointerEvents: 'none' }}
          />
        ) : null}
        <GhostItem polygons={ghostPolygons} isValid={ghost.isValid} />
        {/* 드래그 중인 아이템만 하이라이트를 유지한다 */}
        {draftPolygons.map((item) => (
          <PlacedItem
            key={item.id}
            id={item.id}
            polygons={item.polygons}
            onPick={handlePickPlaced}
            visible={Boolean(dragSession && dragSession.originPlacedId === item.id)}
          />
        ))}
      </svg>
    </div>
  );
};

export default RoomCanvas;
