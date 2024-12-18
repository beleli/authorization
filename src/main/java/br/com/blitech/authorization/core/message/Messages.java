package br.com.blitech.authorization.core.message;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/messages");

    public static String get(String key, Object... args) {
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
