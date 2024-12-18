package br.com.blitech.authorization.domain.exception.entityinuse;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityInUseException;

public class ApplicationInUseException extends EntityInUseException {
    public ApplicationInUseException() {
        super(Messages.get("application.in-use-exception"));
    }
}
