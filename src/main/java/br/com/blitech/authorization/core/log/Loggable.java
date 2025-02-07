package br.com.blitech.authorization.core.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Loggable {
    private static final int MASK_MAX_LENGTH = 5;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String toLog() {
        Map<String, Object> propertiesMap = getProperties(this, Loggable::toLogSafely);
        return this.getClass().getSimpleName() + "(" +
            propertiesMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","))
            + ")";
    }

    public String toJsonLog() {
        Map<String, Object> propertiesMap = getProperties(this, Loggable::toJsonLogSafely);
        try {
            return compactJson(objectMapper.writeValueAsString(propertiesMap));
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to JSON log", e);
        }
    }

    @NotNull
    private Map<String, Object> getProperties(@NotNull Object obj, LogFunction logFunction) {
        Map<String, Object> propertiesMap = new HashMap<>();
        Arrays.stream(obj.getClass().getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value instanceof Collection) {
                    propertiesMap.put(field.getName(), ((Collection<?>) value).stream()
                            .map(logFunction::apply).collect(Collectors.toList()));
                } else if (field.isAnnotationPresent(MaskProperty.class)) {
                    MaskProperty maskProperty = field.getAnnotation(MaskProperty.class);
                    propertiesMap.put(field.getName(), applyMask(value.toString(), maskProperty.format()));
                } else {
                    propertiesMap.put(field.getName(), value instanceof OffsetDateTime ? value.toString() : value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to access field " + field.getName(), e);
            }
        });
        return propertiesMap;
    }

    private String applyMask(String value, LogMaskFormat format) {
        if (value == null) return null;
        switch (format) {
            case CPF:
                return maskCPF(value);
            case ADDRESS:
                return maskAfter(value, 5);
            case EMAIL:
                return maskEmail(value);
            case NAME:
                return maskName(value);
            default:
                return maskAll(value);
        }
    }

    @NotNull
    private String maskAll(@NotNull String value) {
        return "*".repeat(Math.min(value.length(), MASK_MAX_LENGTH));
    }

    @NotNull
    private String maskCPF(@NotNull String value) {
        return value.substring(0, 3) + "*".repeat(MASK_MAX_LENGTH) + value.substring(value.length() - 2);
    }

    @NotNull
    @Contract(pure = true)
    private String maskEmail(@NotNull String value) {
        return value.replaceAll("(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?=.*\\.)", "*");
    }

    @NotNull
    private String maskAfter(@NotNull String value, int lastDigit) {
        if (value.length() > lastDigit) {
            int endIndex = Math.min(value.length(), lastDigit + MASK_MAX_LENGTH);
            return value.substring(0, lastDigit) + "*".repeat(endIndex - lastDigit);
        }
        return value;
    }

    private String maskName(@NotNull String value) {
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
                continue;
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

    private static String toLogSafely(Object obj) {
        if (obj instanceof Loggable loggable) {
            return loggable.toLog();
        }
        return obj.toString();
    }

    private static String toJsonLogSafely(Object obj) {
        if (obj instanceof Loggable loggable) {
            return loggable.toJsonLog();
        }
        return obj.toString();
    }

    @FunctionalInterface
    private interface LogFunction {
        String apply(Object obj);
    }

    public enum LogMaskFormat {
        DEFAULT,
        CPF,
        ADDRESS,
        EMAIL,
        NAME
    }

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MaskProperty {
        LogMaskFormat format() default LogMaskFormat.DEFAULT;
    }
}
