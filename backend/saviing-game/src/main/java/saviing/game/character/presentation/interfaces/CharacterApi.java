package saviing.game.character.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;
import saviing.game.character.presentation.dto.request.ConnectAccountRequest;
import saviing.game.character.presentation.dto.request.CreateCharacterRequest;
import saviing.game.character.presentation.dto.response.CharacterResponse;
import saviing.game.character.presentation.dto.response.GameEntryResponse;
import saviing.game.character.presentation.dto.response.CharacterStatisticsResponse;

@Tag(name = "Character", description = "게임 캐릭터 관리 API")
public interface CharacterApi {

    @Operation(
        summary = "캐릭터 생성",
        description = "새로운 캐릭터를 생성합니다. 고객당 하나의 활성 캐릭터만 가질 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "캐릭터 생성 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패 - 고객 ID가 비어있거나 올바르지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 활성 캐릭터가 존재함 (CHARACTER_DUPLICATE_ACTIVE)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "CHARACTER_DUPLICATE_ACTIVE",
                    summary = "이미 활성 캐릭터가 존재하는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 409,
                          "code": "CHARACTER_DUPLICATE_ACTIVE",
                          "message": "이미 활성 캐릭터가 존재합니다",
                          "timestamp": "2025-01-15T10:30:00"
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
    ApiResult<CharacterResponse> createCharacter(
        @Parameter(description = "캐릭터 생성 요청", required = true)
        @Valid @RequestBody CreateCharacterRequest request
    );

    @Operation(
        summary = "캐릭터 상세 조회",
        description = "캐릭터 ID로 캐릭터의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "캐릭터 조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 캐릭터 ID 형식 - 양수가 아닌 값 입력",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "캐릭터를 찾을 수 없음 (CHARACTER_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "CHARACTER_NOT_FOUND",
                    summary = "캐릭터를 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "CHARACTER_NOT_FOUND",
                          "message": "캐릭터를 찾을 수 없습니다",
                          "timestamp": "2025-01-15T10:30:00"
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
    ApiResult<CharacterResponse> getCharacter(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );

    @Operation(
        summary = "계좌 연결",
        description = "캐릭터와 계좌를 연결합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "계좌 연결 요청 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패 또는 잘못된 캐릭터 상태",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "캐릭터를 찾을 수 없음 (CHARACTER_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "CHARACTER_NOT_FOUND",
                    summary = "캐릭터를 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "CHARACTER_NOT_FOUND",
                          "message": "캐릭터를 찾을 수 없습니다",
                          "timestamp": "2025-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "계좌 연결 충돌 - 이미 계좌가 연결되어 있거나 연결 진행 중 (CHARACTER_ACCOUNT_ALREADY_CONNECTED, CHARACTER_ACCOUNT_CONNECTION_IN_PROGRESS)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = {
                    @ExampleObject(
                        name = "CHARACTER_ACCOUNT_ALREADY_CONNECTED",
                        summary = "계좌가 이미 연결되어 있는 경우",
                        value = """
                            {
                              "success": false,
                              "status": 409,
                              "code": "CHARACTER_ACCOUNT_ALREADY_CONNECTED",
                              "message": "계좌가 이미 연결되어 있습니다",
                              "timestamp": "2025-01-15T10:30:00"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "CHARACTER_ACCOUNT_CONNECTION_IN_PROGRESS",
                        summary = "계좌 연결이 이미 진행 중인 경우",
                        value = """
                            {
                              "success": false,
                              "status": 409,
                              "code": "CHARACTER_ACCOUNT_CONNECTION_IN_PROGRESS",
                              "message": "계좌 연결이 이미 진행 중입니다",
                              "timestamp": "2025-01-15T10:30:00"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    ApiResult<Void> connectAccount(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId,
        @Parameter(description = "계좌 연결 요청", required = true)
        @Valid @RequestBody ConnectAccountRequest request
    );

    @Operation(
        summary = "계좌 연결 해제",
        description = "캐릭터와 계좌 연결을 해제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "계좌 연결 해제 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "해제할 수 있는 계좌 연결이 없음 (CHARACTER_ACCOUNT_NOT_CONNECTED, CHARACTER_INVALID_ACCOUNT_CONNECTION_STATE)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = {
                    @ExampleObject(
                        name = "CHARACTER_ACCOUNT_NOT_CONNECTED",
                        summary = "계좌가 연결되어 있지 않은 경우",
                        value = """
                            {
                              "success": false,
                              "status": 400,
                              "code": "CHARACTER_ACCOUNT_NOT_CONNECTED",
                              "message": "계좌가 연결되어 있지 않습니다",
                              "timestamp": "2025-01-15T10:30:00"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "CHARACTER_INVALID_ACCOUNT_CONNECTION_STATE",
                        summary = "잘못된 계좌 연결 상태인 경우",
                        value = """
                            {
                              "success": false,
                              "status": 400,
                              "code": "CHARACTER_INVALID_ACCOUNT_CONNECTION_STATE",
                              "message": "잘못된 계좌 연결 상태입니다",
                              "timestamp": "2025-01-15T10:30:00"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "캐릭터를 찾을 수 없음 (CHARACTER_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "CHARACTER_NOT_FOUND",
                    summary = "캐릭터를 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "CHARACTER_NOT_FOUND",
                          "message": "캐릭터를 찾을 수 없습니다",
                          "timestamp": "2025-01-15T10:30:00"
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
    ApiResult<Void> cancelAccountConnection(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );

    // =========================
    // 1) 메인 엔트리 게임 정보 조회
    // =========================
    @Operation(
        summary = "메인 엔트리 게임 정보 조회",
        description = "메인 페이지에서 표시할 게임 정보를 조회합니다. 활성 캐릭터와 1층에 배치된 첫 번째 펫 정보를 반환합니다. JWT 토큰에서 customerId를 자동 추출합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메인 엔트리 게임 정보 조회 성공"),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - JWT 토큰이 누락되거나 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "활성 캐릭터를 찾을 수 없음 (CHARACTER_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "CHARACTER_NOT_FOUND",
                    summary = "활성 캐릭터를 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "CHARACTER_NOT_FOUND",
                          "message": "활성 캐릭터를 찾을 수 없습니다",
                          "timestamp": "2025-01-15T10:30:00"
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
    ApiResult<GameEntryResponse> getGameEntry(
        @Parameter(hidden = true) Authentication authentication
    );

    // =========================
    // 2) 캐릭터 통계 조회
    // =========================
    @Operation(
        summary = "캐릭터 통계 조회",
        description = "캐릭터의 펫 레벨 합계와 인벤토리 희귀도 통계를 조회합니다. 상위 10개 펫의 레벨 합계와 카테고리별 상위 5개 아이템의 희귀도 합계를 제공합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "캐릭터 통계 조회 성공",
            content = @Content(
                schema = @Schema(implementation = CharacterStatisticsResponse.class),
                examples = @ExampleObject(
                    name = "CHARACTER_STATISTICS_SUCCESS",
                    summary = "캐릭터 통계 조회 성공 예시",
                    value = """
                        {
                          "success": true,
                          "status": 200,
                          "message": "success",
                          "data": {
                            "characterId": 1,
                            "topPetLevelSum": 150,
                            "inventoryRarityStatistics": {
                              "pet": {
                                "CAT": 12
                              },
                              "decoration": {
                                "LEFT": 15,
                                "RIGHT": 8,
                                "BOTTOM": 6,
                                "ROOM_COLOR": 4
                              }
                            }
                          },
                          "timestamp": "2025-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 캐릭터 ID 형식 - 양수가 아닌 값 입력",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "캐릭터를 찾을 수 없음 (CHARACTER_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "CHARACTER_NOT_FOUND",
                    summary = "캐릭터를 찾을 수 없는 경우",
                    value = """
                        {
                        "success": false,
                        "status": 404,
                        "code": "CHARACTER_NOT_FOUND",
                        "message": "캐릭터를 찾을 수 없습니다",
                        "timestamp": "2025-01-15T10:30:00"
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
    @GetMapping(value = "/characters/{characterId}/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResult<CharacterStatisticsResponse> getCharacterStatistics(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );
}