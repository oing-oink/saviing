import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import type {
  MouseEvent as ReactMouseEvent,
  TouchEvent as ReactTouchEvent,
} from 'react';
import type { RoomRenderContext } from '@/features/game/room/RoomBase';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import { usePlacementController } from '@/features/game/deco/hooks/usePlacementController';
import {
  buildFootprint,
  normalizePlacementArea,
} from '@/features/game/deco/utils/grid';
import { getItemImage } from '@/features/game/shop/utils/getItemImage';
import type { PlacedItem as PlacedItemType } from '@/features/game/deco/types/decoTypes';
import PlacedItem from './PlacedItem';
import GhostItem from './GhostItem';
import { useGrid } from '@/features/game/room/hooks/useGrid';
import CatSprite from '@/features/game/pet/components/CatSprite';
import { CAT_ANIMATIONS } from '@/features/game/pet/data/catAnimations';
import type { PetAnimationState } from '@/features/game/pet/types/petTypes';

/**
 * RoomCanvas 컴포넌트에서 필요한 컨텍스트와 동작 제어 옵션.
 */
interface RoomCanvasProps {
  context: RoomRenderContext;
  onAutoPlacementFail?: () => void;
  allowItemPickup?: boolean;
  allowItemPickupPredicate?: (item: PlacedItemType) => boolean;
  showActions?: boolean;
  pickupOnlyPreview?: boolean;
  allowDelete?: boolean;
  deleteOnlyPreview?: boolean;
  onPlacedItemClick?: (payload: {
    item: PlacedItemType;
    clientX: number;
    clientY: number;
  }) => void;
  catAnimationState?: PetAnimationState;
  onCatAnimationComplete?: (animation: PetAnimationState) => void;
}

/**
 * 드래그 중인 아이템과 확정된 배치를 시각화하고 배치 액션을 처리하는 캔버스 레이어.
 */
