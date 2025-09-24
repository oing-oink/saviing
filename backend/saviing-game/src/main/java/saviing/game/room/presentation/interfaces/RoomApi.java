package saviing.game.room.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;
import saviing.game.room.presentation.dto.request.SaveRoomPlacementsRequest;
import saviing.game.room.presentation.dto.response.RoomPlacementsResponse;
import saviing.game.room.presentation.dto.response.RoomResponse;

@Tag(name = "Room", description = "게임 방 관리 API")
public interface RoomApi {

    @Operation(
        summary = "방 배치 조회",
        description = "특정 방의 아이템 배치 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "방 배치 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoomPlacementsResponse.class),
                examples = @ExampleObject(
                    name = "방 배치 조회 결과 예시",
                    summary = "배치된 아이템들이 포함된 응답",
                    value = """
                        {
                          "success": true,
                          "status": 200,
                          "data": {
                            "roomId": 1,
                            "placements": [
                              {
                                "placementId": 1,
                                "inventoryItemId": 101,
                                "positionX": 2,
                                "positionY": 3,
                                "xLength": 2,
                                "yLength": 1,
                                "category": "LEFT"
                              },
                              {
                                "placementId": 2,
                                "inventoryItemId": 102,
                                "positionX": 5,
                                "positionY": 6,
                                "xLength": 1,
                                "yLength": 1,
                                "category": "RIGHT"
                              }
                            ]
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 방 ID 형식",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "방을 찾을 수 없음 (ROOM_NOT_FOUND)",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    @GetMapping("/{roomId}/placements")
    ApiResult<RoomPlacementsResponse> getRoomPlacements(
        @Parameter(description = "방 ID", required = true, example = "1")
        @PathVariable Long roomId
    );

    @Operation(
        summary = "방 배치 저장",
        description = "방의 아이템 배치를 저장합니다. 기존 배치는 완전히 교체됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "방 배치 저장 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "방 배치 저장 성공 응답",
                    summary = "배치 저장이 성공적으로 완료된 경우",
                    value = """
                        {
                          "success": true,
                          "status": 200,
                          "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패 - 배치 규칙 위반 (겹침, 펫 개수 초과 등)",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "방을 찾을 수 없음 (ROOM_NOT_FOUND)",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "인벤토리 아이템 사용 충돌 (INVENTORY_ITEM_ALREADY_IN_USE)",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    ApiResult<Void> saveRoomPlacements(
        @Parameter(description = "방 ID", required = true, example = "1")
        @PathVariable Long roomId,
        @Parameter(
            description = "방 배치 저장 요청",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SaveRoomPlacementsRequest.class),
                examples = @ExampleObject(
                    name = "방 배치 저장 예시",
                    summary = "가구와 펫을 배치하는 예시",
                    value = """
                        {
                          "characterId": 1,
                          "placedItems": [
                            {
                              "inventoryItemId": 101,
                              "itemId": 201,
                              "positionX": 2,
                              "positionY": 3,
                              "xLength": 2,
                              "yLength": 1,
                              "category": "LEFT"
                            },
                            {
                              "inventoryItemId": 102,
                              "itemId": 301,
                              "positionX": 5,
                              "positionY": 6,
                              "xLength": 1,
                              "yLength": 1,
                              "category": "RIGHT"
                            },
                            {
                              "inventoryItemId": 103,
                              "itemId": 401,
                              "positionX": 8,
                              "positionY": 2,
                              "xLength": 3,
                              "yLength": 2,
                              "category": "BOTTOM"
                            }
                          ]
                        }
                        """
                )
            )
        )
        @RequestBody SaveRoomPlacementsRequest request
    );

    /**
     * 캐릭터 ID와 방 번호로 방을 조회합니다.
     *
     * @param characterId 조회할 캐릭터의 식별자
     * @param roomNumber 조회할 방 번호
     * @return 방 정보 조회 결과
     */
    @Operation(
        summary = "캐릭터별 방 조회",
        description = "특정 캐릭터의 특정 번호 방 정보를 조회합니다. roomNumber가 1인 경우 해당 캐릭터의 첫 번째 방을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoomResponse.class),
                examples = @ExampleObject(
                    name = "방 조회 결과 예시",
                    summary = "캐릭터의 방 정보 조회 성공",
                    value = """
                        {
                          "success": true,
                          "status": 200,
                          "data": {
                            "roomId": 1,
                            "characterId": 1,
                            "roomNumber": 1,
                            "createdAt": "2023-12-01T10:00:00",
                            "updatedAt": "2023-12-01T10:00:00"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (characterId나 roomNumber가 유효하지 않음)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "잘못된 파라미터",
                    summary = "characterId나 roomNumber가 유효하지 않은 경우",
                    value = """
                        {
                          "success": false,
                          "status": 400,
                          "error": {
                            "code": "INVALID_PARAMETER",
                            "message": "characterId는 양수여야 합니다"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 조건의 방을 찾을 수 없음",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "방 미발견",
                    summary = "해당 캐릭터의 방 번호가 존재하지 않는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "error": {
                            "code": "ROOM_NOT_FOUND",
                            "message": "캐릭터 ID 1의 방 번호 1을 찾을 수 없습니다"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    @GetMapping
    ApiResult<RoomResponse> getRoomByCharacterIdAndRoomNumber(
        @Parameter(
            description = "조회할 캐릭터의 식별자",
            example = "1",
            required = true,
            schema = @Schema(type = "integer", format = "int64", minimum = "1")
        )
        @RequestParam Long characterId,
        @Parameter(
            description = "조회할 방 번호 (1~5 범위)",
            example = "1",
            required = true,
            schema = @Schema(type = "integer", format = "int32", minimum = "1", maximum = "5")
        )
        @RequestParam Integer roomNumber
    );
}