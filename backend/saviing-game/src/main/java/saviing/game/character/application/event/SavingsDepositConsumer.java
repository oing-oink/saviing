package saviing.game.character.application.event;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import saviing.game.character.application.dto.command.AddCoinsCommand;
import saviing.game.character.application.service.CharacterCommandService;
import saviing.game.character.domain.model.aggregate.Character;
import saviing.game.character.domain.model.vo.CustomerId;
import saviing.game.character.domain.repository.CharacterRepository;
import saviing.game.config.StreamConsumerProperties;

/**
 * 적금 입금 이벤트 소비자
 * Redis Stream에서 적금 입금 이벤트를 실시간 처리하여 게임 캐릭터에 코인을 지급합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsDepositConsumer {

    private final StringRedisTemplate redisTemplate;
    private final CharacterCommandService characterCommandService;
    private final CharacterRepository characterRepository;
    private final StreamConsumerProperties streamProperties;

    @PostConstruct
    public void startConsuming() {
        String streamKey = streamProperties.getKey();
        String group = streamProperties.getGroup();

        ensureStreamExists(streamKey);
        createConsumerGroupSafely(streamKey, group);

        Thread.ofVirtual().start(this::consume);
        log.info("Redis Stream Consumer started: streamKey={}, group={}, consumer={}",
            streamKey, group, streamProperties.getConsumerName());
    }

    private void ensureStreamExists(String streamKey) {
        try {
            redisTemplate.opsForStream().size(streamKey);
        } catch (Exception e) {
            try {
                redisTemplate.opsForStream().add(streamKey, Map.of("dummy", "init"));
                log.info("더미 메시지로 스트림 생성: {}", streamKey);
            } catch (Exception ex) {
                log.warn("스트림 생성 실패: {}", ex.getMessage());
            }
        }
    }

    private void createConsumerGroupSafely(String streamKey, String group) {
        try {
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), group);
            log.info("Consumer group created: {}", group);
        } catch (Exception e) {
            log.debug("Consumer group already exists or creation failed: {}", e.getMessage());
        }
    }

    private void consume() {
        String streamKey = streamProperties.getKey();
        String group = streamProperties.getGroup();
        String consumer = streamProperties.getConsumerName();
        Integer batchSize = streamProperties.getMaxReadCount() != null ? streamProperties.getMaxReadCount() : 10;

        log.info("배치 소비 시작: batchSize={}", batchSize);

        while (true) {
            try {
                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(Consumer.from(group, consumer),
                          StreamReadOptions.empty()
                              .count(batchSize)
                              .block(Duration.ofSeconds(2)),
                          StreamOffset.create(streamKey, ReadOffset.lastConsumed()));

                if (records != null && !records.isEmpty()) {
                    log.debug("배치 처리 시작: {} 건", records.size());

                    for (MapRecord<String, Object, Object> record : records) {
                        processRecord(record);
                    }

                    String[] recordIds = records.stream()
                        .map(MapRecord::getId)
                        .map(Object::toString)
                        .toArray(String[]::new);

                    redisTemplate.opsForStream().acknowledge(streamKey, group, recordIds);
                    log.debug("배치 ACK 완료: {} 건", recordIds.length);
                }
            } catch (Exception e) {
                log.error("Redis stream error: {}", e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void processRecord(MapRecord<String, Object, Object> record) {
        try {
            Map<Object, Object> body = record.getValue();
            String customerIdStr = (String) body.get("customerId");
            String amountStr = (String) body.get("amount");

            if (customerIdStr == null || amountStr == null) {
                log.warn("Required data missing: customerId={}, amount={}", customerIdStr, amountStr);
                return;
            }

            Long customerId = Long.parseLong(customerIdStr);
            Integer amount = Integer.parseInt(amountStr) / 100;

            Character character = characterRepository
                .findActiveCharacterByCustomerId(CustomerId.of(customerId))
                .orElse(null);

            if (character != null) {
                AddCoinsCommand command = AddCoinsCommand.coin(character.getCharacterId(), amount);
                characterCommandService.addCoins(command);
                log.info("적금 입금 코인 지급 완료: customerId={}, amount={}", customerId, amount);
            } else {
                log.info("활성 캐릭터를 찾을 수 없음: customerId={}", customerId);
            }
        } catch (Exception e) {
            log.error("Process record error: {}", e.getMessage());
        }
    }
}