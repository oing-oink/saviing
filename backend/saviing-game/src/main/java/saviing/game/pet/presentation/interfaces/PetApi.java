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
import saviing.game.pet.presentation.dto.response.PetInfoResponse;

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
}