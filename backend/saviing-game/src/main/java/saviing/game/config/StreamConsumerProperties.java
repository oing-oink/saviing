package saviing.game.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.streams", ignoreUnknownFields = true)
public class StreamConsumerProperties {

    private String key;
    private String group;
    private String consumerName;
    private Long blockMillis;
    private Integer maxReadCount;
}