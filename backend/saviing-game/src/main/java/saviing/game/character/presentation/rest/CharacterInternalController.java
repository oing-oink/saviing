package saviing.game.character.presentation.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saviing.common.response.ApiResult;
import saviing.game.character.application.service.CharacterCommandService;
import saviing.game.character.presentation.dto.request.AddCoinsRequest;
import saviing.game.character.presentation.dto.request.CompleteAccountConnectionRequest;
import saviing.game.character.presentation.dto.request.HandleAccountTerminatedRequest;
import saviing.game.character.presentation.interfaces.CharacterInternalControllerInterface;
import saviing.game.character.presentation.mapper.CharacterRequestMapper;

/**
 * 내부 시스템 간 통신을 위한 캐릭터 API 컨트롤러
 * 은행 도메인 등 다른 모듈에서 호출하는 내부 API를 제공합니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/internal/characters")
public class CharacterInternalController implements CharacterInternalControllerInterface {

    private final CharacterCommandService characterCommandService;
    private final CharacterRequestMapper requestMapper;

    @Override
    public ApiResult<Void> completeAccountConnection(@PathVariable Long characterId,
                                                   @Valid @RequestBody CompleteAccountConnectionRequest request) {
        log.info("Completing account connection for character: {}, account: {}", characterId, request.accountId());

        characterCommandService.completeAccountConnection(
            requestMapper.toCommand(characterId, request)
        );

        return ApiResult.ok();
    }

    @Override
    public ApiResult<Void> addCoins(@PathVariable Long characterId,
                                   @Valid @RequestBody AddCoinsRequest request) {
        log.info("Adding coins to character: {}, coin: {}, fishCoin: {}",
                characterId, request.coinAmount(), request.fishCoinAmount());

        characterCommandService.addCoins(
            requestMapper.toCommand(characterId, request)
        );

        return ApiResult.ok();
    }

    @Override
    public ApiResult<Void> handleAccountTerminated(@PathVariable Long characterId,
                                                  @Valid @RequestBody HandleAccountTerminatedRequest request) {
        log.info("Handling account termination for character: {}, reason: {}",
                characterId, request.terminationReason());

        characterCommandService.handleAccountTerminated(
            requestMapper.toCommand(characterId, request)
        );

        return ApiResult.ok();
    }

    @Override
    public ApiResult<Void> deactivateCharacter(@PathVariable Long characterId) {
        log.info("Deactivating character: {}", characterId);

        characterCommandService.deactivateCharacter(
            requestMapper.toDeactivateCommand(characterId)
        );

        return ApiResult.ok();
    }
}