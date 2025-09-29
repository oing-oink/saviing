package saviing.game.inventory.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import saviing.common.exception.ErrorCode;

/**
 * 인벤토리 도메인 에러 코드
 * 인벤토리 관련 모든 에러 코드를 정의합니다.
 */
@Getter
@AllArgsConstructor
public enum InventoryErrorCode implements ErrorCode {
    // 인벤토리 아이템 관련 에러
    INVENTORY_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "인벤토리 아이템을 찾을 수 없습니다"),
    DUPLICATE_INVENTORY_ITEM(HttpStatus.CONFLICT, "이미 존재하는 인벤토리 아이템입니다"),
    INVALID_INVENTORY_OWNER(HttpStatus.FORBIDDEN, "인벤토리 아이템의 소유자가 아닙니다"),

    // 펫 관련 에러
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "펫을 찾을 수 없습니다"),
    PET_ALREADY_IN_USE(HttpStatus.CONFLICT, "이미 사용 중인 펫입니다"),
    PET_NOT_IN_USE(HttpStatus.BAD_REQUEST, "사용 중이지 않은 펫입니다"),
    PET_INSUFFICIENT_ENERGY(HttpStatus.BAD_REQUEST, "펫의 에너지가 부족합니다"),
    INVALID_PET_STATS(HttpStatus.BAD_REQUEST, "유효하지 않은 펫 스탯입니다"),

    // 액세서리 관련 에러
    ACCESSORY_NOT_FOUND(HttpStatus.NOT_FOUND, "액세서리를 찾을 수 없습니다"),
    ACCESSORY_ALREADY_EQUIPPED(HttpStatus.CONFLICT, "이미 장착된 액세서리입니다"),
    ACCESSORY_NOT_EQUIPPED(HttpStatus.BAD_REQUEST, "장착되지 않은 액세서리입니다"),
    INVALID_ACCESSORY_FOR_PET(HttpStatus.BAD_REQUEST, "해당 펫에 장착할 수 없는 액세서리입니다"),

    // 데코레이션 관련 에러
    DECORATION_NOT_FOUND(HttpStatus.NOT_FOUND, "데코레이션을 찾을 수 없습니다"),
    DECORATION_ALREADY_PLACED(HttpStatus.CONFLICT, "이미 배치된 데코레이션입니다"),
    DECORATION_NOT_PLACED(HttpStatus.BAD_REQUEST, "배치되지 않은 데코레이션입니다"),
    INVALID_DECORATION_POSITION(HttpStatus.BAD_REQUEST, "해당 위치에 배치할 수 없는 데코레이션입니다"),

    // 장착 관련 에러
    EQUIPMENT_SLOT_OCCUPIED(HttpStatus.CONFLICT, "이미 다른 액세서리가 장착된 슬롯입니다"),
    EQUIPMENT_RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "장착 관계를 찾을 수 없습니다"),
    INVALID_EQUIPMENT_OPERATION(HttpStatus.BAD_REQUEST, "유효하지 않은 장착 작업입니다"),

    // 일반적인 도메인 에러
    INVALID_INVENTORY_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 인벤토리 타입입니다"),
    INVENTORY_OPERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인벤토리 작업을 수행할 수 없습니다"),
    INVALID_INVENTORY_STATE(HttpStatus.BAD_REQUEST, "유효하지 않은 인벤토리 상태입니다");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}