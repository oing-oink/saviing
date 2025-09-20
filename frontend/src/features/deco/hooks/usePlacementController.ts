import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import type { GridCell } from '@/features/game/room/hooks/useGrid';
import { buildFootprint, findNearestCell, getCellBounds } from '@/features/deco/utils/grid';
import { useDecoStore } from '@/features/deco/state/deco.store';
import { useOccupancyMap } from '@/features/deco/hooks/useOccupancyMap';

interface UsePlacementControllerOptions {
  gridCells: GridCell[];
  onAutoPlacementFail?: () => void;
}

interface PlacementState {
  ghostCellId: string | null;
  footprintCellIds: string[];
  isValid: boolean;
}

export const usePlacementController = ({
  gridCells,
  onAutoPlacementFail,
}: UsePlacementControllerOptions) => {
  const dragSession = useDecoStore((state) => state.dragSession);
  const draftItems = useDecoStore((state) => state.draftItems);
  const stagePlacementToStore = useDecoStore((state) => state.stagePlacement);
  const updateHoverCell = useDecoStore((state) => state.updateHoverCell);

  const [ghost, setGhost] = useState<PlacementState>({
    ghostCellId: null,
    footprintCellIds: [],
    isValid: false,
  });

  const autoPlacementNotifiedRef = useRef(false);

  const occupiedCellIds = useOccupancyMap(draftItems);

  useEffect(() => {
    if (!dragSession) {
      setGhost({ ghostCellId: null, footprintCellIds: [], isValid: false });
      autoPlacementNotifiedRef.current = false;
    }
  }, [dragSession]);

  useEffect(() => {
    autoPlacementNotifiedRef.current = false;
  }, [dragSession?.itemId, dragSession?.originPlacedId]);

  const gridCellIdSet = useMemo(
    () => new Set(gridCells.map((cell) => cell.id)),
    [gridCells],
  );

  const gridBounds = useMemo(() => getCellBounds(gridCells), [gridCells]);

  useEffect(() => {
    if (!dragSession) {
      return;
    }
    if (dragSession.originPlacedId) {
      return;
    }
    if (dragSession.hoverCellId) {
      return;
    }

    const candidate = gridCells.find((cell) => {
      if (dragSession.allowedGridType && cell.gridType !== dragSession.allowedGridType) {
        return false;
      }
      const footprint = buildFootprint(
        cell.id,
        dragSession.xLength ?? 1,
        dragSession.yLength ?? 1,
      );
      const footprintWithinGrid =
        footprint.length === dragSession.xLength * dragSession.yLength &&
        footprint.every((id) => gridCellIdSet.has(id));
      if (!footprintWithinGrid) {
        return false;
      }

      return footprint.every((id) => !occupiedCellIds.has(id));
    });

    if (!candidate) {
      setGhost({ ghostCellId: null, footprintCellIds: [], isValid: false });
      updateHoverCell(null);
      if (!autoPlacementNotifiedRef.current) {
        onAutoPlacementFail?.();
        autoPlacementNotifiedRef.current = true;
      }
      return;
    }

    const nextFootprint = buildFootprint(
      candidate.id,
      dragSession.xLength ?? 1,
      dragSession.yLength ?? 1,
    );
    setGhost({
      ghostCellId: candidate.id,
      footprintCellIds: nextFootprint,
      isValid: true,
    });
    updateHoverCell(candidate.id);
    autoPlacementNotifiedRef.current = false;
  }, [
    dragSession,
    gridCells,
    gridCellIdSet,
    occupiedCellIds,
    updateHoverCell,
    onAutoPlacementFail,
  ]);

  const handlePointerMove = useCallback(
    (px: number, py: number) => {
      if (!dragSession) {
        setGhost({ ghostCellId: null, footprintCellIds: [], isValid: false });
        return;
      }

      const clampedX = Math.max(gridBounds.minX, Math.min(gridBounds.maxX, px));
      const clampedY = Math.max(gridBounds.minY, Math.min(gridBounds.maxY, py));

      const nearest = findNearestCell(gridCells, clampedX, clampedY);
      const nextGhostId = nearest?.id ?? null;
      if (!nextGhostId || !nearest) {
        setGhost({ ghostCellId: null, footprintCellIds: [], isValid: false });
        updateHoverCell(null);
        return;
      }

      const footprint = buildFootprint(
        nextGhostId,
        dragSession.xLength ?? 1,
        dragSession.yLength ?? 1,
      );
      const footprintWithinGrid =
        footprint.length === dragSession.xLength * dragSession.yLength &&
        footprint.every((id) => gridCellIdSet.has(id));
      const matchesGridRule =
        !dragSession.allowedGridType || dragSession.allowedGridType === nearest.gridType;
      const cellsAreFree = footprint.every((id) => !occupiedCellIds.has(id));
      const isValidPlacement = Boolean(footprintWithinGrid && matchesGridRule && cellsAreFree);

      setGhost({ ghostCellId: nextGhostId, footprintCellIds: footprint, isValid: isValidPlacement });
      updateHoverCell(nextGhostId);
    },
    [
      gridCells,
      dragSession,
      updateHoverCell,
      gridCellIdSet,
      occupiedCellIds,
      gridBounds,
    ],
  );

  const stagePlacement = useCallback(() => {
    if (!dragSession || !ghost.ghostCellId || !ghost.isValid) {
      return false;
    }

    const staged = stagePlacementToStore(ghost.ghostCellId, ghost.footprintCellIds);
    if (!staged) {
      setGhost({ ghostCellId: null, footprintCellIds: [], isValid: false });
    }
    return staged;
  }, [stagePlacementToStore, dragSession, ghost]);

  return { ghost, handlePointerMove, stagePlacement };
};
