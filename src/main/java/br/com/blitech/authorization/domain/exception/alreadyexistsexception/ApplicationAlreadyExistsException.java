package br.com.blitech.authorization.domain.exception.alreadyexistsexception;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityAlreadyExistsException;

public class ApplicationAlreadyExistsException extends EntityAlreadyExistsException {
    public ApplicationAlreadyExistsException() { super(Messages.get("application.already-exists-exception")); }
}
