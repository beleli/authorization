package br.com.blitech.authorization.domain.exception;

public class EntityInUseException extends BusinessException {
    public EntityInUseException(String message) {
        super(message);
    }
}
