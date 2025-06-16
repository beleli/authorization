package br.com.blitech.authorization.core.log;

import br.com.blitech.authorization.api.utlis.ObjectMapperProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Loggable {
    int MASK_MAX_LENGTH = 5;

    default String toLog() {
        return formatLog(this, getProperties(this, item -> toSafely(item, Loggable::toLog)));
    }

    default String toJsonLog() {
        try {
            return compactJson(ObjectMapperProvider.INSTANCE.writeValueAsString(getProperties(this, item -> toSafely(item, Loggable::toJsonLog))));
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to JSON log", e);
        }
    }

    @NotNull
    private Map<String, Object> getProperties(@NotNull Object obj, Function<Object, String> logFunction) {
        Map<String, Object> propertiesMap = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value instanceof Collection<?>) {
                    propertiesMap.put(field.getName(), ((Collection<?>) value).stream()
                        .map(logFunction)
                        .collect(Collectors.toList()));
                } else if (field.isAnnotationPresent(MaskProperty.class)) {
                    MaskProperty maskProperty = field.getAnnotation(MaskProperty.class);
                    propertiesMap.put(field.getName(), applyMask(Objects.toString(value, ""), maskProperty.format()));
                } else {
                    propertiesMap.put(field.getName(), (value instanceof Temporal) ? value.toString() : value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to access field " + field.getName(), e);
            }
        }
        return propertiesMap;
    }

    private static String toSafely(Object obj, Function<Loggable, String> fn) {
        return obj instanceof Loggable loggable ? fn.apply(loggable) : Objects.toString(obj, "null");
    }

    @NotNull
    private static String formatLog(@NotNull Object obj, @NotNull Map<String, Object> properties) {
        return obj.getClass().getSimpleName() + properties.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(", ", "(", ")"));
    }

    private static String applyMask(String value, LogMaskFormat format) {
        if (value == null) return null;
        return switch (format) {
            case CPF -> maskCPF(value);
            case ADDRESS -> maskAfter(value, 5);
            case EMAIL -> maskEmail(value);
            case NAME -> maskName(value);
            default -> maskAll(value);
        };
    }

    @NotNull
    private static String maskAll(@NotNull String value) {
        return "*".repeat(Math.min(value.length(), MASK_MAX_LENGTH));
    }

    @NotNull
    private static String maskCPF(@NotNull String value) {
        return value.substring(0, 3) + "*".repeat(MASK_MAX_LENGTH) + value.substring(value.length() - 2);
    }

    @NotNull
    @Contract(pure = true)
    private static String maskEmail(@NotNull String value) {
        return value.replaceAll("(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?=.*\\.)", "*");
    }

    @NotNull
    private static String maskAfter(@NotNull String value, int lastDigit) {
        int endIndex = Math.min(value.length(), lastDigit + MASK_MAX_LENGTH);
        return value.substring(0, lastDigit) + "*".repeat(endIndex - lastDigit);
    }

    private static String maskName(@NotNull String value) {
        return Arrays.stream(value.split(" "))
            .map(part -> maskAfter(part, 2))
            .collect(Collectors.joining(" "));
    }

    @NotNull
    private String compactJson(@NotNull String json) {
        StringBuilder result = new StringBuilder(json.length());
        for (int i = 0; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (ch == '\u0000' || ch == '\\') {
                //just ignore ch
            } else if (ch == '"' && i < json.length() - 1 && json.charAt(i + 1) == '{') {
                result.append('{');
                i++;
            } else if (ch == '}' && i < json.length() - 1 && json.charAt(i + 1) == '"') {
                result.append('}');
                i++;
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    enum LogMaskFormat {
        DEFAULT, CPF, ADDRESS, EMAIL, NAME
    }

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface MaskProperty {
        LogMaskFormat format() default LogMaskFormat.DEFAULT;
    }
}