const RoomCanvas = ({
  context,
  onAutoPlacementFail,
  allowItemPickup = true,
  allowItemPickupPredicate,
  showActions = true,
  pickupOnlyPreview = false,
  allowDelete = true,
  deleteOnlyPreview = false,
  onPlacedItemClick,
  catAnimationState,
  onCatAnimationComplete,
}: RoomCanvasProps) => {
  const { containerRef, gridCells, scale, surfacePolygon } = context;

  const allLeft = useGrid({
    scale,
    position: context.position,
    roomImageRef: context.imageRef,
    containerRef: context.containerRef,
    placementArea: 'LEFT',
  });
  const allRight = useGrid({
    scale,
    position: context.position,
    roomImageRef: context.imageRef,
    containerRef: context.containerRef,
    placementArea: 'RIGHT',
  });
  const allFloor = useGrid({
    scale,
    position: context.position,
    roomImageRef: context.imageRef,
    containerRef: context.containerRef,
    placementArea: 'BOTTOM',
  });

  const dragSession = useDecoStore(state => state.dragSession);
  const draftItems = useDecoStore(state => state.draftItems);
  const pendingPlacement = useDecoStore(state => state.pendingPlacement);

  const commitPlacement = useDecoStore(state => state.commitPlacement);
  const cancelPendingPlacement = useDecoStore(
    state => state.cancelPendingPlacement,
  );
  const startDragFromPlaced = useDecoStore(state => state.startDragFromPlaced);
  const cancelDrag = useDecoStore(state => state.cancelDrag);
  const removeDraftItem = useDecoStore(state => state.removeDraftItem);
  const deleteDraggedItem = useDecoStore(state => state.deleteDraggedItem);
  const setScale = useDecoStore(state => state.setScale);

  const imageSizeCacheRef = useRef(
    new Map<string, { width: number; height: number }>(),
  );
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
    const map = new Map<string, (typeof gridCells)[number]>();
    [
      ...allLeft.gridCells,
      ...allRight.gridCells,
      ...allFloor.gridCells,
    ].forEach(cell => {
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
          cache.set(imageUrl, {
            width: img.naturalWidth,
            height: img.naturalHeight,
          });
          forceRender(value => value + 1);
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
        .map(cellId => cellMap.get(cellId))
        .filter((cell): cell is (typeof gridCells)[number] => Boolean(cell));

      if (!footprintCells.length) {
        return null;
      }

      const vertices = footprintCells.flatMap(cell => cell.vertices);
      const xs = vertices.map(v => v.x);
      const ys = vertices.map(v => v.y);
      const minX = Math.min(...xs);
      const maxX = Math.max(...xs);
      const minY = Math.min(...ys);
      const maxY = Math.max(...ys);

      const firstCell = footprintCells[0];
      const edgeDistance = (
        a: { x: number; y: number },
        b: { x: number; y: number },
      ) => {
        const dx = a.x - b.x;
        const dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
      };
      const topWidth = edgeDistance(
        firstCell.vertices[1],
        firstCell.vertices[0],
      );
      const bottomWidth = edgeDistance(
        firstCell.vertices[2],
        firstCell.vertices[3],
      );
      const baseWidth = Math.max(topWidth, bottomWidth);
      const baseHeight = Math.abs(
        firstCell.vertices[0].y - firstCell.vertices[3].y,
      );
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
      .map(cellId => cellMap.get(cellId))
      .filter((cell): cell is (typeof gridCells)[number] => Boolean(cell))
      .map(cell => cell.vertices.map(({ x, y }) => `${x},${y}`).join(' '));
  }, [cellMap, ghost.footprintCellIds]);

  const draftMap = useMemo(() => {
    return new Map(draftItems.map(item => [item.id, item]));
  }, [draftItems]);

  const draftPolygons = useMemo(() => {
    return draftItems.map(item => {
      const footprint =
        item.footprintCellIds && item.footprintCellIds.length > 0
          ? item.footprintCellIds
          : buildFootprint(item.cellId, item.xLength, item.yLength);
      const polygons = footprint
        .map(cellId => cellMap.get(cellId))
        .filter((cell): cell is (typeof gridCells)[number] => Boolean(cell))
        .map(cell => cell.vertices.map(({ x, y }) => `${x},${y}`).join(' '));
      return {
        id: item.id,
        polygons,
      };
    });
  }, [cellMap, draftItems]);

  const spriteData = useMemo(() => {
    const sprites = draftItems
      .map(item => {
        const footprintIds =
          item.footprintCellIds && item.footprintCellIds.length > 0
            ? item.footprintCellIds
            : buildFootprint(item.cellId, item.xLength, item.yLength);
        const sprite = computeSprite({
          id: item.id,
          footprintIds,
          rotation: item.rotation ?? 0,
          itemId: item.itemId,
          imageUrl: item.imageUrl,
          xLength: item.xLength ?? 1,
          yLength: item.yLength ?? 1,
        });
        if (!sprite) {
          return null;
        }
        return {
          ...sprite,
          itemType: item.itemType ?? 'DECORATION',
          itemId: item.itemId,
        };
      })
      .filter(
        (
          sprite,
        ): sprite is {
          id: string;
          imageUrl: string;
          x: number;
          y: number;
          width: number;
          height: number;
          rotation: number;
          centerX: number;
          centerY: number;
          itemType: string;
          itemId: number;
        } => Boolean(sprite),
      );

    const normalizeLayer = (layer: string | undefined | null) => {
      if (!layer) {
        return null;
      }
      const key = layer.toLowerCase();
      if (key === 'left') {
        return 'leftWall';
      }
      if (key === 'right') {
        return 'rightWall';
      }
      if (key === 'bottom') {
        return 'floor';
      }
      return layer;
    };

    const getLayerPriority = (layer: string | null) => {
      switch (layer) {
        case 'leftWall':
          return 0;
        case 'rightWall':
          return 1;
        case 'floor':
          return 2;
        default:
          return 3;
      }
    };

    sprites.sort((a, b) => {
      const draftA = draftMap.get(a.id);
      const draftB = draftMap.get(b.id);
      const layerA = normalizeLayer(draftA?.layer);
      const layerB = normalizeLayer(draftB?.layer);
      const layerPriorityA = getLayerPriority(layerA);
      const layerPriorityB = getLayerPriority(layerB);
      if (layerPriorityA !== layerPriorityB) {
        return layerPriorityA - layerPriorityB;
      }

      const depthA = a.y + a.height;
      const depthB = b.y + b.height;
      if (depthA !== depthB) {
        return depthA - depthB;
      }

      const rowA = draftA?.positionY ?? 0;
      const rowB = draftB?.positionY ?? 0;
      if (rowA !== rowB) {
        return rowB - rowA;
      }

      const colA = draftA?.positionX ?? 0;
      const colB = draftB?.positionX ?? 0;
      return colB - colA;
    });

    return sprites;
  }, [computeSprite, draftItems, draftMap]);

  const lastStagedCellRef = useRef<string | null>(null);

  const currentPlacementArea = useMemo(() => {
    if (!gridCells.length) {
      return null;
    }
    const normalized = normalizePlacementArea(gridCells[0]?.placementArea);
    return normalized === 'LEFT' ||
      normalized === 'RIGHT' ||
      normalized === 'BOTTOM'
      ? normalized
      : null;
  }, [gridCells]);

  useEffect(() => {
    if (!dragSession) {
      lastStagedCellRef.current = null;
      return;
    }
    if (!ghost.isValid || !ghost.ghostCellId) {
      return;
    }
    if (lastStagedCellRef.current === ghost.ghostCellId) {
      return;
    }
    const staged = stagePlacement();
    if (staged) {
      lastStagedCellRef.current = ghost.ghostCellId;
    }
  }, [dragSession, ghost, stagePlacement]);

  // 드래그 중에는 hover 상태의 footprint를 사용해 임시 스프라이트를 만든다.
  const ghostSprite = useMemo(() => {
    if (!dragSession) {
      return null;
    }
    const footprintIds = ghost.footprintCellIds.length
      ? ghost.footprintCellIds
      : ghost.ghostCellId
        ? buildFootprint(
            ghost.ghostCellId,
            dragSession.xLength ?? 1,
            dragSession.yLength ?? 1,
          )
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
    if (!pendingPlacement) {
      return null;
    }
    const footprintIds =
      pendingPlacement.footprintCellIds && pendingPlacement.footprintCellIds.length > 0
        ? pendingPlacement.footprintCellIds
        : buildFootprint(
            pendingPlacement.cellId,
            pendingPlacement.xLength ?? 1,
            pendingPlacement.yLength ?? 1,
          );
    return computeSprite({
      id: pendingPlacement.id,
      footprintIds,
      rotation: pendingPlacement.rotation ?? 0,
      itemId: pendingPlacement.itemId,
      imageUrl: pendingPlacement.imageUrl,
      xLength: pendingPlacement.xLength ?? 1,
      yLength: pendingPlacement.yLength ?? 1,
    });
  }, [computeSprite, pendingPlacement]);

  const getCatRenderBox = useCallback(
    (
      imageUrl: string | undefined,
      fallbackWidth: number,
      fallbackHeight: number,
      animation: PetAnimationState,
    ) => {
      const metrics = imageUrl ? imageSizeCacheRef.current.get(imageUrl) : null;
      const sheetWidth = metrics?.width ?? fallbackWidth;
      const sheetHeight = metrics?.height ?? fallbackHeight;
      const frameInfo = CAT_ANIMATIONS[animation];
      const frameCount = frameInfo?.frames ?? 1;
      const frameWidth = frameCount > 0 ? sheetWidth / frameCount : sheetWidth;
      const frameHeight = sheetHeight;
      if (frameWidth <= 0 || frameHeight <= 0) {
        return { width: fallbackWidth, height: fallbackHeight };
      }
      const aspect = frameHeight / frameWidth;
      return {
        width: fallbackWidth,
        height: Math.max(fallbackWidth * aspect, fallbackHeight),
      };
    },
    [],
  );

  const isPointerActiveRef = useRef(false);
  const [isPointerActive, setIsPointerActive] = useState(false);
  const hasPointerMovedRef = useRef(false);

  const displayPendingSprite = useMemo(
    () => (!isPointerActive ? pendingSprite : null),
    [isPointerActive, pendingSprite],
  );

  const actionAnchor = useMemo(() => {
    if (!showActions) {
      return null;
    }
    if (ghost.isValid && ghostSprite) {
      return ghostSprite;
    }
    if (displayPendingSprite) {
      return displayPendingSprite;
    }
    if (isPointerActive && ghostSprite) {
      return ghostSprite;
    }
    return null;
  }, [
    displayPendingSprite,
    ghost.isValid,
    ghostSprite,
    isPointerActive,
    showActions,
  ]);

  const deleteTargetId = useMemo(() => {
    if (pendingPlacement) {
      return pendingPlacement.id;
    }
    if (dragSession?.originPlacedId) {
      return dragSession.originPlacedId;
    }
    return null;
  }, [dragSession?.originPlacedId, pendingPlacement]);

  const deleteTargetItem = useMemo(() => {
    if (!deleteTargetId) {
      return null;
    }
    if (pendingPlacement && pendingPlacement.id === deleteTargetId) {
      return pendingPlacement;
    }
    return draftItems.find(item => item.id === deleteTargetId) ?? null;
  }, [deleteTargetId, draftItems, pendingPlacement]);

  const canDelete = Boolean(
    allowDelete &&
      deleteTargetItem &&
      (!deleteOnlyPreview || deleteTargetItem.isPreview),
  );
  const isEditingExisting = Boolean(dragSession?.originPlacedId);
  const showDeleteButton = Boolean(canDelete && isEditingExisting);

  const handleDelete = useCallback(() => {
    if (!allowDelete) {
      return;
    }
    if (!deleteTargetItem) {
      return;
    }
    if (deleteOnlyPreview && !deleteTargetItem.isPreview) {
      return;
    }
    removeDraftItem(deleteTargetItem.id);
    deleteDraggedItem();
    isPointerActiveRef.current = false;
    setIsPointerActive(false);
    lastStagedCellRef.current = null;
  }, [
    allowDelete,
    deleteOnlyPreview,
    deleteTargetItem,
    deleteDraggedItem,
    removeDraftItem,
  ]);

  const showActionButtons = Boolean(
    showActions && actionAnchor && (ghost.isValid || pendingPlacement),
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
        const cross =
          (next.x - current.x) * (y - current.y) -
          (next.y - current.y) * (x - current.x);
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
    if (event.cancelable) {
      event.preventDefault();
    }
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
    hasPointerMovedRef.current = false;
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
    if (event.cancelable) {
      event.preventDefault();
    }
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
    hasPointerMovedRef.current = false;
    projectToOverlay(event.clientX, event.clientY);
  };

  // 드래그 중 위치를 계속 업데이트한다.
  const handleMouseMove = (event: ReactMouseEvent<HTMLDivElement>) => {
    if (!dragSession || !isPointerActiveRef.current) {
      return;
    }
    if (event.cancelable) {
      event.preventDefault();
    }
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    hasPointerMovedRef.current = true;
    projectToOverlay(event.clientX, event.clientY);
  };

  // 드래그가 끝나면 후보 상태로만 stagePlacement를 호출하고
  // 실패 시 cancelDrag로 상태를 롤백한다.
  const finalizePlacement = useCallback(() => {
    if (!dragSession) {
      isPointerActiveRef.current = false;
      setIsPointerActive(false);
      hasPointerMovedRef.current = false;
      return;
    }
    const staged = stagePlacement();
    if (!staged) {
      cancelDrag();
    }
    isPointerActiveRef.current = false;
    setIsPointerActive(false);
    hasPointerMovedRef.current = false;
  }, [dragSession, stagePlacement, cancelDrag]);

  const handleMouseUp = (event: ReactMouseEvent<HTMLDivElement>) => {
    if (!dragSession || !isPointerActiveRef.current) {
      return;
    }
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    if (hasPointerMovedRef.current) {
      finalizePlacement();
    } else {
      isPointerActiveRef.current = false;
      setIsPointerActive(false);
      hasPointerMovedRef.current = false;
    }
  };

  // 포인터가 캔버스를 벗어나면 드래그 상태만 해제한다.
  const handleMouseLeave = () => {
    isPointerActiveRef.current = false;
    setIsPointerActive(false);
    hasPointerMovedRef.current = false;
  };

  useEffect(() => {
    const handleGlobalPointerEnd = () => {
      if (!isPointerActiveRef.current) {
        return;
      }
      if (hasPointerMovedRef.current) {
        finalizePlacement();
      } else {
        isPointerActiveRef.current = false;
        setIsPointerActive(false);
        hasPointerMovedRef.current = false;
      }
    };

    window.addEventListener('mouseup', handleGlobalPointerEnd);
    window.addEventListener('touchend', handleGlobalPointerEnd);
    window.addEventListener('touchcancel', handleGlobalPointerEnd);

    return () => {
      window.removeEventListener('mouseup', handleGlobalPointerEnd);
      window.removeEventListener('touchend', handleGlobalPointerEnd);
      window.removeEventListener('touchcancel', handleGlobalPointerEnd);
    };
  }, [finalizePlacement]);

  const activeTouchIdRef = useRef<number | null>(null);
  const canvasRef = useRef<HTMLDivElement>(null);

  // 터치 환경에서도 마우스와 동일한 흐름으로 고스트를 제어한다.
  const handleTouchStartCapture = useCallback(
    (event: TouchEvent) => {
      const touch = event.changedTouches[0];
      const target = event.target as HTMLElement | null;
      if (target && target.closest('[data-room-action="true"]')) {
        return;
      }
      if (!touch || !shouldCapturePointer(touch.clientX, touch.clientY)) {
        return;
      }
      try {
        event.preventDefault();
      } catch {
        // passive event listener에서는 preventDefault 호출 불가
      }
      event.stopPropagation();
      event.stopImmediatePropagation();
      activeTouchIdRef.current = touch.identifier;
      projectToOverlay(touch.clientX, touch.clientY);
      isPointerActiveRef.current = true;
      setIsPointerActive(true);
      hasPointerMovedRef.current = false;
    },
    [shouldCapturePointer, projectToOverlay, setIsPointerActive],
  );

  // passive: false로 터치 이벤트 등록
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) {
      return;
    }

    canvas.addEventListener('touchstart', handleTouchStartCapture, {
      passive: false,
      capture: true,
    });

    return () => {
      canvas.removeEventListener('touchstart', handleTouchStartCapture, {
        capture: true,
      });
    };
  }, [handleTouchStartCapture]);

  const handleTouchStart = (event: ReactTouchEvent<HTMLDivElement>) => {
    const touch = event.changedTouches[0];
    const target = event.target as HTMLElement | null;
    if (target && target.closest('[data-room-action="true"]')) {
      return;
    }
    if (!touch || !shouldCapturePointer(touch.clientX, touch.clientY)) {
      return;
    }
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    activeTouchIdRef.current = touch.identifier;
    projectToOverlay(touch.clientX, touch.clientY);
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
    hasPointerMovedRef.current = false;
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
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    hasPointerMovedRef.current = true;
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
    event.stopPropagation();
    event.nativeEvent.stopImmediatePropagation();
    if (hasPointerMovedRef.current) {
      finalizePlacement();
    } else {
      isPointerActiveRef.current = false;
      setIsPointerActive(false);
      hasPointerMovedRef.current = false;
    }
    activeTouchIdRef.current = null;
    setIsPointerActive(false);
  };

  const handleTouchCancel = () => {
    activeTouchIdRef.current = null;
    isPointerActiveRef.current = false;
    setIsPointerActive(false);
    hasPointerMovedRef.current = false;
  };

  const handlePickPlaced = ({
    id,
    clientX,
    clientY,
  }: {
    id: string;
    clientX: number;
    clientY: number;
  }) => {
    const targetItem = draftItems.find(item => item.id === id);
    if (!targetItem) {
      return;
    }

    onPlacedItemClick?.({ item: targetItem, clientX, clientY });

    if (!allowItemPickup) {
      return;
    }
    if (pickupOnlyPreview && !targetItem.isPreview) {
      return;
    }
    if (allowItemPickupPredicate && !allowItemPickupPredicate(targetItem)) {
      return;
    }
    if (!currentPlacementArea) {
      return;
    }
    const itemPlacementArea = normalizePlacementArea(targetItem.layer);
    if (!itemPlacementArea || currentPlacementArea !== itemPlacementArea) {
      return;
    }
    startDragFromPlaced(id);
    isPointerActiveRef.current = true;
    setIsPointerActive(true);
    projectToOverlay(clientX, clientY);
  };

  const overlayActive = Boolean(
    dragSession ||
      pendingPlacement ||
      showActionButtons ||
      isPointerActive ||
      allowItemPickup,
  );

  return (
    <div
      ref={canvasRef}
      className="absolute inset-0"
      style={{ pointerEvents: overlayActive ? 'auto' : 'none' }}
      onMouseDownCapture={handleMouseDownCapture}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseLeave}
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
            onMouseDown={event => event.stopPropagation()}
            onTouchStart={event => event.stopPropagation()}
          >
            <button
              type="button"
              onClick={event => {
                event.stopPropagation();
                handleCancel();
              }}
              onTouchEnd={event => {
                try {
                  event.preventDefault();
                } catch {
                  // passive event listener에서는 preventDefault 호출 불가
                }
                event.stopPropagation();
                handleCancel();
              }}
              className="rounded-md bg-gray-200 px-2.5 py-1 whitespace-nowrap text-gray-700 transition hover:bg-gray-300 disabled:cursor-not-allowed disabled:opacity-50"
            >
              취소
            </button>
            <button
              type="button"
              onClick={event => {
                event.stopPropagation();
                handleConfirm();
              }}
              onTouchEnd={event => {
                try {
                  event.preventDefault();
                } catch {
                  // passive event listener에서는 preventDefault 호출 불가
                }
                event.stopPropagation();
                if (pendingPlacement) {
                  handleConfirm();
                }
              }}
              disabled={!pendingPlacement}
              className="rounded-md bg-primary px-2.5 py-1 whitespace-nowrap text-white transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              배치
            </button>
            {showDeleteButton ? (
              <button
                type="button"
                onClick={handleDelete}
                className="rounded-md bg-red-500 px-2.5 py-1 whitespace-nowrap text-white transition hover:bg-red-500/90 disabled:cursor-not-allowed disabled:opacity-50"
              >
                삭제
              </button>
            ) : null}
          </div>
        </div>
      ) : null}
      {/* 메인 SVG 캔버스 (3배 확장으로 스크롤/팬 지원) */}
      <svg
        className="absolute inset-0"
        style={{
          width: '300%',
          height: '300%',
          left: '-100%',
          top: '-100%',
        }}
      >
        {/* 방의 유효한 표면 영역 표시 (반투명 회색) */}
        {surfacePolygon ? (
          <polygon
            points={surfacePolygon.map(({ x, y }) => `${x},${y}`).join(' ')}
            fill="rgba(200, 200, 200, 0.12)"
            stroke="none"
            pointerEvents="none"
          />
        ) : null}
        {/* 확정된 아이템 스프라이트 */}
        {spriteData.map(sprite => {
          if (sprite.itemType === 'PET') {
            const catAnimation = catAnimationState ?? 'idle';
            const box = getCatRenderBox(
              sprite.imageUrl,
              sprite.width,
              sprite.height,
              catAnimation,
            );
            const offsetX = sprite.centerX - box.width / 2;
            const offsetY = sprite.y + sprite.height - box.height;
            return (
              <foreignObject
                key={`${sprite.id}-cat`}
                x={offsetX}
                y={offsetY}
                width={box.width}
                height={box.height}
                style={{ pointerEvents: 'none', overflow: 'visible' }}
              >
                <div
                  xmlns="http://www.w3.org/1999/xhtml"
                  className="flex h-full w-full items-end justify-center"
                  style={{ pointerEvents: 'none' }}
                >
                  <CatSprite
                    petId={sprite.itemId}
                    currentAnimation={catAnimation}
                    className="pointer-events-none"
                    targetWidth={box.width}
                    onAnimationComplete={onCatAnimationComplete}
                  />
                </div>
              </foreignObject>
            );
          }
          return (
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
          );
        })}
        {displayPendingSprite ? (
          ((pendingPlacement && pendingPlacement.itemType === 'PET') ||
            (!pendingPlacement && dragSession?.itemType === 'PET')) ? (
            (() => {
              const catAnimation = catAnimationState ?? 'idle';
              const box = getCatRenderBox(
                displayPendingSprite.imageUrl,
                displayPendingSprite.width,
                displayPendingSprite.height,
                catAnimation,
              );
              const offsetX = displayPendingSprite.centerX - box.width / 2;
              const offsetY =
                displayPendingSprite.y + displayPendingSprite.height - box.height;
              const pendingItemId =
                pendingPlacement?.itemId ?? Number(dragSession?.itemId ?? 0);
              return (
                <foreignObject
                  key={`${displayPendingSprite.id}-pending-cat`}
                  x={offsetX}
                  y={offsetY}
                  width={box.width}
                  height={box.height}
                  style={{ pointerEvents: 'none', overflow: 'visible', opacity: 0.85 }}
                >
                  <div
                    xmlns="http://www.w3.org/1999/xhtml"
                    className="flex h-full w-full items-end justify-center"
                    style={{ pointerEvents: 'none' }}
                  >
                    <CatSprite
                      petId={pendingItemId}
                      currentAnimation={catAnimation}
                      className="pointer-events-none"
                      targetWidth={box.width}
                      onAnimationComplete={onCatAnimationComplete}
                    />
                  </div>
                </foreignObject>
              );
            })()
          ) : (
            <image
              key={`${displayPendingSprite.id}-pending-image`}
              href={displayPendingSprite.imageUrl}
              xlinkHref={displayPendingSprite.imageUrl}
              x={displayPendingSprite.x}
              y={displayPendingSprite.y}
              width={displayPendingSprite.width}
              height={displayPendingSprite.height}
              preserveAspectRatio="xMidYMax meet"
              transform={
                displayPendingSprite.rotation
                  ? `rotate(${displayPendingSprite.rotation}, ${displayPendingSprite.centerX}, ${displayPendingSprite.centerY})`
                  : undefined
              }
              opacity={0.85}
              style={{ pointerEvents: 'none' }}
            />
          )
        ) : null}
        {/* 드래그 중 고스트 스프라이트 (유효할 때만 표시) */}
        {isPointerActive && ghostSprite && ghost.isValid ? (
          dragSession?.itemType === 'PET' ? (
            (() => {
              const catAnimation = catAnimationState ?? 'idle';
              const box = getCatRenderBox(
                ghostSprite.imageUrl,
                ghostSprite.width,
                ghostSprite.height,
                catAnimation,
              );
              const offsetX = ghostSprite.centerX - box.width / 2;
              const offsetY = ghostSprite.y + ghostSprite.height - box.height;
              const ghostItemId = Number(dragSession?.itemId ?? 0);
              return (
                <foreignObject
                  key={`${ghostSprite.id}-ghost-cat`}
                  x={offsetX}
                  y={offsetY}
                  width={box.width}
                  height={box.height}
                  style={{ pointerEvents: 'none', overflow: 'visible', opacity: 0.6 }}
                >
                  <div
                    xmlns="http://www.w3.org/1999/xhtml"
                    className="flex h-full w-full items-end justify-center"
                    style={{ pointerEvents: 'none' }}
                  >
                    <CatSprite
                      petId={ghostItemId}
                      currentAnimation={catAnimation}
                      className="pointer-events-none"
                      targetWidth={box.width}
                      onAnimationComplete={onCatAnimationComplete}
                    />
                  </div>
                </foreignObject>
              );
            })()
          ) : (
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
          )
        ) : null}
        <GhostItem polygons={ghostPolygons} isValid={ghost.isValid} />
        {(allowItemPickup || onPlacedItemClick)
          ? draftPolygons.map(item => (
              <PlacedItem
                key={`${item.id}-hit`}
                id={item.id}
                polygons={item.polygons}
                onPick={handlePickPlaced}
                variant="hit"
              />
            ))
          : null}
        {/* 드래그 중인 아이템만 하이라이트를 유지한다 */}
        {draftPolygons.map(item => (
          <PlacedItem
            key={`${item.id}-highlight`}
            id={item.id}
            polygons={item.polygons}
            onPick={handlePickPlaced}
            visible={Boolean(
              dragSession && dragSession.originPlacedId === item.id,
            )}
          />
        ))}
      </svg>
    </div>
  );
};

export default RoomCanvas;
