package br.com.blitech.authorization.domain.exception.alreadyexistsexception;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityAlreadyExistsException;

public class ServiceUserAlreadyExistsException extends EntityAlreadyExistsException {
    public ServiceUserAlreadyExistsException() { super(Messages.get("service-user.already-exists-exception")); }
}

