package br.com.blitech.authorization.domain.exception.entitynotfound;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;

public class ApplicationKeyNotFoundException extends EntityNotFoundException {
    public ApplicationKeyNotFoundException() {
        super(Messages.get("application-key.not-found-exception"));
    }
}
