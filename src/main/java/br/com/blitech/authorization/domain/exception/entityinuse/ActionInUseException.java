package br.com.blitech.authorization.domain.exception.entityinuse;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityInUseException;

public class ActionInUseException extends EntityInUseException {
    public ActionInUseException() {
        super(Messages.get("action.in-use-exception"));
    }
}
