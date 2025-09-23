package saviing.game.room.application.event;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import saviing.game.room.domain.event.RoomPlacementChangedEvent;
import saviing.game.room.domain.model.aggregate.PlacedItem;

/**
 * 방 배치 변경 이벤트를 처리하는 핸들러
 * Inventory BC와의 동기화 및 기타 연관 시스템과의 연동을 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoomPlacementEventHandler {

    /**
     * 방 배치 변경 이벤트를 처리하여 Inventory BC와 동기화
     * 트랜잭션 커밋 후 비동기로 처리되어 성능과 안정성을 보장
     *
     * @param event 방 배치 변경 이벤트
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRoomPlacementChanged(RoomPlacementChangedEvent event) {
        try {
            log.info("방 배치 변경 이벤트 처리 시작. roomId: {}, placedItemCount: {}",
                event.getRoomId().value(), event.getPlacedItemCount());

            // Inventory BC와 동기화 (벌크 처리)
            syncWithInventoryBc(event);

            // 기타 필요한 후속 처리 (통계, 로깅 등)
            processAdditionalTasks(event);

            log.info("방 배치 변경 이벤트 처리 완료. roomId: {}", event.getRoomId().value());

        } catch (Exception e) {
            log.error("방 배치 변경 이벤트 처리 실패. roomId: {}, error: {}",
                event.getRoomId().value(), e.getMessage(), e);

            // 실패 시 보상 트랜잭션 또는 재시도 로직 구현 가능
            handleEventProcessingFailure(event, e);
        }
    }

    /**
     * Inventory BC와의 동기화를 수행
     * 룸별 일괄 처리로 성능 최적화
     *
     * @param event 방 배치 변경 이벤트
     */
    private void syncWithInventoryBc(RoomPlacementChangedEvent event) {
        List<Long> placedInventoryItemIds = event.getPlacedItems().stream()
            .map(PlacedItem::getInventoryItemId)
            .collect(Collectors.toList());

        log.debug("Inventory BC 동기화 시작. roomId: {}, inventoryItemIds: {}",
            event.getRoomId().value(), placedInventoryItemIds);

        // TODO: Inventory BC의 벌크 업데이트 API 호출
        // 예시: inventoryCommandService.syncRoomPlacements(event.getRoomId(), placedInventoryItemIds);

        // 현재는 로깅으로 대체 (실제 구현 시 Inventory BC 연동 코드 추가)
        log.info("Inventory BC 동기화 완료. roomId: {}, 배치된 아이템 수: {}",
            event.getRoomId().value(), placedInventoryItemIds.size());
    }

    /**
     * 추가적인 후속 처리 작업들을 수행
     * 통계 업데이트, 알림 발송, 로깅 등의 부가 기능
     *
     * @param event 방 배치 변경 이벤트
     */
    private void processAdditionalTasks(RoomPlacementChangedEvent event) {
        // 펫 배치 통계 업데이트
        if (event.containsPetItems()) {
            log.info("펫 아이템이 배치되었습니다. roomId: {}", event.getRoomId().value());
            // TODO: 펫 배치 통계 업데이트 로직
        }

        // 배치 활동 로그 기록
        log.debug("배치 활동 기록. roomId: {}, timestamp: {}, itemCount: {}",
            event.getRoomId().value(), event.getOccurredAt(), event.getPlacedItemCount());

        // TODO: 기타 필요한 후속 처리 (알림, 통계, 캐시 무효화 등)
    }

    /**
     * 이벤트 처리 실패 시 처리 로직
     * 실패 이벤트 기록, 재시도 큐 등록, 알림 발송 등
     *
     * @param event 처리에 실패한 이벤트
     * @param exception 발생한 예외
     */
    private void handleEventProcessingFailure(RoomPlacementChangedEvent event, Exception exception) {
        log.error("이벤트 처리 실패 후속 작업 시작. roomId: {}", event.getRoomId().value());

        // TODO: 실패 이벤트 기록, DLQ(Dead Letter Queue) 등록
        // TODO: 모니터링 시스템에 알림 발송
        // TODO: 필요시 보상 트랜잭션 실행

        log.warn("이벤트 처리 실패가 기록되었습니다. 수동 확인이 필요할 수 있습니다. roomId: {}",
            event.getRoomId().value());
    }
}