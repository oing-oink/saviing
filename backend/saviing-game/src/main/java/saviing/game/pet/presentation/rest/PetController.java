package saviing.game.pet.presentation.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.game.character.application.dto.query.GetActiveCharacterQuery;
import saviing.game.character.application.dto.result.CharacterResult;
import saviing.game.character.application.service.CharacterQueryService;
import saviing.game.character.domain.exception.CharacterNotFoundException;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.inventory.domain.model.vo.InventoryItemId;
import saviing.game.pet.application.dto.command.ChangePetNameCommand;
import saviing.game.pet.application.dto.command.InteractWithPetCommand;
import saviing.game.pet.application.dto.query.GetPetInfoQuery;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.application.dto.result.PetInteractionResult;
import saviing.game.pet.application.service.PetCommandService;
import saviing.game.pet.application.service.PetQueryService;
import saviing.game.pet.presentation.dto.request.ChangePetNameRequest;
import saviing.game.pet.presentation.dto.request.PetInteractionRequest;
import saviing.game.pet.presentation.dto.response.PetInfoResponse;
import saviing.game.pet.presentation.dto.response.PetInteractionResponse;
import saviing.game.pet.presentation.interfaces.PetApi;
import saviing.game.pet.presentation.mapper.PetResponseMapper;

/**
 * 펫 REST API 컨트롤러
 * /v1/game/pets 경로로 펫 관련 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/v1/game")
@RequiredArgsConstructor
public class PetController implements PetApi {

    private final PetQueryService petQueryService;
    private final PetCommandService petCommandService;
    private final PetResponseMapper petResponseMapper;
    private final CharacterQueryService characterQueryService;

    @Override
    @GetMapping("/pets/{petId}")
    public ApiResult<PetInfoResponse> getPetInfo(
        @PathVariable Long petId
    ) {
        log.info("펫 정보 조회 요청: petId={}", petId);

        GetPetInfoQuery query = GetPetInfoQuery.of(petId);
        PetResult result = petQueryService.getPetInfo(query);
        PetInfoResponse response = petResponseMapper.toResponse(result);

        log.info("펫 정보 조회 완료: petId={}, petName={}", petId, response.name());
        return ApiResult.ok(response);
    }

    @Override
    @PostMapping("/pets/{petId}/interaction")
    public ApiResult<PetInteractionResponse> interactWithPet(
        @PathVariable Long petId,
        @Valid @RequestBody PetInteractionRequest request,
        Authentication authentication
    ) {
        log.info("펫 상호작용 요청: petId={}, interactionType={}", petId, request.type());

        Long customerId = Long.valueOf(authentication.getName());
        log.info("펫 상호작용 - 사용자 인증: customerId={}", customerId);

        CharacterId characterId;
        try {
            GetActiveCharacterQuery query = GetActiveCharacterQuery.of(customerId);
            CharacterResult characterResult = characterQueryService.getActiveCharacter(query);
            characterId = CharacterId.of(characterResult.characterId());
            log.info("펫 상호작용 - 활성 캐릭터 조회 성공: characterId={}", characterId.value());
        } catch (Exception e) {
            log.error("펫 상호작용 - 활성 캐릭터 조회 실패: customerId={}, error={}", customerId, e.getMessage());
            throw new CharacterNotFoundException("활성 캐릭터를 찾을 수 없습니다. 캐릭터를 생성하거나 활성화해주세요.");
        }

        InventoryItemId inventoryItemId = InventoryItemId.of(petId);
        InteractWithPetCommand command = InteractWithPetCommand.of(
            characterId,
            inventoryItemId,
            request.type()
        );

        PetInteractionResult interactionResult = petCommandService.interactWithPet(command);

        PetInteractionResponse response = petResponseMapper.toInteractionResponse(
            interactionResult.pet(),
            interactionResult.consumption()
        );

        log.info("펫 상호작용 완료: petId={}, interactionType={}", petId, request.type());
        return ApiResult.ok(response);
    }

    @Override
    @PatchMapping("/pets/{petId}/name")
    public ApiResult<PetInfoResponse> changePetName(
        @PathVariable Long petId,
        @Valid @RequestBody ChangePetNameRequest request
    ) {
        log.info("펫 이름 변경 요청: petId={}, newName={}", petId, request.name());

        InventoryItemId inventoryItemId = InventoryItemId.of(petId);
        ChangePetNameCommand command = ChangePetNameCommand.of(inventoryItemId, request.name());

        PetResult result = petCommandService.changePetName(command);
        PetInfoResponse response = petResponseMapper.toResponse(result);

        log.info("펫 이름 변경 완료: petId={}, newName={}", petId, response.name());
        return ApiResult.ok(response);
    }
}