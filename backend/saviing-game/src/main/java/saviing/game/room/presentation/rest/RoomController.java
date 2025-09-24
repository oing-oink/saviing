package saviing.game.room.presentation.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saviing.common.response.ApiResult;
import saviing.game.room.application.dto.query.GetRoomPlacementsQuery;
import saviing.game.room.application.dto.query.GetRoomByCharacterQuery;
import saviing.game.room.application.dto.result.RoomPlacementListResult;
import saviing.game.room.application.dto.result.RoomResult;
import saviing.game.room.application.dto.command.SaveRoomPlacementsCommand;
import saviing.game.room.application.service.RoomCommandService;
import saviing.game.room.application.service.RoomQueryService;
import saviing.game.room.presentation.dto.request.SaveRoomPlacementsRequest;
import saviing.game.room.presentation.dto.response.RoomPlacementsResponse;
import saviing.game.room.presentation.dto.response.RoomResponse;
import saviing.game.room.presentation.interfaces.RoomApi;
import saviing.game.room.presentation.mapper.RoomPresentationMapper;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 방 관련 API를 제공하는 REST 컨트롤러입니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/game/rooms")
public class RoomController implements RoomApi {

    private final RoomQueryService roomQueryService;
    private final RoomCommandService roomCommandService;
    private final RoomPresentationMapper roomPresentationMapper;

    /**
     * 방의 배치 목록을 조회합니다.
     *
     * @param roomId 조회할 방의 식별자
     * @return 방 배치 목록 조회 결과
     * @throws IllegalArgumentException roomId가 유효하지 않은 경우
     */
    @GetMapping("/{roomId}/placements")
    public ApiResult<RoomPlacementsResponse> getRoomPlacements(@PathVariable Long roomId) {
        log.info("방 배치 조회 요청: roomId={}", roomId);

        GetRoomPlacementsQuery query = GetRoomPlacementsQuery.builder()
            .roomId(roomId)
            .build();

        RoomPlacementListResult result = roomQueryService.getRoomPlacements(query);
        RoomPlacementsResponse response = roomPresentationMapper.toResponse(result);

        log.info("방 배치 조회 완료: roomId={}, placementCount={}", roomId, response.placements().size());
        return ApiResult.ok(response);
    }

    /**
     * 방의 배치를 저장합니다.
     *
     * @param roomId 저장할 방의 식별자
     * @param request 방 배치 저장 요청
     * @return 성공 응답
     * @throws IllegalArgumentException 요청 데이터가 유효하지 않은 경우
     */
    @PutMapping("/{roomId}/placements")
    public ApiResult<Void> saveRoomPlacements(
        @PathVariable Long roomId,
        @RequestBody SaveRoomPlacementsRequest request
    ) {
        log.info("방 배치 저장 요청: roomId={}, characterId={}, itemCount={}",
            roomId, request.characterId(), request.placedItems().size());

        SaveRoomPlacementsCommand command = roomPresentationMapper.toCommand(roomId, request);
        roomCommandService.savePlacements(command);

        log.info("방 배치 저장 완료: roomId={}, characterId={}", roomId, request.characterId());
        return ApiResult.ok();
    }

    /**
     * 캐릭터 ID와 방 번호로 방을 조회합니다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @param roomNumber 조회할 방 번호
     * @return 방 정보 조회 결과
     * @throws IllegalArgumentException characterId나 roomNumber가 유효하지 않은 경우
     */
    @GetMapping
    public ApiResult<RoomResponse> getRoomByCharacterIdAndRoomNumber(
        @RequestParam Long characterId,
        @RequestParam Integer roomNumber
    ) {
        log.info("캐릭터별 방 조회 요청: characterId={}, roomNumber={}", characterId, roomNumber);

        GetRoomByCharacterQuery query = GetRoomByCharacterQuery.builder()
            .characterId(characterId)
            .roomNumber(roomNumber)
            .build();

        RoomResult result = roomQueryService.findRoomByCharacterIdAndRoomNumber(query);
        RoomResponse response = roomPresentationMapper.toRoomResponse(result);

        log.info("캐릭터별 방 조회 완료: characterId={}, roomNumber={}, roomId={}",
            characterId, roomNumber, result.roomId());
        return ApiResult.ok(response);
    }
}