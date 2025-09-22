package saviing.game.pet.domain.exception;

import saviing.game.inventory.domain.model.vo.InventoryItemId;

/**
 * 펫 정보를 찾을 수 없을 때 발생하는 예외
 */
public class PetNotFoundException extends PetException {

    public PetNotFoundException() {
        super(PetErrorCode.PET_NOT_FOUND);
    }

    public PetNotFoundException(String message) {
        super(PetErrorCode.PET_NOT_FOUND, message);
    }

    public PetNotFoundException(InventoryItemId inventoryItemId) {
        super(PetErrorCode.PET_NOT_FOUND, "펫 정보를 찾을 수 없습니다. InventoryItemId: " + inventoryItemId.value());
    }

    public PetNotFoundException(Long inventoryItemId) {
        super(PetErrorCode.PET_NOT_FOUND, "펫 정보를 찾을 수 없습니다. InventoryItemId: " + inventoryItemId);
    }
}