package br.com.blitech.authorization.domain.entity;

public abstract class BaseEntity {

    protected String setString(String value, boolean toLowerCase) {
        if (value == null || value.isBlank()) return null;
        return toLowerCase ? value.toLowerCase() : value;
    }

    protected String setString(String value) {
        return setString(value, true);
    }

    protected void normalizeFields() { }
}
