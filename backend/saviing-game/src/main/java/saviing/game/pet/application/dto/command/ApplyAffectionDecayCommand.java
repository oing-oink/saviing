package saviing.game.pet.application.dto.command;

import lombok.Builder;
import saviing.game.inventory.domain.model.vo.InventoryItemId;

import java.time.LocalDateTime;

/**
 * 애정도 감소 적용 Command
 * 시간 경과에 따른 펫의 애정도 감소를 적용하기 위한 명령 객체입니다.
 */
@Builder
public record ApplyAffectionDecayCommand(
    InventoryItemId inventoryItemId,
    LocalDateTime lastAccessTime
) {
    public ApplyAffectionDecayCommand {
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("인벤토리 아이템 ID는 null일 수 없습니다");
        }
        if (lastAccessTime == null) {
            throw new IllegalArgumentException("마지막 접속 시간은 null일 수 없습니다");
        }
    }

    /**
     * ApplyAffectionDecayCommand를 생성합니다.
     *
     * @param inventoryItemId 펫의 인벤토리 아이템 ID
     * @param lastAccessTime 마지막 접속 시간
     * @return ApplyAffectionDecayCommand 인스턴스
     */
    public static ApplyAffectionDecayCommand of(InventoryItemId inventoryItemId, LocalDateTime lastAccessTime) {
        return ApplyAffectionDecayCommand.builder()
            .inventoryItemId(inventoryItemId)
            .lastAccessTime(lastAccessTime)
            .build();
    }
}