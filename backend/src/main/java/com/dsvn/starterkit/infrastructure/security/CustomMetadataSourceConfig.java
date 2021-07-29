package com.dsvn.starterkit.infrastructure.security;

import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class CustomMetadataSourceConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new CustomPermissionAllowedMethodSecurityMetadataSource();
    }
}
