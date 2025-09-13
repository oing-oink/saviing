package saviing.game.character.presentation.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saviing.common.response.ApiResult;
import saviing.game.character.application.dto.AddCoinsCommand;
import saviing.game.character.application.dto.CompleteAccountConnectionCommand;
import saviing.game.character.application.dto.DeactivateCharacterCommand;
import saviing.game.character.application.dto.HandleAccountTerminatedCommand;
import saviing.game.character.application.service.CharacterApplicationService;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.presentation.dto.request.AddCoinsRequest;
import saviing.game.character.presentation.dto.request.CompleteAccountConnectionRequest;
import saviing.game.character.presentation.dto.request.HandleAccountTerminatedRequest;
import saviing.game.character.presentation.interfaces.CharacterInternalControllerInterface;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/internal/characters")
public class CharacterInternalController implements CharacterInternalControllerInterface {
    
    private final CharacterApplicationService characterApplicationService;
    
    @Override
    public ApiResult<Void> completeAccountConnection(@PathVariable Long characterId,
                                                   @Valid @RequestBody CompleteAccountConnectionRequest request) {
        log.info("Completing account connection for character: {}, account: {}", characterId, request.accountId());
        
        CompleteAccountConnectionCommand command = CompleteAccountConnectionCommand.of(
                new CharacterId(characterId), request.accountId());
        characterApplicationService.completeAccountConnection(command);
        
        return ApiResult.ok();
    }
    
    @Override
    public ApiResult<Void> addCoins(@PathVariable Long characterId,
                                   @Valid @RequestBody AddCoinsRequest request) {
        log.info("Adding coins to character: {}, coin: {}, fishCoin: {}", 
                characterId, request.coinAmount(), request.fishCoinAmount());
        
        AddCoinsCommand command;
        if (request.coinAmount() != null && request.fishCoinAmount() != null) {
            command = AddCoinsCommand.both(new CharacterId(characterId), request.coinAmount(), request.fishCoinAmount());
        } else if (request.coinAmount() != null) {
            command = AddCoinsCommand.coin(new CharacterId(characterId), request.coinAmount());
        } else if (request.fishCoinAmount() != null) {
            command = AddCoinsCommand.fishCoin(new CharacterId(characterId), request.fishCoinAmount());
        } else {
            throw new IllegalArgumentException("코인 또는 피쉬 코인 중 적어도 하나는 제공되어야 합니다");
        }
        
        characterApplicationService.addCoins(command);
        
        return ApiResult.ok();
    }
    
    @Override
    public ApiResult<Void> handleAccountTerminated(@PathVariable Long characterId,
                                                  @Valid @RequestBody HandleAccountTerminatedRequest request) {
        log.info("Handling account termination for character: {}, reason: {}", 
                characterId, request.terminationReason());
        
        HandleAccountTerminatedCommand command = HandleAccountTerminatedCommand.of(
                new CharacterId(characterId), request.terminationReason());
        characterApplicationService.handleAccountTerminated(command);
        
        return ApiResult.ok();
    }
    
    @Override
    public ApiResult<Void> deactivateCharacter(@PathVariable Long characterId) {
        log.info("Deactivating character: {}", characterId);
        
        DeactivateCharacterCommand command = DeactivateCharacterCommand.of(new CharacterId(characterId));
        characterApplicationService.deactivateCharacter(command);
        
        return ApiResult.ok();
    }
}