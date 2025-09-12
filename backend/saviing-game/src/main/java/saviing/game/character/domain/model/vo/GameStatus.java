package saviing.game.character.domain.model.vo;

/**
 * 게임 내 캐릭터의 상태 정보를 관리하는 Value Object
 * 코인, 피쉬 코인, 방 수, 활성화 상태를 포함합니다.
 */
public record GameStatus(
    Integer coin,
    Integer fishCoin,
    Integer roomCount,
    Boolean isActive
) {
    public GameStatus {
        if (coin == null || coin < 0) {
            throw new IllegalArgumentException("코인은 음수일 수 없습니다");
        }
        if (fishCoin == null || fishCoin < 0) {
            throw new IllegalArgumentException("피쉬 코인은 음수일 수 없습니다");
        }
        if (roomCount == null || roomCount < 0) {
            throw new IllegalArgumentException("방 수는 음수일 수 없습니다");
        }
        if (isActive == null) {
            throw new IllegalArgumentException("활성 상태는 null일 수 없습니다");
        }
    }

    /**
     * 초기 게임 상태를 생성합니다.
     * 모든 값이 0이고 활성 상태로 설정됩니다.
     * 
     * @return 초기화된 GameStatus
     */
    public static GameStatus initialize() {
        return new GameStatus(0, 0, 0, true);
    }

    /**
     * 코인을 추가합니다.
     * 
     * @param amount 추가할 코인 수량 (양수)
     * @return 코인이 추가된 새로운 GameStatus
     * @throws IllegalArgumentException amount가 음수인 경우
     */
    public GameStatus addCoin(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("코인 수량은 양수여야 합니다");
        }
        return new GameStatus(coin + amount, fishCoin, roomCount, isActive);
    }

    /**
     * 피쉬 코인을 추가합니다.
     * 
     * @param amount 추가할 피쉬 코인 수량 (양수)
     * @return 피쉬 코인이 추가된 새로운 GameStatus
     * @throws IllegalArgumentException amount가 음수인 경우
     */
    public GameStatus addFishCoin(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("피쉬 코인 수량은 양수여야 합니다");
        }
        return new GameStatus(coin, fishCoin + amount, roomCount, isActive);
    }

    /**
     * 방 수를 1 증가시킵니다.
     * 
     * @return 방 수가 1 증가된 새로운 GameStatus
     */
    public GameStatus increaseRoomCount() {
        return new GameStatus(coin, fishCoin, roomCount + 1, isActive);
    }

    /**
     * 캐릭터를 활성화합니다.
     * 
     * @return 활성화된 새로운 GameStatus
     */
    public GameStatus activate() {
        return new GameStatus(coin, fishCoin, roomCount, true);
    }

    /**
     * 캐릭터를 비활성화합니다.
     * 
     * @return 비활성화된 새로운 GameStatus
     */
    public GameStatus deactivate() {
        return new GameStatus(coin, fishCoin, roomCount, false);
    }
}