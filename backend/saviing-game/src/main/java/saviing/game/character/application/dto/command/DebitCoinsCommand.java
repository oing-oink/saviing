package saviing.game.character.application.dto.command;

import lombok.Builder;
import saviing.game.character.domain.model.vo.CharacterId;

/**
 * 코인 차감 Command입니다.
 *
 * @param characterId 캐릭터 ID
 * @param coinAmount 차감할 코인 수량
 * @param fishCoinAmount 차감할 피쉬 코인 수량
 */
@Builder
public record DebitCoinsCommand(
    CharacterId characterId,
    Integer coinAmount,
    Integer fishCoinAmount
) {
    public DebitCoinsCommand {
        if (characterId == null) {
            throw new IllegalArgumentException("캐릭터 ID는 필수입니다");
        }

        int finalCoinAmount = coinAmount != null ? coinAmount : 0;
        int finalFishCoinAmount = fishCoinAmount != null ? fishCoinAmount : 0;

        if (finalCoinAmount < 0) {
            throw new IllegalArgumentException("코인 수량은 음수일 수 없습니다");
        }
        if (finalFishCoinAmount < 0) {
            throw new IllegalArgumentException("피쉬 코인 수량은 음수일 수 없습니다");
        }
        if (finalCoinAmount == 0 && finalFishCoinAmount == 0) {
            throw new IllegalArgumentException("코인 또는 피쉬 코인 중 적어도 하나는 양수여야 합니다");
        }
    }

    /**
     * 코인만 차감하는 DebitCoinsCommand를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param coinAmount 차감할 코인 수량
     * @return DebitCoinsCommand 인스턴스
     */
    public static DebitCoinsCommand coin(CharacterId characterId, Integer coinAmount) {
        return DebitCoinsCommand.builder()
            .characterId(characterId)
            .coinAmount(coinAmount)
            .fishCoinAmount(0)
            .build();
    }

    /**
     * 피쉬 코인만 차감하는 DebitCoinsCommand를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param fishCoinAmount 차감할 피쉬 코인 수량
     * @return DebitCoinsCommand 인스턴스
     */
    public static DebitCoinsCommand fishCoin(CharacterId characterId, Integer fishCoinAmount) {
        return DebitCoinsCommand.builder()
            .characterId(characterId)
            .coinAmount(0)
            .fishCoinAmount(fishCoinAmount)
            .build();
    }

    /**
     * 코인과 피쉬 코인을 모두 차감하는 DebitCoinsCommand를 생성합니다.
     *
     * @param characterId 캐릭터 ID
     * @param coinAmount 차감할 코인 수량
     * @param fishCoinAmount 차감할 피쉬 코인 수량
     * @return DebitCoinsCommand 인스턴스
     */
    public static DebitCoinsCommand both(CharacterId characterId, Integer coinAmount, Integer fishCoinAmount) {
        return DebitCoinsCommand.builder()
            .characterId(characterId)
            .coinAmount(coinAmount)
            .fishCoinAmount(fishCoinAmount)
            .build();
    }
}