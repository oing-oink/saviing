package saviing.game.pet.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PetInfo JPA Entity
 * MySQL game.pet_info 테이블과 매핑됩니다.
 * energy 컬럼은 포만감을 의미합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pet_info")
public class PetInfoEntity {

    @Id
    @Column(name = "inventory_item_id")
    private Long inventoryItemId;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "experience", nullable = false)
    private Integer experience;

    @Column(name = "affection", nullable = false)
    private Integer affection;

    @Column(name = "energy", nullable = false)
    private Integer energy;

    @Column(name = "pet_name")
    private String petName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public PetInfoEntity(
        Long inventoryItemId,
        Integer level,
        Integer experience,
        Integer affection,
        Integer energy,
        String petName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.inventoryItemId = inventoryItemId;
        this.level = level;
        this.experience = experience;
        this.affection = affection;
        this.energy = energy;
        this.petName = petName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 엔티티의 필드값들을 업데이트합니다.
     * 수정 시간은 자동으로 현재 시간으로 설정됩니다.
     */
    public void updateEntity(
        Integer level,
        Integer experience,
        Integer affection,
        Integer energy,
        String petName
    ) {
        this.level = level;
        this.experience = experience;
        this.affection = affection;
        this.energy = energy;
        this.petName = petName;
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