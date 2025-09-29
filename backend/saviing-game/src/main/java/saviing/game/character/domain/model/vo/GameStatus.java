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

    /**
     * 코인을 차감합니다.
     *
     * @param amount 차감할 코인 수량 (양수)
     * @return 코인이 차감된 새로운 GameStatus
     * @throws IllegalArgumentException amount가 음수이거나 잔액이 부족한 경우
     */
    public GameStatus debitCoin(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감할 코인 수량은 양수여야 합니다");
        }
        if (coin < amount) {
            throw new IllegalArgumentException("코인 잔액이 부족합니다. 현재: " + coin + ", 필요: " + amount);
        }
        return new GameStatus(coin - amount, fishCoin, roomCount, isActive);
    }

    /**
     * 피쉬 코인을 차감합니다.
     *
     * @param amount 차감할 피쉬 코인 수량 (양수)
     * @return 피쉬 코인이 차감된 새로운 GameStatus
     * @throws IllegalArgumentException amount가 음수이거나 잔액이 부족한 경우
     */
    public GameStatus debitFishCoin(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감할 피쉬 코인 수량은 양수여야 합니다");
        }
        if (fishCoin < amount) {
            throw new IllegalArgumentException("피쉬 코인 잔액이 부족합니다. 현재: " + fishCoin + ", 필요: " + amount);
        }
        return new GameStatus(coin, fishCoin - amount, roomCount, isActive);
    }

    /**
     * 잔액이 충분한지 확인합니다.
     *
     * @param coinAmount 필요한 코인 수량
     * @param fishCoinAmount 필요한 피쉬 코인 수량
     * @return 잔액 충분 여부
     */
    public boolean hasSufficientFunds(Integer coinAmount, Integer fishCoinAmount) {
        return (coinAmount == null || coinAmount <= 0 || coin >= coinAmount) &&
               (fishCoinAmount == null || fishCoinAmount <= 0 || fishCoin >= fishCoinAmount);
    }
}