import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
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
  showActions?: boolean;
}

// RoomCanvas는 드래그 중/확정된 아이템 스프라이트와 하이라이트를 관리하는 캔버스 레이어다.
const RoomCanvas = ({
  context,
  onAutoPlacementFail,
  allowItemPickup = true,
  showActions = true,
}: RoomCanvasProps) => {
  const { containerRef, gridCells, scale, surfacePolygon } = context;

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
  const pendingPlacement = useDecoStore((state) => state.pendingPlacement);
  const commitPlacement = useDecoStore((state) => state.commitPlacement);
  const cancelPendingPlacement = useDecoStore((state) => state.cancelPendingPlacement);
  const startDragFromPlaced = useDecoStore((state) => state.startDragFromPlaced);
  const cancelDrag = useDecoStore((state) => state.cancelDrag);
  const setScale = useDecoStore((state) => state.setScale);

  const imageSizeCacheRef = useRef(new Map<string, { width: number; height: number }>());
  const loadingImagesRef = useRef(new Set<string>());
  const [, forceRender] = useState(0);

  useEffect(() => {
    setScale(scale);
  }, [scale, setScale]);

  const handlePlacementFail = useCallback(() => {
    cancelDrag();
    onAutoPlacementFail?.();
  }, [cancelDrag, onAutoPlacementFail]);

  const { ghost, handlePointerMove, stagePlacement } = usePlacementController({
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
  const ensureImageSize = useCallback(
    (imageUrl: string | undefined) => {
      if (!imageUrl) {
        return null;
      }
      const cache = imageSizeCacheRef.current;
      const cached = cache.get(imageUrl);
      if (cached) {
        return cached;
      }
      if (typeof window === 'undefined') {
        return null;
      }
      if (loadingImagesRef.current.has(imageUrl)) {
        return null;
      }
      loadingImagesRef.current.add(imageUrl);
      const img = new Image();
      const finalize = () => {
        img.onload = null;
        img.onerror = null;
        if (!cache.has(imageUrl)) {
          cache.set(imageUrl, { width: img.naturalWidth, height: img.naturalHeight });
          forceRender((value) => value + 1);
        }
        loadingImagesRef.current.delete(imageUrl);
      };
      img.onload = finalize;
      img.onerror = () => {
        loadingImagesRef.current.delete(imageUrl);
      };
      img.src = imageUrl;
      if (img.complete && img.naturalWidth > 0 && img.naturalHeight > 0) {
        finalize();
        return cache.get(imageUrl) ?? null;
      }
      return null;
    },
    [forceRender],
  );

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
      const edgeDistance = (a: { x: number; y: number }, b: { x: number; y: number }) => {
        const dx = a.x - b.x;
        const dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
      };
      const topWidth = edgeDistance(firstCell.vertices[1], firstCell.vertices[0]);
      const bottomWidth = edgeDistance(firstCell.vertices[2], firstCell.vertices[3]);
      const baseWidth = Math.max(topWidth, bottomWidth);
      const baseHeight = Math.abs(firstCell.vertices[0].y - firstCell.vertices[3].y);
      const spriteImage = imageUrl ?? getItemImage(itemId);
      const intrinsicSize = ensureImageSize(spriteImage);
      const footprintWidth = maxX - minX;
      const width = Math.max(footprintWidth, baseWidth * Math.max(xLength, 1));
      const desiredHeight = intrinsicSize
        ? Math.round((intrinsicSize.height / intrinsicSize.width) * width)
        : Math.max(maxY - minY, baseHeight * Math.max(yLength, 1));
      const minHeight = baseHeight * Math.max(yLength, 1);
      const height = Math.max(desiredHeight, minHeight);
      const y = Math.min(minY, maxY - height);
      const centerX = minX + width / 2;
      const centerY = y + height / 2;

      return {
        id,
        imageUrl: spriteImage,
        x: minX,
        y,
        width,
        height,
        rotation,
        centerX,
        centerY,
      };
    },
    [cellMap, ensureImageSize, gridCells],
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

  const draftMap = useMemo(() => {
    return new Map(draftItems.map((item) => [item.id, item]));
  }, [draftItems]);

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
    const sprites = draftItems
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

    const sortSprites = (items: typeof sprites) =>
      items.sort((a, b) => {
        const draftA = draftMap.get(a.id);
        const draftB = draftMap.get(b.id);
        const rowA = draftA?.positionY ?? 0;
        const rowB = draftB?.positionY ?? 0;
        if (rowA !== rowB) {
          return rowB - rowA;
        }
        const colA = draftA?.positionX ?? 0;
        const colB = draftB?.positionX ?? 0;
        return colB - colA;
      });

    const leftSprites: typeof sprites = [];
    const rightSprites: typeof sprites = [];
    const floorSprites: typeof sprites = [];
    const otherSprites: typeof sprites = [];

    sprites.forEach((sprite) => {
      const draft = draftMap.get(sprite.id);
      switch (draft?.layer) {
        case 'leftWall':
          leftSprites.push(sprite);
          break;
        case 'rightWall':
          rightSprites.push(sprite);
          break;
        case 'floor':
          floorSprites.push(sprite);
          break;
        default:
          otherSprites.push(sprite);
      }
    });

    sortSprites(leftSprites);
    sortSprites(rightSprites);
    sortSprites(floorSprites);

    return [...leftSprites, ...rightSprites, ...floorSprites, ...otherSprites];
  }, [computeSprite, draftItems, draftMap]);

  useEffect(() => {
    if (!dragSession) {
      return;
    }
    if (!ghost.isValid || !ghost.ghostCellId) {
      return;
    }
    stagePlacement();
  }, [dragSession, ghost, stagePlacement]);

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

  const pendingSprite = useMemo(() => {
    const source = pendingPlacement ?? (ghost.isValid && ghostSprite
      ? {
          id: `${dragSession?.itemId ?? 'ghost'}-pending`,
          cellId: ghost.ghostCellId ?? '',
          footprintCellIds: ghost.footprintCellIds,
          rotation: dragSession?.originalItem?.rotation ?? 0,
          itemId: Number(dragSession?.itemId ?? 0),
          imageUrl: dragSession?.imageUrl,
          xLength: dragSession?.xLength ?? 1,
          yLength: dragSession?.yLength ?? 1,
        }
      : null);
    if (!source) {
      return null;
    }
    const footprintIds =
      source.footprintCellIds && source.footprintCellIds.length > 0
        ? source.footprintCellIds
        : buildFootprint(source.cellId, source.xLength, source.yLength);
    return computeSprite({
      id: source.id,
      footprintIds,
      rotation: source.rotation ?? 0,
      itemId: source.itemId,
      imageUrl: source.imageUrl,
      xLength: source.xLength,
      yLength: source.yLength,
    });
  }, [computeSprite, dragSession, ghost, ghostSprite, pendingPlacement]);

  const isPointerActiveRef = useRef(false);
  const [isPointerActive, setIsPointerActive] = useState(false);

  const actionAnchor = useMemo(() => {
    if (!showActions) {
      return null;
    }
    if (isPointerActive) {
      return ghost.isValid && ghostSprite ? ghostSprite : null;
    }
    if (pendingSprite) {
      return pendingSprite;
    }
    if (ghost.isValid && ghostSprite) {
      return ghostSprite;
    }
    return null;
  }, [ghost.isValid, ghostSprite, isPointerActive, pendingSprite, showActions]);

  const showActionButtons = Boolean(
    showActions && actionAnchor && (isPointerActive ? ghost.isValid : pendingPlacement || ghost.isValid),
  );

  const containerWidth = containerRef.current?.clientWidth ?? 0;
  const containerHeight = containerRef.current?.clientHeight ?? 0;
  const actionPosition = actionAnchor
    ? {
        left: actionAnchor.centerX - containerWidth,
        top: actionAnchor.y + actionAnchor.height + 6 - containerHeight,
      }
    : null;

  const handleConfirm = useCallback(() => {
    if (pendingPlacement) {
      commitPlacement();
      return;
    }
    const staged = stagePlacement();
    if (staged) {
      commitPlacement();
    }
  }, [commitPlacement, pendingPlacement, stagePlacement]);

  const handleCancel = useCallback(() => {
    cancelPendingPlacement();
  }, [cancelPendingPlacement]);

  const toOverlayCoords = useCallback(
    (clientX: number, clientY: number) => {
      const rect = containerRef.current?.getBoundingClientRect();
      const width = containerRef.current?.clientWidth ?? 0;
      const height = containerRef.current?.clientHeight ?? 0;
      return {
        x: rect ? clientX - rect.left + width : clientX,
        y: rect ? clientY - rect.top + height : clientY,
      };
    },
    [containerRef],
  );

  const isInsideSurface = useCallback(
    (clientX: number, clientY: number) => {
      if (!surfacePolygon) {
        return true;
      }
      const { x, y } = toOverlayCoords(clientX, clientY);
      let hasPositive = false;
      let hasNegative = false;
      for (let i = 0; i < surfacePolygon.length; i += 1) {
        const current = surfacePolygon[i];
        const next = surfacePolygon[(i + 1) % surfacePolygon.length];
        const cross = (next.x - current.x) * (y - current.y) - (next.y - current.y) * (x - current.x);
        if (cross > 0) {
          hasPositive = true;
        } else if (cross < 0) {
          hasNegative = true;
        }
        if (hasPositive && hasNegative) {
          return false;
        }
      }
      return true;
    },
    [surfacePolygon, toOverlayCoords],
  );

  const projectToOverlay = useCallback(
    (clientX: number, clientY: number) => {
      const { x, y } = toOverlayCoords(clientX, clientY);
      handlePointerMove(x, y);
    },
    [handlePointerMove, toOverlayCoords],
  );

  const shouldCapturePointer = useCallback(
    (clientX: number, clientY: number) => {
      if (!dragSession) {
        return false;
      }
      if (pendingPlacement) {
        return isInsideSurface(clientX, clientY);
      }
      return true;
    },
    [dragSession, pendingPlacement, isInsideSurface],
  );

  const handleMouseDownCapture = (event: ReactMouseEvent<HTMLDivElement>) => {
    const target = event.target as HTMLElement | null;
    if (target && target.closest('[data-room-action="true"]')) {
      return;
    }
    if (!shouldCapturePointer(event.clientX, event.clientY)) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
    projectToOverlay(event.clientX, event.clientY);
  };

  // 마우스로 고스트를 집어 들 때 실행된다.
  const handleMouseDown = (event: ReactMouseEvent<HTMLDivElement>) => {
    const target = event.target as HTMLElement | null;
    if (target && target.closest('[data-room-action="true"]')) {
      return;
    }
    if (!shouldCapturePointer(event.clientX, event.clientY)) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
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

  // 드래그가 끝나면 후보 상태로만 stagePlacement를 호출하고
  // 실패 시 cancelDrag로 상태를 롤백한다.
  const finalizePlacement = () => {
    if (!dragSession) {
      return;
    }
    const staged = stagePlacement();
    if (!staged) {
      cancelDrag();
    }
    isPointerActiveRef.current = false;
    setIsPointerActive(false);
  };

  const handleMouseUp = (event: ReactMouseEvent<HTMLDivElement>) => {
    if (!dragSession || !isPointerActiveRef.current) {
      return;
    }
    event.preventDefault();
    finalizePlacement();
  };

  // 포인터가 캔버스를 벗어나면 드래그 상태만 해제한다.
  const handleMouseLeave = () => {
    isPointerActiveRef.current = false;
    setIsPointerActive(false);
  };

  const activeTouchIdRef = useRef<number | null>(null);

  // 터치 환경에서도 마우스와 동일한 흐름으로 고스트를 제어한다.
  const handleTouchStartCapture = (event: ReactTouchEvent<HTMLDivElement>) => {
    const touch = event.changedTouches[0];
    const target = event.target as HTMLElement | null;
    if (target && target.closest('[data-room-action="true"]')) {
      return;
    }
    if (!touch || !shouldCapturePointer(touch.clientX, touch.clientY)) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    activeTouchIdRef.current = touch.identifier;
    projectToOverlay(touch.clientX, touch.clientY);
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
  };

  const handleTouchStart = (event: ReactTouchEvent<HTMLDivElement>) => {
    const touch = event.changedTouches[0];
    const target = event.target as HTMLElement | null;
    if (target && target.closest('[data-room-action="true"]')) {
      return;
    }
    if (!touch || !shouldCapturePointer(touch.clientX, touch.clientY)) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    activeTouchIdRef.current = touch.identifier;
    projectToOverlay(touch.clientX, touch.clientY);
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
  };

  // 멀티 터치 중 현재 드래그에 해당하는 터치를 조회한다.
  const findActiveTouch = (
    touches: ReactTouchEvent<HTMLDivElement>['changedTouches'],
  ) => {
    if (activeTouchIdRef.current === null) {
      return null;
    }
    for (let index = 0; index < touches.length; index += 1) {
      const touch = touches.item(index);
      if (touch && touch.identifier === activeTouchIdRef.current) {
        return touch;
      }
    }
    return null;
  };

  const handleTouchMove = (event: ReactTouchEvent<HTMLDivElement>) => {
    if (!dragSession || !isPointerActiveRef.current) {
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
    if (!dragSession || !isPointerActiveRef.current) {
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
    setIsPointerActive(false);
  };

  const handleTouchCancel = () => {
    activeTouchIdRef.current = null;
    isPointerActiveRef.current = false;
    setIsPointerActive(false);
  };

  const handlePickPlaced = ({ id, clientX, clientY }: { id: string; clientX: number; clientY: number }) => {
    if (!allowItemPickup) {
      return;
    }
    startDragFromPlaced(id);
    isPointerActiveRef.current = true;
    projectToOverlay(clientX, clientY);
  };

  const overlayActive = Boolean(
    dragSession || pendingPlacement || showActionButtons || isPointerActive,
  );

  return (
    <div
      className="absolute inset-0"
      style={{ pointerEvents: overlayActive ? 'auto' : 'none' }}
      onMouseDownCapture={handleMouseDownCapture}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseLeave}
      onTouchStartCapture={handleTouchStartCapture}
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
      onTouchEnd={handleTouchEnd}
      onTouchCancel={handleTouchCancel}
    >
      {showActionButtons && actionAnchor && actionPosition ? (
        <div className="pointer-events-none absolute inset-0">
          <div
            className="pointer-events-auto flex gap-1.5 rounded-lg bg-white/90 px-3 py-1.5 text-xs font-semibold shadow-lg backdrop-blur"
            data-room-action="true"
            style={{
              position: 'absolute',
              left: `${actionPosition.left}px`,
              top: `${actionPosition.top}px`,
              transform: 'translate(-50%, 0)',
              zIndex: 10,
            }}
            onMouseDown={(event) => event.stopPropagation()}
            onTouchStart={(event) => event.stopPropagation()}
          >
            <button
              type="button"
              onClick={handleCancel}
              className="rounded-md bg-gray-200 px-2.5 py-1 text-gray-700 transition hover:bg-gray-300 disabled:cursor-not-allowed disabled:opacity-50 whitespace-nowrap"
            >
              취소
            </button>
            <button
              type="button"
              onClick={handleConfirm}
              disabled={!pendingPlacement}
              className="rounded-md bg-primary px-2.5 py-1 text-white transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50 whitespace-nowrap"
            >
              배치
            </button>
          </div>
        </div>
      ) : null}
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
