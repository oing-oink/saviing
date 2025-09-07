package saviing.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record InvalidParam(
    String field,
    String message,
    Object rejectedValue
) {
}