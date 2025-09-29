package saviing.game.shop.application.dto.query;

import lombok.Builder;

/**
 * 가챠 정보 조회 쿼리 DTO
 *
 * @param onlyActive 활성화된 가챠풀만 조회할지 여부
 */
@Builder
public record GetGachaInfoQuery(
    Boolean onlyActive
) {

    /**
     * 기본값을 적용한 빌더를 생성합니다.
     *
     * @return 기본값이 적용된 쿼리 (활성화된 가챠풀만 조회)
     */
    public static GetGachaInfoQuery activeOnly() {
        return GetGachaInfoQuery.builder()
            .onlyActive(true)
            .build();
    }

    /**
     * 모든 가챠풀을 조회하는 쿼리를 생성합니다.
     *
     * @return 모든 가챠풀 조회 쿼리
     */
    public static GetGachaInfoQuery all() {
        return GetGachaInfoQuery.builder()
            .onlyActive(false)
            .build();
    }

    /**
     * 활성화된 가챠풀만 조회할지 여부를 반환합니다.
     * null인 경우 기본값 true를 반환합니다.
     *
     * @return 활성화된 가챠풀만 조회할지 여부
     */
    public boolean isOnlyActive() {
        return onlyActive == null || onlyActive;
    }
}