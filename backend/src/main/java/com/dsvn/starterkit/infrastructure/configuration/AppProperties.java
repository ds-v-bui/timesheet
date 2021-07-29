package com.dsvn.starterkit.infrastructure.configuration;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Cors cors = new Cors();
    private final Auth auth = new Auth();
    private String domainPattern;
    private String timeZone;
    private String frontEndUrl;
    private int passwordResetTokenExpirationSec;

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowOrigin = new ArrayList<>();
        private Long maxAge;
    }

    @Getter
    @Setter
    public static class Auth {
        private String tokenSecret;
        private int tokenExpirationSec;
    }
}
