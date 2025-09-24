package saviing.bank.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.streams", ignoreUnknownFields = true)
public class StreamProperties {

    private String key;
    private String group;
    private String consumerName;
    private Long blockMillis;
    private Integer maxReadCount;
    private Long maxlenApprox;
}
