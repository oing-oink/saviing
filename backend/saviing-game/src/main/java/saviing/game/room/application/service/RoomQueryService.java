package saviing.game.room.application.service;

import java.util.Collections;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import saviing.game.room.application.dto.query.GetRoomPlacementsQuery;
import saviing.game.room.application.dto.result.RoomPlacementListResult;
import saviing.game.room.application.mapper.RoomResponseMapper;
import saviing.game.room.domain.model.aggregate.PlacedItem;
import saviing.game.room.domain.model.aggregate.Placement;
import saviing.game.room.domain.model.vo.RoomId;
import saviing.game.room.domain.repository.PlacementRepository;

/**
 * Room 도메인의 조회 처리를 담당하는 애플리케이션 서비스
 * 배치 조회 등의 쿼리 유스케이스를 구현하며,
 * 도메인 객체를 응답 DTO로 변환하여 반환
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService {

    private final PlacementRepository placementRepository;
    private final RoomResponseMapper roomResponseMapper;

    /**
     * 방의 배치 목록을 조회
     * 룸별로 하나의 Placement 애그리거트를 조회하여 배치된 아이템 목록을 반환
     *
     * @param query 방 배치 조회 쿼리 (roomId 포함)
     * @return 방 배치 목록 조회 결과 (PlacedItem 목록 포함)
     * @throws IllegalArgumentException query가 null이거나 유효하지 않은 경우
     */
    public RoomPlacementListResult getRoomPlacements(@NonNull GetRoomPlacementsQuery query) {
        // 1. 쿼리 검증
        query.validate();

        RoomId roomId = new RoomId(query.roomId());

        // 2. 도메인 조회
        List<PlacedItem> placedItems = placementRepository.findByRoomId(roomId)
            .map(Placement::getPlacedItems)
            .orElse(Collections.emptyList());

        // 3. 응답 DTO 변환
        return roomResponseMapper.toRoomPlacementListResult(query.roomId(), placedItems);
    }
}