package com.dsvn.starterkit.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    private final Credentials credentials = new Credentials();
    private final S3 s3 = new S3();
    private String region;

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class S3 {
        private String bucketNamePublic;
        private String bucketNamePublicAccessUrl;
    }
}
