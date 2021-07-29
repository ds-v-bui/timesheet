package com.dsvn.starterkit.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.lang3.reflect.FieldUtils;

public class FieldUtil {
    public static Object getFieldValue(final Field field, final Object target)
            throws IllegalAccessException {
        String getterName =
                "get"
                        + Character.toUpperCase(field.getName().charAt(0))
                        + field.getName().substring(1);
        try {
            Method getterMethod = field.getDeclaringClass().getMethod(getterName);
            return getterMethod.invoke(target);
        } catch (NoSuchMethodException | InvocationTargetException e) {
            return FieldUtils.readField(field, target, true);
        }
    }
}
