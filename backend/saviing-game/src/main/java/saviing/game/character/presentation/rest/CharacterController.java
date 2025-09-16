package saviing.game.character.presentation.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saviing.common.response.ApiResult;
import saviing.game.character.application.dto.result.CharacterCreatedResult;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.application.service.CharacterCommandService;
import saviing.game.character.application.service.CharacterQueryService;
import saviing.game.character.presentation.dto.request.ConnectAccountRequest;
import saviing.game.character.presentation.dto.request.CreateCharacterRequest;
import saviing.game.character.presentation.dto.response.CharacterResponse;
import saviing.game.character.presentation.mapper.CharacterRequestMapper;
import saviing.game.character.presentation.mapper.CharacterResponseMapper;
import saviing.game.character.presentation.interfaces.CharacterApi;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * 캐릭터 관련 REST API를 제공하는 컨트롤러
 * 캐릭터 생성, 조회, 계좌 연결 등의 외부 API를 처리합니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/games/characters")
public class CharacterController implements CharacterApi {

    private final CharacterCommandService characterCommandService;
    private final CharacterQueryService characterQueryService;
    private final CharacterRequestMapper requestMapper;
    private final CharacterResponseMapper responseMapper;

    @Override
    @PostMapping
    public ApiResult<CharacterResponse> createCharacter(@Valid @RequestBody CreateCharacterRequest request) {
        log.info("Creating character for customer: {}", request.customerId());

        CharacterCreatedResult result = characterCommandService.createCharacter(
            requestMapper.toCommand(request)
        );
        CharacterResponse response = responseMapper.toResponse(result);

        return ApiResult.of(HttpStatus.CREATED, response);
    }

    @Override
    @GetMapping("/{characterId}")
    public ApiResult<CharacterResponse> getCharacter(@PathVariable Long characterId) {
        log.info("Getting character: {}", characterId);

        CharacterResult result = characterQueryService.getCharacter(
            requestMapper.toQuery(characterId)
        );
        CharacterResponse response = responseMapper.toResponse(result);

        return ApiResult.ok(response);
    }

    @Override
    @PutMapping("/{characterId}/account")
    public ApiResult<Void> connectAccount(@PathVariable Long characterId,
                                        @Valid @RequestBody ConnectAccountRequest request) {
        log.info("Connecting account for character: {}, account: {}", characterId, request.accountId());

        characterCommandService.connectAccount(
            requestMapper.toCommand(characterId, request)
        );

        return ApiResult.ok();
    }

    @Override
    @DeleteMapping("/{characterId}/account")
    public ApiResult<Void> cancelAccountConnection(@PathVariable Long characterId) {
        log.info("Canceling account connection for character: {}", characterId);

        characterCommandService.cancelAccountConnection(
            requestMapper.toCancelConnectionCommand(characterId)
        );

        return ApiResult.ok();
    }

}