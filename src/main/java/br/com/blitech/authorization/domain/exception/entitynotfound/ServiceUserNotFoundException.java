package br.com.blitech.authorization.domain.exception.entitynotfound;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;

public class ServiceUserNotFoundException extends EntityNotFoundException {
    public ServiceUserNotFoundException() {
        super(Messages.get("service-user.not-found-exception"));
    }
}

