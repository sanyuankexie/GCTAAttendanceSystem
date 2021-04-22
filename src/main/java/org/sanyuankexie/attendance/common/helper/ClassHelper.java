package org.sanyuankexie.attendance.common.helper;

import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class ClassHelper {
    public static Field getObjectField(Class<T> klass, String fieldName) throws NoSuchFieldException {
        Field[] fields = klass.getDeclaredFields();
        for (Field self : fields) {
            if (self.getName().equals(fieldName)) return self;
        }
        if (klass.getSuperclass() == null) throw new NoSuchFieldException("no such field");
        else {
            return getObjectField((Class<T>) klass.getSuperclass(), fieldName);
        }
    }

    public static Field getObjectField(Object any, String fieldName) throws NoSuchFieldException {
        return getObjectField((Class<T>) any.getClass(), fieldName);
    }

    public static Object getObjectFieldValue(Object any, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = getObjectField((Class<T>) any.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(any);
    }

    public static void setObjectFieldValue(Object any, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        Field field = getObjectField((Class<T>) any.getClass(), fieldName);
        field.setAccessible(true);
        field.set(any, value);
    }
}
