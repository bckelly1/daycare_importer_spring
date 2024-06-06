package com.brian.daycare_importer_spring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mail", ignoreUnknownFields = false)
public class MailConfig {
    private String host;
    private String username;
    private String password;
    private String daycareLabel;
}
