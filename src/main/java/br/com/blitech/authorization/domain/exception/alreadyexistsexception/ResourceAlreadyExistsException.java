package br.com.blitech.authorization.domain.exception.alreadyexistsexception;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityAlreadyExistsException;

public class ResourceAlreadyExistsException extends EntityAlreadyExistsException {
    public ResourceAlreadyExistsException() { super(Messages.get("resource.already-exists-exception")); }
}
