package saviing.game.pet.presentation.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import saviing.common.response.ApiResult;
import saviing.game.pet.application.dto.query.GetPetInfoQuery;
import saviing.game.pet.application.dto.result.PetResult;
import saviing.game.pet.application.service.PetQueryService;
import saviing.game.pet.presentation.dto.response.PetInfoResponse;
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
    private final PetResponseMapper petResponseMapper;

    @Override
    @GetMapping("/pets/{petId}")
    public ApiResult<PetInfoResponse> getPetInfo(
        @PathVariable Long petId,
        @RequestParam Long characterId
    ) {
        log.info("펫 정보 조회 요청: petId={}, characterId={}", petId, characterId);

        GetPetInfoQuery query = GetPetInfoQuery.of(petId, characterId);
        PetResult result = petQueryService.getPetInfo(query);
        PetInfoResponse response = petResponseMapper.toResponse(result);

        log.info("펫 정보 조회 완료: petId={}, petName={}", petId, response.name());
        return ApiResult.ok(response);
    }
}