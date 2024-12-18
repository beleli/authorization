package br.com.blitech.authorization.domain.exception.entitynotfound;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;

public class ApplicationNotFoundException extends EntityNotFoundException {
    public ApplicationNotFoundException() {
        super(Messages.get("application.not-found-exception"));
    }
}
