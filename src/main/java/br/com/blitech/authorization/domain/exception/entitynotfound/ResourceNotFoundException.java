package br.com.blitech.authorization.domain.exception.entitynotfound;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;

public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException() {
        super(Messages.get("resource.not-found-exception"));
    }
}
