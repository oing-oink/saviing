package saviing.game.character.domain.model.aggregate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.character.domain.event.AccountConnectedEvent;
import saviing.game.character.domain.event.AccountTerminatedEvent;
import saviing.game.character.domain.event.CharacterCreatedEvent;
import saviing.game.character.domain.event.CharacterDeactivatedEvent;
import saviing.game.character.domain.event.DomainEvent;
import saviing.game.character.domain.exception.InvalidAccountConnectionException;
import saviing.game.character.domain.exception.InvalidCharacterStateException;
import saviing.game.character.domain.model.vo.AccountConnection;
import saviing.game.character.domain.model.vo.AccountTermination;
import saviing.game.character.domain.model.vo.CharacterId;
import saviing.game.character.domain.model.vo.CharacterLifecycle;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.domain.model.vo.GameStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Character 생성자 (Builder 패턴 사용)
     * 
     * @param characterId 캐릭터 식별자
     * @param customerId 고객 식별자
     * @param accountConnection 계좌 연결 정보
     * @param gameStatus 게임 상태
     * @param characterLifecycle 캐릭터 생명주기
     */
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
        Character character = Character.builder()
            .customerId(customerId)
            .accountConnection(AccountConnection.noAccount())
            .gameStatus(GameStatus.initialize())
            .characterLifecycle(CharacterLifecycle.createNew())
            .build();
        
        character.addDomainEvent(CharacterCreatedEvent.of(character.characterId, customerId));
        return character;
    }

    /**
     * 계좌 연결을 시작합니다 (연결 중 상태로 전환).
     * 
     * @param accountId 연결할 계좌 ID
     * @throws IllegalStateException 이미 연결되어 있거나 연결 중인 경우
     */
    public void startConnectingAccount(Long accountId) {
        if (accountConnection.isConnected()) {
            throw InvalidAccountConnectionException.accountAlreadyConnected();
        }
        if (accountConnection.isConnecting()) {
            throw InvalidAccountConnectionException.connectionInProgress();
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
            throw InvalidAccountConnectionException.invalidConnectionState(
                "계좌 연결을 완료하려면 연결 중 상태여야 합니다");
        }
        
        this.accountConnection = AccountConnection.connected(accountId);
        updateLifecycle();
        
        addDomainEvent(AccountConnectedEvent.of(this.characterId, this.customerId, accountId));
    }

    /**
     * 외부에서 전달받은 계좌 해지 정보를 처리합니다.
     * 계좌 해지는 외부 도메인에서 처리되고, 그 결과를 이 메서드로 전달받습니다.
     *
     * @param reason 해지 사유
     * @throws IllegalStateException 계좌가 연결되어 있지 않은 경우
     */
    public void handleAccountTerminated(String reason) {
        if (!accountConnection.isConnected()) {
            throw InvalidAccountConnectionException.accountNotConnected();
        }
        
        AccountTermination termination = new AccountTermination(reason, java.time.LocalDateTime.now());
        this.accountConnection = AccountConnection.terminated(termination);
        updateLifecycle();
        
        addDomainEvent(AccountTerminatedEvent.of(this.characterId, this.customerId, reason));
    }

    /**
     * 연결 시도를 취소합니다 (연결 중 -> 계좌 없음).
     * 
     * @throws IllegalStateException 연결 중 상태가 아닌 경우
     */
    public void cancelConnection() {
        if (!accountConnection.isConnecting()) {
            throw InvalidAccountConnectionException.invalidConnectionState(
                "연결 취소를 하려면 연겴 중 상태여야 합니다");
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
            throw InvalidCharacterStateException.invalidCoinAmount("코인 수량은 양수여야 합니다");
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
            throw InvalidCharacterStateException.invalidFishCoinAmount("피쉬 코인 수량은 양수여야 합니다");
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
        
        addDomainEvent(CharacterDeactivatedEvent.of(this.characterId, this.customerId));
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
     * @return 해지 정보
     */
    public AccountTermination getTerminationInfo() {
        return accountConnection.terminationInfo();
    }

    /**
     * 캐릭터가 활성화되어 있는지 확인합니다.
     * 
     * @return 캐릭터 활성화 여부
     */
    public boolean isActive() {
        return gameStatus.isActive();
    }

    /**
     * 캐릭터 생명주기를 업데이트합니다 (수정 시간 갱신).
     */
    private void updateLifecycle() {
        this.characterLifecycle = characterLifecycle.updateModified();
    }

    /**
     * 발행된 도메인 이벤트 목록을 반환합니다.
     * 
     * @return 도메인 이벤트 목록 (읽기 전용)
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 도메인 이벤트를 모두 삭제합니다.
     * 이벤트 발행 후 호출되어야 합니다.
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    
    /**
     * 도메인 이벤트를 추가합니다.
     * 
     * @param event 추가할 도메인 이벤트
     */
    private void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * 캐릭터 불변 조건을 검증합니다.
     * 
     * @throws IllegalArgumentException 필수 필드가 null인 경우
     */
    private void validateInvariants() {
        if (customerId == null) {
            throw new IllegalArgumentException("고객 ID는 null일 수 없습니다");
        }
        if (accountConnection == null) {
            throw new IllegalArgumentException("계좌 연결 정보는 null일 수 없습니다");
        }
        if (gameStatus == null) {
            throw new IllegalArgumentException("게임 상태는 null일 수 없습니다");
        }
        if (characterLifecycle == null) {
            throw new IllegalArgumentException("캐릭터 생명주기는 null일 수 없습니다");
        }
    }
}