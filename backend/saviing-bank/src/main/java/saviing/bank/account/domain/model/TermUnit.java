package saviing.bank.account.domain.model;

/**
 * 금융상품의 기간 단위를 나타내는 열거형
 *
 * 예금, 적금 등의 상품에서 사용되는 기간 단위를 정의하며,
 * 각 단위를 일(day) 단위로 환산하는 기능을 제공합니다.
 * 실제 은행과 같은 다양한 기간 표현(26주, 12개월 등)을 지원합니다.
 */
public enum TermUnit {
    /** 일 단위 (1일 = 1일) */
    DAYS("일", 1),

    /** 주 단위 (1주 = 7일) */
    WEEKS("주", 7),

    /** 월 단위 (1월 = 30일, 평균 계산) */
    MONTHS("월", 30),

    /** 년 단위 (1년 = 365일) */
    YEARS("년", 365);

    /** 단위의 한글 표시명 */
    private final String description;

    /** 일(day) 단위로 환산하기 위한 곱셈 계수 */
    private final int daysMultiplier;

    /**
     * TermUnit 생성자
     * @param description 단위의 한글 표시명
     * @param daysMultiplier 일 단위 환산 계수
     */
    TermUnit(String description, int daysMultiplier) {
        this.description = description;
        this.daysMultiplier = daysMultiplier;
    }

    /**
     * 단위의 한글 표시명을 반환
     * @return 한글 단위명 (예: "주", "월", "년")
     */
    public String getDescription() {
        return description;
    }

    /**
     * 일 단위 환산 계수를 반환
     * @return 일 단위로 변환할 때 사용하는 곱셈 계수
     */
    public int getDaysMultiplier() {
        return daysMultiplier;
    }

    /**
     * 주어진 값을 일 단위로 변환
     * @param value 변환할 기간 값 (예: 2주의 경우 2)
     * @return 일 단위로 변환된 값 (예: 2주 = 14일)
     */
    public int toDays(int value) {
        return value * daysMultiplier;
    }
}