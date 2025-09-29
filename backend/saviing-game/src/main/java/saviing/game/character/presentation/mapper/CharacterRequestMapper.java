package saviing.game.character.presentation.mapper;

import org.springframework.stereotype.Component;
import saviing.game.character.application.dto.command.AddCoinsCommand;
import saviing.game.character.application.dto.command.CancelAccountConnectionCommand;
import saviing.game.character.application.dto.command.CompleteAccountConnectionCommand;
import saviing.game.character.application.dto.command.ConnectAccountCommand;
import saviing.game.character.application.dto.command.CreateCharacterCommand;
import saviing.game.character.application.dto.command.DeactivateCharacterCommand;
import saviing.game.character.application.dto.command.HandleAccountTerminatedCommand;
import saviing.game.character.application.dto.command.IncreaseRoomCountCommand;
import saviing.game.character.application.dto.query.GetActiveCharacterQuery;
import saviing.game.character.application.dto.query.GetAllCharactersByCustomerQuery;
import saviing.game.character.application.dto.query.GetCharacterQuery;
import saviing.game.character.application.dto.query.GetGameEntryQuery;
import saviing.game.character.application.dto.query.GetCharacterStatisticsQuery;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.presentation.dto.request.ConnectAccountRequest;
import saviing.game.character.presentation.dto.request.CreateCharacterRequest;

/**
 * Presentation layer Request를 Application layer Command/Query로 변환하는 Mapper
 * Presentation 계층에서 Application 계층으로의 변환을 담당합니다.
 */
@Component
public class CharacterRequestMapper {

    // ========== Command 변환 메서드들 ==========

    /**
     * CreateCharacterRequest를 CreateCharacterCommand로 변환합니다.
     *
     * @param request CreateCharacterRequest
     * @return CreateCharacterCommand
     */
    public CreateCharacterCommand toCommand(CreateCharacterRequest request) {
        return CreateCharacterCommand.builder()
            .customerId(CustomerId.of(request.customerId()))
            .build();
    }

    /**
     * ConnectAccountRequest를 ConnectAccountCommand로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @param request ConnectAccountRequest
     * @return ConnectAccountCommand
     */
    public ConnectAccountCommand toCommand(Long characterId, ConnectAccountRequest request) {
        return ConnectAccountCommand.builder()
            .characterId(CharacterId.of(characterId))
            .accountId(request.accountId())
            .build();
    }


    /**
     * 캐릭터 ID를 IncreaseRoomCountCommand로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @return IncreaseRoomCountCommand
     */
    public IncreaseRoomCountCommand toIncreaseRoomCountCommand(Long characterId) {
        return IncreaseRoomCountCommand.builder()
            .characterId(CharacterId.of(characterId))
            .build();
    }

    /**
     * 캐릭터 ID를 DeactivateCharacterCommand로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @return DeactivateCharacterCommand
     */
    public DeactivateCharacterCommand toDeactivateCommand(Long characterId) {
        return DeactivateCharacterCommand.builder()
            .characterId(CharacterId.of(characterId))
            .build();
    }

    /**
     * 캐릭터 ID를 CancelAccountConnectionCommand로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @return CancelAccountConnectionCommand
     */
    public CancelAccountConnectionCommand toCancelConnectionCommand(Long characterId) {
        return CancelAccountConnectionCommand.builder()
            .characterId(CharacterId.of(characterId))
            .build();
    }

    // ========== Query 변환 메서드들 ==========

    /**
     * 캐릭터 ID를 GetCharacterQuery로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetCharacterQuery
     */
    public GetCharacterQuery toQuery(Long characterId) {
        return GetCharacterQuery.builder()
            .characterId(CharacterId.of(characterId))
            .build();
    }

    /**
     * 고객 ID를 GetActiveCharacterQuery로 변환합니다.
     *
     * @param customerId 고객 ID
     * @return GetActiveCharacterQuery
     */
    public GetActiveCharacterQuery toActiveCharacterQuery(Long customerId) {
        return GetActiveCharacterQuery.builder()
            .customerId(CustomerId.of(customerId))
            .build();
    }

    /**
     * 고객 ID를 GetAllCharactersByCustomerQuery로 변환합니다.
     *
     * @param customerId 고객 ID
     * @return GetAllCharactersByCustomerQuery
     */
    public GetAllCharactersByCustomerQuery toAllCharactersByCustomerQuery(Long customerId) {
        return GetAllCharactersByCustomerQuery.builder()
            .customerId(CustomerId.of(customerId))
            .build();
    }

    /**
     * 고객 ID를 GetGameEntryQuery로 변환합니다.
     *
     * @param customerId 고객 ID
     * @return GetGameEntryQuery
     */
    public GetGameEntryQuery toGameEntryQuery(Long customerId) {
        return GetGameEntryQuery.builder()
            .customerId(CustomerId.of(customerId))
            .build();
    }

    /**
     * 캐릭터 ID를 GetCharacterStatisticsQuery로 변환합니다.
     *
     * @param characterId 캐릭터 ID
     * @return GetCharacterStatisticsQuery
     */
    public GetCharacterStatisticsQuery toStatisticsQuery(Long characterId) {
        return GetCharacterStatisticsQuery.builder()
            .characterId(CharacterId.of(characterId))
            .build();
    }
}