package br.com.blitech.authorization.api.utlis;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperProvider {

    public static final ObjectMapper INSTANCE = new ObjectMapper();

    private ObjectMapperProvider() {
        // prevents instantiation
    }
}
