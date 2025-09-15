package saviing.game.character.domain.model.vo;

import java.time.LocalDateTime;

/**
 * 캐릭터의 생명주기 정보를 관리하는 Value Object
 * 생성 시간, 수정 시간, 비활성화 시간을 추적합니다.
 */
public record CharacterLifecycle(
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deactivatedAt
) {
    public CharacterLifecycle {
        if (createdAt == null) {
            throw new IllegalArgumentException("생성 시간은 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("수정 시간은 null일 수 없습니다");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("수정 시간은 생성 시간보다 이전일 수 없습니다");
        }
    }

    /**
     * 새로운 CharacterLifecycle을 생성합니다.
     * 생성 시간과 수정 시간이 동일하게 설정되고, 비활성화 시간은 null입니다.
     * 
     * @return 새로운 CharacterLifecycle
     */
    public static CharacterLifecycle createNew() {
        LocalDateTime now = LocalDateTime.now();
        return new CharacterLifecycle(now, now, null);
    }

    /**
     * 수정 시간을 현재 시간으로 업데이트합니다.
     * 
     * @return 수정 시간이 업데이트된 CharacterLifecycle
     */
    public CharacterLifecycle updateModified() {
        return new CharacterLifecycle(createdAt, LocalDateTime.now(), deactivatedAt);
    }

    /**
     * 지정된 시간 이후에 생성되었는지 확인합니다.
     * 
     * @param dateTime 비교할 시간
     * @return 지정된 시간 이후 생성 여부
     */
    public boolean isCreatedAfter(LocalDateTime dateTime) {
        return createdAt.isAfter(dateTime);
    }

    /**
     * 지정된 시간 이후에 수정되었는지 확인합니다.
     * 
     * @param dateTime 비교할 시간
     * @return 지정된 시간 이후 수정 여부
     */
    public boolean isModifiedAfter(LocalDateTime dateTime) {
        return updatedAt.isAfter(dateTime);
    }

    /**
     * 캐릭터를 비활성화합니다.
     * 
     * @return 비활성화 시간이 설정된 CharacterLifecycle
     */
    public CharacterLifecycle deactivate() {
        return new CharacterLifecycle(createdAt, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * 캐릭터를 활성화합니다 (비활성화 시간을 null로 설정).
     * 
     * @return 비활성화 시간이 제거된 CharacterLifecycle
     */
    public CharacterLifecycle activate() {
        return new CharacterLifecycle(createdAt, LocalDateTime.now(), null);
    }

    /**
     * 캐릭터가 비활성화되어 있는지 확인합니다.
     * 
     * @return 비활성화 여부
     */
    public boolean isDeactivated() {
        return deactivatedAt != null;
    }

    /**
     * 지정된 시간 이후에 비활성화되었는지 확인합니다.
     * 
     * @param dateTime 비교할 시간
     * @return 지정된 시간 이후 비활성화 여부
     */
    public boolean isDeactivatedAfter(LocalDateTime dateTime) {
        return deactivatedAt != null && deactivatedAt.isAfter(dateTime);
    }
}