package br.com.blitech.authorization.domain.exception.business;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.BusinessException;

public class UserInvalidPasswordException extends BusinessException {
    public UserInvalidPasswordException() {
        super(Messages.get("user.invalid-password-exception"));
    }
}
