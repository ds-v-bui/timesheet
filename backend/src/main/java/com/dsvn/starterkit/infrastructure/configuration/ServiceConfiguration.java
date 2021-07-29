package com.dsvn.starterkit.infrastructure.configuration;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AppProperties.class, AwsProperties.class})
public class ServiceConfiguration {

    @Autowired private AppProperties appProperties;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(appProperties.getTimeZone()));
    }
}
