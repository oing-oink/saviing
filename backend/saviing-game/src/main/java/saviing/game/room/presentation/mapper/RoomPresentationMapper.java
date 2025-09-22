package saviing.game.room.presentation.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import saviing.game.room.application.dto.command.PlaceItemCommand;
import saviing.game.room.domain.model.aggregate.Category;
import saviing.game.room.application.dto.result.RoomPlacementListResult;
import saviing.game.room.application.dto.command.SaveRoomPlacementsCommand;
import saviing.game.room.presentation.dto.request.PlacementItemRequest;
import saviing.game.room.presentation.dto.request.SaveRoomPlacementsRequest;
import saviing.game.room.presentation.dto.response.PlacementResponse;
import saviing.game.room.presentation.dto.response.RoomPlacementsResponse;

/**
 * 프레젠테이션 레이어와 애플리케이션 레이어 간의 DTO 변환을 담당하는 매퍼입니다.
 */
@Component
public class RoomPresentationMapper {

    /**
     * SaveRoomPlacementsRequest를 SaveRoomPlacementsCommand로 변환합니다.
     *
     * @param roomId 방 식별자
     * @param request 방 배치 저장 요청
     * @return 변환된 SaveRoomPlacementsCommand
     * @throws IllegalArgumentException roomId가 null이거나 request가 null인 경우
     */
    public SaveRoomPlacementsCommand toCommand(Long roomId, SaveRoomPlacementsRequest request) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다");
        }
        if (request == null) {
            throw new IllegalArgumentException("request는 필수입니다");
        }

        List<PlaceItemCommand> placeItemCommands = request.placedItems().stream()
            .map(this::toPlaceItemCommand)
            .collect(Collectors.toList());

        return SaveRoomPlacementsCommand.builder()
            .roomId(roomId)
            .characterId(request.characterId())
            .placedItems(placeItemCommands)
            .build();
    }

    /**
     * PlacementItemRequest를 PlaceItemCommand로 변환합니다.
     *
     * @param request 배치 아이템 요청
     * @return 변환된 PlaceItemCommand
     * @throws IllegalArgumentException request가 null인 경우
     */
    private PlaceItemCommand toPlaceItemCommand(PlacementItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request는 필수입니다");
        }

        Category category = parseCategory(request.category());

        return new PlaceItemCommand(
            request.inventoryItemId(),
            request.positionX(),
            request.positionY(),
            request.xLength(),
            request.yLength(),
            category
        );
    }

    /**
     * 문자열 category를 Category 열거형으로 변환합니다.
     *
     * @param category 카테고리 문자열
     * @return 변환된 Category 열거형
     * @throws IllegalArgumentException 유효하지 않은 category 값인 경우
     */
    private Category parseCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("category는 필수입니다");
        }

        try {
            return Category.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 category 값입니다: " + category);
        }
    }

    /**
     * RoomPlacementListResult를 RoomPlacementsResponse로 변환합니다.
     *
     * @param result 방 배치 목록 조회 결과
     * @return 변환된 RoomPlacementsResponse
     * @throws IllegalArgumentException result가 null인 경우
     */
    public RoomPlacementsResponse toResponse(RoomPlacementListResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result는 필수입니다");
        }

        List<PlacementResponse> placementResponses = result.placements().stream()
            .map(this::toPlacementResponse)
            .collect(Collectors.toList());

        return RoomPlacementsResponse.builder()
            .roomId(result.roomId())
            .placements(placementResponses)
            .build();
    }

    /**
     * PlacementInfo를 PlacementResponse로 변환합니다.
     *
     * @param placementInfo 배치 정보
     * @return 변환된 PlacementResponse
     * @throws IllegalArgumentException placementInfo가 null인 경우
     */
    private PlacementResponse toPlacementResponse(RoomPlacementListResult.PlacementInfo placementInfo) {
        if (placementInfo == null) {
            throw new IllegalArgumentException("placementInfo는 필수입니다");
        }

        return PlacementResponse.builder()
            .placementId(placementInfo.placementId())
            .inventoryItemId(placementInfo.inventoryItemId())
            .positionX(placementInfo.positionX())
            .positionY(placementInfo.positionY())
            .xLength(placementInfo.xLength())
            .yLength(placementInfo.yLength())
            .category(placementInfo.category())
            .build();
    }
}