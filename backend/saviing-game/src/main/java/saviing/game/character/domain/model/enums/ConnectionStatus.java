package saviing.game.character.domain.model.enums;

/**
 * 계좌 연결 상태를 나타내는 열거형입니다.
 */
public enum ConnectionStatus {
    /** 계좌 없음 (초기 상태) */
    NO_ACCOUNT,

    /** 연결 중 (연결 요청 후 처리 중) */
    CONNECTING,

    /** 연결 완료 */
    CONNECTED,

    /** 연결 해지됨 */
    TERMINATED
}