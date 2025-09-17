package saviing.bank.account.api.response;

/**
 * Account 내부 API 호출 결과.
 * 성공/실패만 구분하는 단순한 구조.
 *
 * @param <T> 성공 시 반환되는 데이터 타입
 */
public sealed interface AccountApiResponse<T>
    permits AccountApiResponse.Success, AccountApiResponse.Failure {

    /**
     * 성공 결과
     */
    record Success<T>(T data) implements AccountApiResponse<T> {
        public static <T> Success<T> of(T data) {
            return new Success<>(data);
        }
    }

    /**
     * 실패 결과
     */
    record Failure<T>(String message) implements AccountApiResponse<T> {
        public static <T> Failure<T> of(String message) {
            return new Failure<>(message);
        }
    }

    /**
     * 성공 여부를 확인합니다.
     */
    default boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * 실패 여부를 확인합니다.
     */
    default boolean isFailure() {
        return this instanceof Failure;
    }
}