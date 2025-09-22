package saviing.game.room.domain.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;

import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.ItemSize;
import saviing.game.room.domain.model.vo.Position;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.model.dto.PlacementDraft;

/**
 * 방 내 배치 컬렉션을 관리하고 검증하는 도메인 서비스
 * 배치 규칙 검증 및 충돌 검사를 담당
 */
public class PlacementCollection {

    private static final int MAX_PET_COUNT = 2;

    /**
     * 배치 요청 목록을 검증하고 유효한 Placement 목록을 생성
     *
     * @param roomId 방 식별자
     * @param placementDrafts 배치 요청 목록
     * @return 검증된 Placement 목록
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static List<Placement> validateAndCreate(
        @NonNull RoomId roomId,
        @NonNull List<PlacementDraft> placementDrafts
    ) {

        if (placementDrafts.isEmpty()) {
            return Collections.emptyList();
        }

        validatePlacementConstraints(placementDrafts);

        final List<Placement> placements = createPlacements(roomId, placementDrafts);
        validateNoOverlaps(placements);

        return placements;
    }

    /**
     * 배치 제약사항을 검증 (중복 아이템, 펫 개수 제한)
     * 성능 최적화를 위해 한 번의 순회로 모든 검증을 수행
     *
     * @param drafts 배치 요청 목록
     * @throws IllegalArgumentException 제약사항 위반 시
     */
    private static void validatePlacementConstraints(List<PlacementDraft> drafts) {
        final Set<Long> inventoryItemIds = new HashSet<>();
        int petCount = 0;

        for (final PlacementDraft draft : drafts) {
            // 중복 아이템 검증
            if (!inventoryItemIds.add(draft.inventoryItemId())) {
                throw new IllegalArgumentException("Duplicate inventory item placement: " + draft.inventoryItemId());
            }

            // 펫 개수 계산
            if (draft.isPet()) {
                petCount++;
            }
        }

        // 펫 개수 제한 검증
        if (petCount > MAX_PET_COUNT) {
            throw new IllegalArgumentException("Cannot place more than " + MAX_PET_COUNT + " pets. Found: " + petCount);
        }
    }

    /**
     * PlacementDraft 목록으로부터 Placement 객체들을 생성
     *
     * @param roomId 방 식별자
     * @param drafts 배치 요청 목록
     * @return 생성된 Placement 목록
     */
    private static List<Placement> createPlacements(RoomId roomId, List<PlacementDraft> drafts) {
        return drafts.stream()
            .map(draft -> Placement.create(
                roomId,
                draft.inventoryItemId(),
                new Position(draft.positionX(), draft.positionY()),
                new ItemSize(draft.xLength(), draft.yLength()),
                draft.category()
            ))
            .collect(Collectors.toList());
    }

    /**
     * 배치된 아이템들 간의 겹침이 없는지 검증
     *
     * @param placements 검증할 배치 목록
     * @throws IllegalArgumentException 겹치는 배치가 있는 경우
     */
    private static void validateNoOverlaps(List<Placement> placements) {
        for (int i = 0; i < placements.size(); i++) {
            for (int j = i + 1; j < placements.size(); j++) {
                final Placement placement1 = placements.get(i);
                final Placement placement2 = placements.get(j);

                if (placement1.overlaps(placement2)) {
                    throw new IllegalArgumentException(
                            String.format("Placements overlap: item %d and item %d",
                                    placement1.getInventoryItemId(),
                                    placement2.getInventoryItemId())
                    );
                }
            }
        }
    }

}
