package saviing.game.character.domain.model.vo;

import saviing.game.character.domain.model.enums.ConnectionStatus;

import java.time.LocalDateTime;

/**
 * 계좌 연결 상태를 관리하는 Value Object
 * 계좌 ID, 연결 상태, 연결 시간, 해지 정보를 포함합니다.
 */
public record AccountConnection(
    Long accountId,
    ConnectionStatus connectionStatus,
    LocalDateTime connectionDate,
    AccountTermination terminationInfo
) {
    public AccountConnection {
        if (connectionStatus == null) {
            throw new IllegalArgumentException("Connection status cannot be null");
        }
        
        if (connectionStatus == ConnectionStatus.CONNECTED && accountId == null) {
            throw new IllegalArgumentException("Account ID is required when status is CONNECTED");
        }
        
        if (connectionStatus == ConnectionStatus.CONNECTED && connectionDate == null) {
            throw new IllegalArgumentException("Connection date is required when status is CONNECTED");
        }
        
        if (connectionStatus == ConnectionStatus.CONNECTING && accountId == null) {
            throw new IllegalArgumentException("Account ID is required when status is CONNECTING");
        }
        
        if (connectionStatus == ConnectionStatus.TERMINATED && terminationInfo == null) {
            throw new IllegalArgumentException("Termination info is required when status is TERMINATED");
        }
        
        if (connectionStatus != ConnectionStatus.TERMINATED && terminationInfo != null) {
            throw new IllegalArgumentException("Termination info should only be present when status is TERMINATED");
        }
    }

    /**
     * 계좌 없는 상태의 AccountConnection을 생성합니다 (초기 상태).
     * 
     * @return 계좌 없는 상태의 AccountConnection
     */
    public static AccountConnection noAccount() {
        return new AccountConnection(null, ConnectionStatus.NO_ACCOUNT, null, null);
    }

    /**
     * 연결 중 상태의 AccountConnection을 생성합니다.
     * 
     * @param accountId 연결 시도할 계좌 ID
     * @return 연결 중 상태의 AccountConnection
     */
    public static AccountConnection connecting(Long accountId) {
        return new AccountConnection(accountId, ConnectionStatus.CONNECTING, null, null);
    }

    /**
     * 연결 상태의 AccountConnection을 생성합니다.
     * 
     * @param accountId 연결할 계좌 ID
     * @return 연결 상태의 AccountConnection
     */
    public static AccountConnection connected(Long accountId) {
        return new AccountConnection(accountId, ConnectionStatus.CONNECTED, LocalDateTime.now(), null);
    }

    /**
     * 해지 상태의 AccountConnection을 생성합니다.
     * 
     * @param terminationInfo 해지 정보
     * @return 해지 상태의 AccountConnection
     */
    public static AccountConnection terminated(AccountTermination terminationInfo) {
        return new AccountConnection(null, ConnectionStatus.TERMINATED, null, terminationInfo);
    }

    /**
     * 계좌가 연결되어 있는지 확인합니다.
     * 
     * @return 연결 여부
     */
    public boolean isConnected() {
        return connectionStatus == ConnectionStatus.CONNECTED;
    }

    /**
     * 계좌가 없는 상태인지 확인합니다.
     * 
     * @return 계좌 없는 상태 여부
     */
    public boolean hasNoAccount() {
        return connectionStatus == ConnectionStatus.NO_ACCOUNT;
    }

    /**
     * 계좌 연결이 진행 중인지 확인합니다.
     * 
     * @return 연결 중 상태 여부
     */
    public boolean isConnecting() {
        return connectionStatus == ConnectionStatus.CONNECTING;
    }

    /**
     * 계좌가 해지되어 있는지 확인합니다.
     * 
     * @return 해지 여부
     */
    public boolean isTerminated() {
        return connectionStatus == ConnectionStatus.TERMINATED;
    }

    /**
     * 해지 정보가 있는지 확인합니다.
     * 
     * @return 해지 정보 존재 여부
     */
    public boolean hasTerminationInfo() {
        return terminationInfo != null;
    }
}