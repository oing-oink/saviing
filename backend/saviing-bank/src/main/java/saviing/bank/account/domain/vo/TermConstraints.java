package saviing.bank.account.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Collections;
import java.util.Set;

import saviing.bank.account.domain.model.TermUnit;

/**
 * 금융상품의 기간 제약 조건을 관리하는 값 객체
 *
 * 실제 은행 시스템과 같은 유연한 기간 제약을 지원합니다.
 * Builder 패턴을 사용하여 다양한 제약 조건을 조합할 수 있습니다.
 *
 * 지원하는 패턴:
 * 1. 특정 기간만 허용 (예: 1주, 2주, 4주, 8주, 12주, 26주, 52주만)
 * 2. 범위 + 증가 단위 (예: 1개월~60개월, 1개월 단위)
 * 3. 혼합 사용 (주 단위와 월 단위 함께 사용)
 *
 * @see TermPeriod 기간을 나타내는 값 객체
 * @see TermUnit 기간 단위 열거형
 */
@Getter
@Builder
public class TermConstraints {

    /** 허용되는 특정 기간들의 집합 (빈 집합이면 범위 기반 검증 사용) */
    @Singular
    private final Set<TermPeriod> allowedTerms;

    /** 최소 기간 (범위 기반 검증에서 사용) */
    private final TermPeriod minTerm;

    /** 최대 기간 (범위 기반 검증에서 사용) */
    private final TermPeriod maxTerm;

    /** 증가 단위 (예: MONTHS - 월 단위로만 증가 허용) */
    private final TermUnit stepUnit;

    /** 증가 값 (예: 1 - 1개월씩 증가, 2 - 2개월씩 증가) */
    private final int stepValue;

    /**
     * 주어진 기간이 제약 조건에 맞는지 검증
     * @param term 검증할 기간
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidTerm(@NonNull TermPeriod term) {
        // 1. 특정 기간만 허용하는 경우 (allowedTerms가 비어있지 않음)
        if (!allowedTerms.isEmpty()) {
            return allowedTerms.contains(term);
        }

        // 2. 범위 기반 검증
        // 최소 기간 체크
        if (minTerm != null && term.isShorterThan(minTerm)) {
            return false;
        }

        // 최대 기간 체크
        if (maxTerm != null && term.isLongerThan(maxTerm)) {
            return false;
        }

        // 증가 단위 체크 (stepUnit과 stepValue가 모두 설정된 경우)
        if (stepUnit != null && stepValue > 0) {
            return isValidStep(term);
        }

        return true;
    }

    /**
     * 기간이 증가 단위 규칙에 맞는지 검증
     * 예: 1개월부터 1개월씩 증가하는 경우, 3개월, 5개월은 유효하지만 3.5개월은 무효
     */
    private boolean isValidStep(TermPeriod term) {
        // 단위가 다르면 무효
        if (term.unit() != stepUnit) {
            return false;
        }

        // 최소값 기준으로 증가 단위 체크
        int minValue = minTerm != null && minTerm.unit() == stepUnit ? minTerm.value() : stepValue;
        return (term.value() - minValue) % stepValue == 0;
    }

    /**
     * 허용되는 기간들의 불변 집합을 반환
     * @return 허용되는 기간들의 읽기 전용 집합
     */
    public Set<TermPeriod> getAllowedTerms() {
        return Collections.unmodifiableSet(allowedTerms);
    }

    /**
     * 특정 기간들만 허용하는 제약 조건 생성
     * 예: 1주, 2주, 4주, 8주, 12주, 26주, 52주만 허용
     * @param terms 허용할 기간들의 집합
     * @return TermConstraints 인스턴스
     */
    public static TermConstraints specificTerms(Set<TermPeriod> terms) {
        return TermConstraints.builder()
            .allowedTerms(terms)
            .build();
    }

    /**
     * 범위와 증가 단위로 제약 조건 생성
     * 예: 1개월~24개월, 1개월 단위
     * @param min 최소 기간
     * @param max 최대 기간
     * @param stepUnit 증가 단위
     * @param stepValue 증가 값
     * @return TermConstraints 인스턴스
     */
    public static TermConstraints range(TermPeriod min, TermPeriod max, TermUnit stepUnit, int stepValue) {
        return TermConstraints.builder()
            .minTerm(min)
            .maxTerm(max)
            .stepUnit(stepUnit)
            .stepValue(stepValue)
            .build();
    }
}