package saviing.game.shop.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 인벤토리 도메인이 구현되기 전까지 사용할 Mock 서비스
 * 아이템 지급을 성공으로 가정하고 처리합니다.
 */
@Slf4j
@Service
public class InventoryMockService {

    /**
     * 캐릭터에게 아이템을 지급합니다. (Mock 구현)
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @param itemName 아이템 이름
     * @throws RuntimeException 아이템 지급 실패 시 (Mock에서는 발생하지 않음)
     */
    public void grantItemToCharacter(Long characterId, Long itemId, String itemName) {
        log.info("아이템 지급 시작 (Mock): characterId={}, itemId={}, itemName={}",
            characterId, itemId, itemName);

        // Mock 구현: 실제 인벤토리 시스템과 통신하는 것처럼 시뮬레이션
        simulateInventoryOperation();

        log.info("아이템 지급 완료 (Mock): characterId={}, itemId={}, itemName={}",
            characterId, itemId, itemName);
    }

    /**
     * 아이템 지급이 가능한지 확인합니다. (Mock 구현)
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @return 항상 true (Mock에서는 항상 가능)
     */
    public boolean canGrantItem(Long characterId, Long itemId) {
        log.debug("아이템 지급 가능 여부 확인 (Mock): characterId={}, itemId={} - 항상 가능",
            characterId, itemId);
        return true;
    }

    /**
     * 인벤토리 처리 결과를 확인합니다. (Mock 구현)
     *
     * @param characterId 캐릭터 ID
     * @param itemId 아이템 ID
     * @return 항상 true (Mock에서는 항상 성공)
     */
    public boolean verifyItemGranted(Long characterId, Long itemId) {
        log.debug("아이템 지급 결과 확인 (Mock): characterId={}, itemId={} - 항상 성공",
            characterId, itemId);
        return true;
    }

    /**
     * 인벤토리 작업을 시뮬레이션합니다.
     * 실제 시스템에서는 여기서 인벤토리 도메인과 통신합니다.
     */
    private void simulateInventoryOperation() {
        try {
            // 실제 인벤토리 시스템 호출을 시뮬레이션
            Thread.sleep(10); // 짧은 지연으로 실제 처리 시간 시뮬레이션
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("인벤토리 처리 시뮬레이션 중단됨");
        }
    }
}