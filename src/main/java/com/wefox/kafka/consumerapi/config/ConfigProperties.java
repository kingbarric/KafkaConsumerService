package com.wefox.kafka.consumerapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("props.system")
@Configuration
@Data
public class ConfigProperties {
    private String onlineTopic;
    private String offlineTopic;
    private String groupId;
    
    private Integer timeOut;
    private String confirmPaymentUrl;
    private String logErrorUrl;
    private String bootstrapServers;

}
