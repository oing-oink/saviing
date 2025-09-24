package saviing.game.pet.domain.exception;

import saviing.game.item.domain.model.enums.Consumption;

/**
 * 소모품 부족 시 발생하는 예외
 */
public class PetInsufficientConsumptionItemsException extends PetException {

    public PetInsufficientConsumptionItemsException(Long characterId, Consumption consumptionType) {
        super(PetErrorCode.PET_INSUFFICIENT_CONSUMPTION_ITEMS,
              String.format("필요한 소모품이 부족합니다. 캐릭터 ID: %d, 필요한 소모품: %s", characterId, consumptionType));
    }
}