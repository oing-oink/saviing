package saviing.game.character.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.character.domain.model.enums.TerminationCategory;
import saviing.game.character.domain.model.vo.AccountConnection;
import saviing.game.character.domain.model.vo.AccountTermination;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CharacterLifecycle;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.domain.model.vo.GameStatus;

/**
 * 캐릭터 Aggregate Root
 * 게임 캐릭터의 전체 생명주기와 비즈니스 규칙을 관리합니다.
 * 고객당 하나의 활성 캐릭터만 존재할 수 있습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character {
    private CharacterId characterId;
    private CustomerId customerId;
    private AccountConnection accountConnection;
    private GameStatus gameStatus;
    private CharacterLifecycle characterLifecycle;

    @Builder
    private Character(
        CharacterId characterId,
        CustomerId customerId,
        AccountConnection accountConnection,
        GameStatus gameStatus,
        CharacterLifecycle characterLifecycle
    ) {
        this.characterId = characterId;
        this.customerId = customerId;
        this.accountConnection = accountConnection != null ? accountConnection : AccountConnection.noAccount();
        this.gameStatus = gameStatus != null ? gameStatus : GameStatus.initialize();
        this.characterLifecycle = characterLifecycle != null ? characterLifecycle : CharacterLifecycle.createNew();
        
        validateInvariants();
    }

    /**
     * 새로운 캐릭터를 생성합니다.
     * 
     * @param customerId 고객 식별자
     * @return 생성된 캐릭터
     */
    public static Character create(CustomerId customerId) {
        return Character.builder()
            .customerId(customerId)
            .accountConnection(AccountConnection.noAccount())
            .gameStatus(GameStatus.initialize())
            .characterLifecycle(CharacterLifecycle.createNew())
            .build();
    }

    /**
     * 계좌 연결을 시작합니다 (연결 중 상태로 전환).
     * 
     * @param accountId 연결할 계좌 ID
     * @throws IllegalStateException 이미 연결되어 있거나 연결 중인 경우
     */
    public void startConnectingAccount(Long accountId) {
        if (accountConnection.isConnected()) {
            throw new IllegalStateException("Account is already connected");
        }
        if (accountConnection.isConnecting()) {
            throw new IllegalStateException("Account connection is already in progress");
        }
        
        this.accountConnection = AccountConnection.connecting(accountId);
        updateLifecycle();
    }

    /**
     * 계좌 연결을 완료합니다 (연결 중 -> 연결됨).
     * 
     * @param accountId 연결된 계좌 ID
     * @throws IllegalStateException 연결 중 상태가 아닌 경우
     */
    public void completeAccountConnection(Long accountId) {
        if (!accountConnection.isConnecting()) {
            throw new IllegalStateException("Account must be in connecting state to complete connection");
        }
        
        this.accountConnection = AccountConnection.connected(accountId);
        updateLifecycle();
    }

    /**
     * 계좌을 해지합니다 (연결됨 -> 해지됨).
     * 
     * @param category 해지 분류
     * @param reason 해지 사유
     * @throws IllegalStateException 계좌가 연결되어 있지 않은 경우
     */
    public void terminateAccount(TerminationCategory category, String reason) {
        if (!accountConnection.isConnected()) {
            throw new IllegalStateException("Account must be connected to terminate");
        }
        
        AccountTermination termination = new AccountTermination(category, reason, java.time.LocalDateTime.now());
        this.accountConnection = AccountConnection.terminated(termination);
        updateLifecycle();
    }

    /**
     * 고객 요청에 의해 계좌을 해지합니다.
     * 
     * @param reason 해지 사유
     * @throws IllegalStateException 계좌가 연결되어 있지 않은 경우
     */
    public void terminateAccountByCustomerRequest(String reason) {
        terminateAccount(TerminationCategory.CUSTOMER_REQUEST, reason);
    }

    /**
     * 시스템 오류로 인해 계좌을 해지합니다.
     * 
     * @param reason 해지 사유
     * @throws IllegalStateException 계좌가 연결되어 있지 않은 경우
     */
    public void terminateAccountBySystemError(String reason) {
        terminateAccount(TerminationCategory.SYSTEM_ERROR, reason);
    }

    /**
     * 연결 시도를 취소합니다 (연결 중 -> 계좌 없음).
     * 
     * @throws IllegalStateException 연결 중 상태가 아닌 경우
     */
    public void cancelConnection() {
        if (!accountConnection.isConnecting()) {
            throw new IllegalStateException("Account must be in connecting state to cancel");
        }
        
        this.accountConnection = AccountConnection.noAccount();
        updateLifecycle();
    }

    /**
     * 코인을 추가합니다.
     * 
     * @param amount 추가할 코인 수량
     * @throws IllegalArgumentException amount가 0 이하인 경우
     */
    public void addCoin(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        this.gameStatus = gameStatus.addCoin(amount);
        updateLifecycle();
    }

    /**
     * 피쉬 코인을 추가합니다.
     * 
     * @param amount 추가할 피쉬 코인 수량
     * @throws IllegalArgumentException amount가 0 이하인 경우
     */
    public void addFishCoin(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        this.gameStatus = gameStatus.addFishCoin(amount);
        updateLifecycle();
    }

    /**
     * 방 수를 1 증가시킵니다.
     */
    public void increaseRoomCount() {
        this.gameStatus = gameStatus.increaseRoomCount();
        updateLifecycle();
    }

    /**
     * 캐릭터를 비활성화합니다.
     */
    public void deactivate() {
        this.gameStatus = gameStatus.deactivate();
        this.characterLifecycle = characterLifecycle.deactivate();
    }

    /**
     * 캐릭터를 활성화합니다.
     */
    public void activate() {
        this.gameStatus = gameStatus.activate();
        this.characterLifecycle = characterLifecycle.activate();
    }

    /**
     * 계좌가 연결되어 있는지 확인합니다.
     * 
     * @return 계좌 연결 여부
     */
    public boolean isAccountConnected() {
        return accountConnection.isConnected();
    }

    /**
     * 계좌가 연결 중인지 확인합니다.
     * 
     * @return 계좌 연결 중 여부
     */
    public boolean isAccountConnecting() {
        return accountConnection.isConnecting();
    }

    /**
     * 계좌가 없는 상태인지 확인합니다.
     * 
     * @return 계좌 없는 상태 여부
     */
    public boolean hasNoAccount() {
        return accountConnection.hasNoAccount();
    }

    /**
     * 계좌가 해지되어 있는지 확인합니다.
     * 
     * @return 계좌 해지 여부
     */
    public boolean isAccountTerminated() {
        return accountConnection.isTerminated();
    }

    /**
     * 해지 정보를 반환합니다.
     * 
     * @return 해지 정보 (Optional)
     */
    public java.util.Optional<AccountTermination> getTerminationInfo() {
        return java.util.Optional.ofNullable(accountConnection.terminationInfo());
    }

    /**
     * 캐릭터가 활성화되어 있는지 확인합니다.
     * 
     * @return 캐릭터 활성화 여부
     */
    public boolean isActive() {
        return gameStatus.isActive();
    }

    private void updateLifecycle() {
        this.characterLifecycle = characterLifecycle.updateModified();
    }

    private void validateInvariants() {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (accountConnection == null) {
            throw new IllegalArgumentException("Account connection cannot be null");
        }
        if (gameStatus == null) {
            throw new IllegalArgumentException("Game status cannot be null");
        }
        if (characterLifecycle == null) {
            throw new IllegalArgumentException("Character lifecycle cannot be null");
        }
    }
}