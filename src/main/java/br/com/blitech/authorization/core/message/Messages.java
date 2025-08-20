package br.com.blitech.authorization.core.message;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {

    private static final String BUNDLE_BASE_NAME = "i18n/messages";
    private static final ResourceBundle defaultBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME);
    private static final ResourceBundle enBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, new Locale("en"));
    private static final ResourceBundle ptBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, new Locale("pt"));

    private Messages() {
        // prevents instantiation
    }

    public static String get(String key, @NotNull Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle resourceBundle = switch (locale.getLanguage().toLowerCase()) {
            case "pt" -> ptBundle;
            case "en" -> enBundle;
            default -> defaultBundle;
        };
        try {
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
