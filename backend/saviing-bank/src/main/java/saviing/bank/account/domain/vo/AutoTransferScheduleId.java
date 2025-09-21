package saviing.bank.account.domain.vo;

import lombok.NonNull;

/**
 * 자동이체 스케줄 식별자 값 객체.
 */
public record AutoTransferScheduleId(
    @NonNull Long value
) {

    /**
     * 식별자를 생성한다.
     *
     * @param value 식별자 값
     * @return {@link AutoTransferScheduleId}
     */
    public static AutoTransferScheduleId of(Long value) {
        return new AutoTransferScheduleId(value);
    }
}
