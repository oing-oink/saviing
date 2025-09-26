package saviing.game.pet.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import saviing.common.response.ApiResult;
import saviing.common.response.ErrorResult;
import saviing.game.pet.presentation.dto.request.ChangePetNameRequest;
import saviing.game.pet.presentation.dto.request.PetInteractionRequest;
import saviing.game.pet.presentation.dto.response.PetInfoResponse;
import saviing.game.pet.presentation.dto.response.PetInteractionResponse;

/**
 * 펫 API 컨트롤러 인터페이스
 * OpenAPI 문서화를 위한 인터페이스입니다.
 */
@Tag(name = "Pet", description = "펫 관리 API")
public interface PetApi {

    @Operation(
        summary = "특정 펫 조회",
        description = "펫 ID로 특정 펫의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 펫 ID 형식 - 양수가 아닌 값 입력",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "펫을 찾을 수 없음 (PET_NOT_FOUND)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "PET_NOT_FOUND",
                    summary = "펫을 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "PET_NOT_FOUND",
                          "message": "펫을 찾을 수 없습니다",
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
    ApiResult<PetInfoResponse> getPetInfo(
        @Parameter(description = "펫 ID", example = "1")
        Long petId
    );

    @Operation(
        summary = "펫과 상호작용",
        description = "펫과 상호작용(먹이주기/놀아주기)을 수행합니다. 소모품을 사용하고 펫의 상태를 변경합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상호작용 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "펫을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "상호작용 불가능 (에너지 부족, 소모품 부족 등)",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResult.class))
        )
    })
    ApiResult<PetInteractionResponse> interactWithPet(
        @Parameter(description = "펫 ID", example = "1")
        Long petId,

        @Parameter(description = "상호작용 요청 정보")
        PetInteractionRequest request
    );

    @Operation(
        summary = "펫 이름 변경",
        description = "펫의 이름을 변경합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이름 변경 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (잘못된 펫 ID, 유효하지 않은 이름)",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "INVALID_NAME",
                    summary = "유효하지 않은 펫 이름",
                    value = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "PET_INVALID_NAME",
                          "message": "펫 이름은 1자 이상 20자 이하여야 합니다",
                          "timestamp": "2025-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "펫을 찾을 수 없음",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                examples = @ExampleObject(
                    name = "PET_NOT_FOUND",
                    summary = "펫을 찾을 수 없는 경우",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "PET_NOT_FOUND",
                          "message": "펫을 찾을 수 없습니다",
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
    ApiResult<PetInfoResponse> changePetName(
        @Parameter(description = "펫 ID", example = "1")
        Long petId,

        @Parameter(description = "펫 이름 변경 요청 정보")
        ChangePetNameRequest request
    );
}