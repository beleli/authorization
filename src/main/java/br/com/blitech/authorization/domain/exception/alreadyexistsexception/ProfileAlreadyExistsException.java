package br.com.blitech.authorization.domain.exception.alreadyexistsexception;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityAlreadyExistsException;

public class ProfileAlreadyExistsException extends EntityAlreadyExistsException {
    public ProfileAlreadyExistsException() { super(Messages.get("profile.already-exists-exception")); }
}
