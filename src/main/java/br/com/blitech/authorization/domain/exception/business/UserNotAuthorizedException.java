package br.com.blitech.authorization.domain.exception.business;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.BusinessException;

public class UserNotAuthorizedException extends BusinessException {
    public UserNotAuthorizedException() {
        super(Messages.get("user.invalid-password-exception"));
    }
}
