package br.com.blitech.authorization.core.message;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {

    private Messages() {
        // prevents instantiation
    }

    public static String get(String key, @NotNull Object... args) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/messages", LocaleContextHolder.getLocale());
            String message = resourceBundle.getString(key);
            if (args.length > 0) {
                return MessageFormat.format(message, args);
            }
            return message;
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
