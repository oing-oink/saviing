package saviing.game.pet.domain.exception;

import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫이 이미 존재할 때 발생하는 예외
 */
public class PetAlreadyExistsException extends PetException {

    public PetAlreadyExistsException() {
        super(PetErrorCode.PET_ALREADY_EXISTS);
    }

    public PetAlreadyExistsException(String message) {
        super(PetErrorCode.PET_ALREADY_EXISTS, message);
    }

    public PetAlreadyExistsException(InventoryItemId inventoryItemId) {
        super(PetErrorCode.PET_ALREADY_EXISTS, "이미 존재하는 펫입니다. InventoryItemId: " + inventoryItemId.value());
    }
}