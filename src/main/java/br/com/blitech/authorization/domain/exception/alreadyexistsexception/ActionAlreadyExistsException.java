package br.com.blitech.authorization.domain.exception.alreadyexistsexception;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityAlreadyExistsException;

public class ActionAlreadyExistsException extends EntityAlreadyExistsException {
    public ActionAlreadyExistsException() { super(Messages.get("action.already-exists-exception")); }
}
