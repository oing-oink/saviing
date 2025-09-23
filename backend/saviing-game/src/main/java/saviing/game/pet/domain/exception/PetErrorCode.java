package saviing.game.pet.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import saviing.common.exception.ErrorCode;

/**
 * 펫 도메인의 에러 코드를 정의하는 열거형
 */
@Getter
@AllArgsConstructor
public enum PetErrorCode implements ErrorCode {

    // 펫 조회 관련
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "펫 정보를 찾을 수 없습니다"),

    // 펫 상태 관련
    PET_INSUFFICIENT_ENERGY(HttpStatus.BAD_REQUEST, "펫의 포만감이 부족합니다"),
    PET_INVALID_STATE(HttpStatus.BAD_REQUEST, "잘못된 펫 상태입니다"),

    // 펫 생성 관련
    PET_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 펫입니다"),

    // 펫 이름 관련
    PET_INVALID_NAME(HttpStatus.BAD_REQUEST, "올바르지 않은 펫 이름입니다"),

    // 펫 접근 권한 관련
    PET_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 펫에 대한 접근 권한이 없습니다")

    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}