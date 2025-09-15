package saviing.game.character.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import saviing.common.exception.ErrorCode;

/**
 * 캐릭터 도메인의 에러 코드를 정의하는 열거형
 */
@Getter
@AllArgsConstructor
public enum CharacterErrorCode implements ErrorCode {
    
    // 캐릭터 조회 관련
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "캐릭터를 찾을 수 없습니다"),
    
    // 캐릭터 생성 관련
    CHARACTER_DUPLICATE_ACTIVE(HttpStatus.CONFLICT, "이미 활성 캐릭터가 존재합니다"),
    
    // 계좌 연결 관련
    CHARACTER_ACCOUNT_NOT_CONNECTED(HttpStatus.BAD_REQUEST, "계좌가 연결되어 있지 않습니다"),
    CHARACTER_ACCOUNT_ALREADY_CONNECTED(HttpStatus.CONFLICT, "계좌가 이미 연결되어 있습니다"),
    CHARACTER_ACCOUNT_CONNECTION_IN_PROGRESS(HttpStatus.CONFLICT, "계좌 연결이 이미 진행 중입니다"),
    CHARACTER_INVALID_ACCOUNT_CONNECTION_STATE(HttpStatus.BAD_REQUEST, "잘못된 계좌 연결 상태입니다"),
    
    // 캐릭터 상태 관련
    CHARACTER_INVALID_STATE(HttpStatus.BAD_REQUEST, "잘못된 캐릭터 상태입니다"),
    CHARACTER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "캐릭터가 활성 상태가 아닙니다"),
    
    // 게임 상태 관련
    CHARACTER_INVALID_COIN_AMOUNT(HttpStatus.BAD_REQUEST, "올바르지 않은 코인 수량입니다"),
    CHARACTER_INVALID_FISH_COIN_AMOUNT(HttpStatus.BAD_REQUEST, "올바르지 않은 피쉬 코인 수량입니다")
    
    ;
    
    private final HttpStatus httpStatus;
    private final String message;
    
    @Override
    public String getCode() {
        return this.name();
    }
}