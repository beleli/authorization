package br.com.blitech.authorization.domain.exception.entitynotfound;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;

public class ActionNotFoundException extends EntityNotFoundException {
    public ActionNotFoundException() {
        super(Messages.get("action.not-found-exception"));
    }
}
