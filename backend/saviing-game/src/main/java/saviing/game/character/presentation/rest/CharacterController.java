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
import saviing.game.character.application.dto.CancelAccountConnectionCommand;
import saviing.game.character.application.dto.ConnectAccountCommand;
import saviing.game.character.application.dto.CreateCharacterCommand;
import saviing.game.character.application.service.CharacterApplicationService;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.presentation.dto.request.ConnectAccountRequest;
import saviing.game.character.presentation.dto.request.CreateCharacterRequest;
import saviing.game.character.presentation.dto.response.CharacterResponse;
import saviing.game.character.presentation.interfaces.CharacterControllerInterface;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/games/characters")
public class CharacterController implements CharacterControllerInterface {
    
    private final CharacterApplicationService characterApplicationService;
    
    @Override
    public ApiResult<CharacterResponse> createCharacter(@Valid @RequestBody CreateCharacterRequest request) {
        log.info("Creating character for customer: {}", request.customerId());
        
        CreateCharacterCommand command = CreateCharacterCommand.of(new CustomerId(request.customerId()));
        Character character = characterApplicationService.createCharacter(command);
        CharacterResponse response = CharacterResponse.from(character);
        
        return ApiResult.of(HttpStatus.CREATED, response);
    }
    
    @Override
    public ApiResult<CharacterResponse> getCharacter(@PathVariable Long characterId) {
        log.info("Getting character: {}", characterId);
        
        Character character = characterApplicationService.getCharacter(new CharacterId(characterId));
        CharacterResponse response = CharacterResponse.from(character);
        
        return ApiResult.ok(response);
    }
    
    @Override
    public ApiResult<Void> connectAccount(@PathVariable Long characterId, 
                                        @Valid @RequestBody ConnectAccountRequest request) {
        log.info("Connecting account for character: {}, account: {}", characterId, request.accountId());
        
        ConnectAccountCommand command = ConnectAccountCommand.of(new CharacterId(characterId), request.accountId());
        characterApplicationService.connectAccount(command);
        
        return ApiResult.ok();
    }
    
    @Override
    public ApiResult<Void> cancelAccountConnection(@PathVariable Long characterId) {
        log.info("Canceling account connection for character: {}", characterId);
        
        CancelAccountConnectionCommand command = CancelAccountConnectionCommand.of(new CharacterId(characterId));
        characterApplicationService.cancelAccountConnection(command);
        
        return ApiResult.ok();
    }

}