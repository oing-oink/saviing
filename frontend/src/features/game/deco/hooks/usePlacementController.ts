import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import type { GridCell } from '@/features/game/room/hooks/useGrid';
import {
  buildFootprint,
  findNearestCell,
  getCellBounds,
} from '@/features/game/deco/utils/grid';
import { useDecoStore } from '@/features/game/deco/store/useDecoStore';
import { useOccupancyMap } from '@/features/game/deco/hooks/useOccupancyMap';

/**
 * 배치 컨트롤러 훅의 초기화에 필요한 설정 옵션.
 *
 * 이 인터페이스는 usePlacementController 훅이 동작하기 위해
 * 필요한 그리드 정보와 콜백 함수를 정의합니다.
 */
interface UsePlacementControllerOptions {
  /**
   * 배치 가능한 그리드 셀들의 배열.
   * 각 셀은 좌표, 크기, 레이어 정보를 포함하며, 배치 검증과
   * 좌표 변환에 사용됩니다.
   */
  gridCells: GridCell[];

  /**
   * 자동 배치가 실패했을 때 호출되는 콜백 함수.
   * 인벤토리에서 드래그를 시작했지만 배치 가능한 위치가
   * 없을 때 사용자에게 알림을 제공하는 데 사용됩니다.
   */
  onAutoPlacementFail?: () => void;
}

/**
 * 고스트 표시와 배치 검증을 위한 내부 상태 구조.
 *
 * 이 인터페이스는 드래그 중인 아이템의 임시 배치 상태를
 * 추적하고 시각적 피드백을 제공하는 데 사용됩니다.
 */
interface PlacementState {
  /**
   * 현재 고스트가 표시되는 기준 셀의 ID.
   * 아이템의 좌상단 기준점이 위치한 셀을 나타냅니다.
   */
  ghostCellId: string | null;

  /**
   * 고스트가 실제로 점유하는 모든 셀들의 ID 배열.
   * 다중 셀 아이템이나 회전된 아이템의 정확한 영역을 나타냅니다.
   */
  footprintCellIds: string[];

  /**
   * 현재 고스트 위치에서 배치가 유효한지 여부.
   * 충돌 감지, 레이어 제한, 그리드 경계 등을 종합한 결과입니다.
   */
  isValid: boolean;
}

/**
 * 드래그 중인 아이템의 배치 위치 계산과 검증을 담당하는 핵심 컨트롤러 훅.
 *
 * 이 훅은 사용자가 아이템을 드래그할 때 실시간으로 배치 가능한 위치를 계산하고,
 * 고스트 표시를 위한 상태를 관리하며, 최종 배치 확정을 처리합니다.
 * 자동 배치와 수동 배치 모두를 지원하며, 복잡한 충돌 감지와 레이어 제한도 처리합니다.
 *
 * @param options - 훅 초기화에 필요한 설정 옵션
 * @param options.gridCells - 배치 가능한 그리드 셀 배열
 * @param options.onAutoPlacementFail - 자동 배치 실패 시 콜백
 * @returns 고스트 상태, 포인터 이동 핸들러, 배치 확정 함수를 포함한 객체
 */
export const usePlacementController = ({
  gridCells,
  onAutoPlacementFail,
}: UsePlacementControllerOptions) => {
  const dragSession = useDecoStore(state => state.dragSession);
  const draftItems = useDecoStore(state => state.draftItems);
  const stagePlacementToStore = useDecoStore(state => state.stagePlacement);
  const updateHoverCell = useDecoStore(state => state.updateHoverCell);

  const [ghost, setGhost] = useState<PlacementState>({
    ghostCellId: null,
    footprintCellIds: [],
    isValid: false,
  });

  // 자동 배치 실패 알림의 중복 호출을 방지하기 위한 플래그
  const autoPlacementNotifiedRef = useRef(false);

  // 현재 배치된 아이템들이 점유하고 있는 셀 ID들의 Set
  const occupiedCellIds = useOccupancyMap(draftItems);

  // 드래그 세션이 종료되면 고스트 상태를 초기화
  useEffect(() => {
    if (!dragSession) {
      setGhost({ ghostCellId: null, footprintCellIds: [], isValid: false });
      autoPlacementNotifiedRef.current = false;
    }
  }, [dragSession]);

  // 드래그하는 아이템이 바뀌면 자동 배치 알림 플래그를 리셋
  useEffect(() => {
    autoPlacementNotifiedRef.current = false;
  }, [dragSession?.itemId, dragSession?.originPlacedId]);

  // 빠른 셀 ID 존재 확인을 위한 Set (O(1) 조회)
  const gridCellIdSet = useMemo(
    () => new Set(gridCells.map(cell => cell.id)),
    [gridCells],
  );

  // 그리드의 전체 경계 좌표 (포인터 클램핑에 사용)
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

    const candidate = gridCells.find(cell => {
      if (
        dragSession.allowedGridType &&
        cell.gridType !== dragSession.allowedGridType
      ) {
        return false;
      }
      const footprint = buildFootprint(
        cell.id,
        dragSession.xLength ?? 1,
        dragSession.yLength ?? 1,
      );
      const footprintWithinGrid =
        footprint.length === dragSession.xLength * dragSession.yLength &&
        footprint.every(id => gridCellIdSet.has(id));
      if (!footprintWithinGrid) {
        return false;
      }

      return footprint.every(id => !occupiedCellIds.has(id));
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
        footprint.every(id => gridCellIdSet.has(id));
      const matchesGridRule =
        !dragSession.allowedGridType ||
        dragSession.allowedGridType === nearest.gridType;
      const cellsAreFree = footprint.every(id => !occupiedCellIds.has(id));
      const isValidPlacement = Boolean(
        footprintWithinGrid && matchesGridRule && cellsAreFree,
      );

      setGhost({
        ghostCellId: nextGhostId,
        footprintCellIds: footprint,
        isValid: isValidPlacement,
      });
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

    const staged = stagePlacementToStore(
      ghost.ghostCellId,
      ghost.footprintCellIds,
    );
    if (!staged) {
      setGhost({ ghostCellId: null, footprintCellIds: [], isValid: false });
    }
    return staged;
  }, [stagePlacementToStore, dragSession, ghost]);

  return { ghost, handlePointerMove, stagePlacement };
};

/**
 * usePlacementController 훅의 반환 타입.
 * 고스트 상태와 상호작용 함수들을 포함합니다.
 */
export interface PlacementControllerResult {
  /**
   * 현재 고스트의 상태 정보.
   * 위치, footprint, 유효성을 포함합니다.
   */
  ghost: PlacementState;

  /**
   * 포인터 이동 시 고스트 위치를 업데이트하는 함수.
   */
  handlePointerMove: (px: number, py: number) => void;

  /**
   * 현재 고스트 위치에서 배치를 스테이징하는 함수.
   */
  stagePlacement: () => boolean;
}
