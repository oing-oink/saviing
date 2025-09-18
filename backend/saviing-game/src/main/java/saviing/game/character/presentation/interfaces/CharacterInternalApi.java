package saviing.game.character.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import saviing.common.response.ApiResult;
import saviing.game.character.presentation.dto.request.AddCoinsRequest;
import saviing.game.character.presentation.dto.request.CompleteAccountConnectionRequest;
import saviing.game.character.presentation.dto.request.HandleAccountTerminatedRequest;

@Tag(name = "Character Internal", description = "캐릭터 시스템 간 통신 API (Bank ↔ Game)")
public interface CharacterInternalApi {

    @Operation(
        summary = "계좌 연결 완료",
        description = "Bank 서버에서 계좌 연결이 완료되었을 때 Game 서버에 알립니다."
    )
    @ApiResponse(responseCode = "400", description = "잘못된 계좌 연결 상태")
    @ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    ApiResult<Void> completeAccountConnection(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId,
        @Parameter(description = "계좌 연결 완료 요청", required = true)
        @Valid @RequestBody CompleteAccountConnectionRequest request
    );

    @Operation(
        summary = "코인 거래 생성",
        description = "Bank 서버에서 캐릭터에게 코인을 지급합니다. (적금 이자, 이벤트 등)"
    )
    @ApiResponse(responseCode = "400", description = "잘못된 코인 수량")
    @ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    ApiResult<Void> addCoins(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId,
        @Parameter(description = "코인 거래 요청", required = true)
        @Valid @RequestBody AddCoinsRequest request
    );

    @Operation(
        summary = "계좌 해지 이벤트",
        description = "Bank 서버에서 계좌가 해지되었을 때 Game 서버에 알립니다."
    )
    @ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    ApiResult<Void> handleAccountTerminated(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId,
        @Parameter(description = "계좌 해지 이벤트", required = true)
        @Valid @RequestBody HandleAccountTerminatedRequest request
    );

    @Operation(
        summary = "캐릭터 상태 변경",
        description = "유예 기간 후 시스템에서 캐릭터 상태를 변경합니다."
    )
    @ApiResponse(responseCode = "400", description = "잘못된 캐릭터 상태")
    @ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    ApiResult<Void> deactivateCharacter(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );
}