package com.dsvn.starterkit.infrastructure.security;

import static org.springframework.security.access.annotation.Jsr250SecurityConfig.DENY_ALL_ATTRIBUTE;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

public class CustomPermissionAllowedMethodSecurityMetadataSource
        extends AbstractFallbackMethodSecurityMetadataSource {

    final String CONTROLLER_PACKAGE = "com.dsvn.starterkit.api.controllers";

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return null;
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        List<ConfigAttribute> attributes = new ArrayList<>();

        // if the class is annotated as @Controller we should by default deny access to all methods
        if (AnnotationUtils.findAnnotation(targetClass, Controller.class) != null
                && targetClass.getPackageName().startsWith(CONTROLLER_PACKAGE)) {
            attributes.add(DENY_ALL_ATTRIBUTE);
        }

        if (AnnotationUtils.findAnnotation(method, PreAuthorize.class) != null
                || AnnotationUtils.findAnnotation(method, PostAuthorize.class) != null) {
            return null;
        }

        return attributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }
}
