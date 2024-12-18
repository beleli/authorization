package br.com.blitech.authorization.domain.exception;

public class EntityAlreadyExistsException extends BusinessException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
