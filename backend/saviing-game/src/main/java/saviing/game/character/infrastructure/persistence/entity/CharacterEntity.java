package saviing.game.character.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import saviing.game.character.domain.model.enums.ConnectionStatus;

import java.time.LocalDateTime;

/**
 * Character JPA Entity
 * MySQL game.characters 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "characters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // Account Connection Info
    @Column(name = "account_id")
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false, length = 20)
    private ConnectionStatus connectionStatus;

    @Column(name = "connection_date")
    private LocalDateTime connectionDate;

    // Termination Info
    @Column(name = "termination_reason", columnDefinition = "TEXT")
    private String terminationReason;

    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    // Game Status
    @Column(name = "coin", nullable = false)
    private Integer coin;

    @Column(name = "fish_coin", nullable = false)
    private Integer fishCoin;

    @Column(name = "room_count", nullable = false)
    private Integer roomCount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // Character Deactivation
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    // Lifecycle
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public CharacterEntity(
        Long characterId,
        Long customerId,
        Long accountId,
        ConnectionStatus connectionStatus,
        LocalDateTime connectionDate,
        String terminationReason,
        LocalDateTime terminatedAt,
        Integer coin,
        Integer fishCoin,
        Integer roomCount,
        Boolean isActive,
        LocalDateTime deactivatedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.characterId = characterId;
        this.customerId = customerId;
        this.accountId = accountId;
        this.connectionStatus = connectionStatus;
        this.connectionDate = connectionDate;
        this.terminationReason = terminationReason;
        this.terminatedAt = terminatedAt;
        this.coin = coin;
        this.fishCoin = fishCoin;
        this.roomCount = roomCount;
        this.isActive = isActive;
        this.deactivatedAt = deactivatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 엔티티의 필드값들을 업데이트합니다.
     * 수정 시간은 자동으로 현재 시간으로 설정됩니다.
     * 
     * @param accountId 계좌 ID
     * @param connectionStatus 연결 상태
     * @param connectionDate 연결 시간
     * @param terminationReason 해지 사유
     * @param terminatedAt 해지 시간
     * @param coin 코인 수량
     * @param fishCoin 피쉬 코인 수량
     * @param roomCount 방 수
     * @param isActive 활성 상태
     * @param deactivatedAt 비활성화 시간
     */
    public void updateEntity(
        Long accountId,
        ConnectionStatus connectionStatus,
        LocalDateTime connectionDate,
        String terminationReason,
        LocalDateTime terminatedAt,
        Integer coin,
        Integer fishCoin,
        Integer roomCount,
        Boolean isActive,
        LocalDateTime deactivatedAt
    ) {
        this.accountId = accountId;
        this.connectionStatus = connectionStatus;
        this.connectionDate = connectionDate;
        this.terminationReason = terminationReason;
        this.terminatedAt = terminatedAt;
        this.coin = coin;
        this.fishCoin = fishCoin;
        this.roomCount = roomCount;
        this.isActive = isActive;
        this.deactivatedAt = deactivatedAt;
    }

    /**
     * 엔티티 저장 전 자동으로 호출되어 생성/수정 시간을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 엔티티 수정 전 자동으로 호출되어 수정 시간을 업데이트합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}