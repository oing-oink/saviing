package saviing.game.character.presentation.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import saviing.common.response.ApiResult;
import saviing.game.character.presentation.dto.request.ConnectAccountRequest;
import saviing.game.character.presentation.dto.request.CreateCharacterRequest;
import saviing.game.character.presentation.dto.response.CharacterResponse;

@Tag(name = "Character", description = "게임 캐릭터 관리 API")
public interface CharacterApi {

    @Operation(
        summary = "캐릭터 생성",
        description = "새로운 캐릭터를 생성합니다. 고객당 하나의 활성 캐릭터만 가질 수 있습니다."
    )
    @ApiResponse(responseCode = "409", description = "이미 활성 캐릭터가 존재함")
    @PostMapping
    ApiResult<CharacterResponse> createCharacter(
        @Parameter(description = "캐릭터 생성 요청", required = true)
        @Valid @RequestBody CreateCharacterRequest request
    );

    @Operation(
        summary = "캐릭터 상세 조회",
        description = "캐릭터 ID로 캐릭터의 상세 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    @GetMapping("/{characterId}")
    ApiResult<CharacterResponse> getCharacter(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );

    @Operation(
        summary = "계좌 연결",
        description = "캐릭터와 계좌를 연결합니다."
    )
    @ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    @ApiResponse(responseCode = "409", description = "이미 계좌가 연결되어 있음")
    @PutMapping("/{characterId}/account")
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
    @ApiResponse(responseCode = "400", description = "해제할 수 있는 계좌 연결이 없음")
    @ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    @DeleteMapping("/{characterId}/account")
    ApiResult<Void> cancelAccountConnection(
        @Parameter(description = "캐릭터 ID", required = true, example = "1")
        @PathVariable Long characterId
    );
}