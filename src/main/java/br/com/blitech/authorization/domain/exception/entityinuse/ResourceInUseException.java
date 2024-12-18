package br.com.blitech.authorization.domain.exception.entityinuse;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityInUseException;

public class ResourceInUseException extends EntityInUseException {
    public ResourceInUseException() {
        super(Messages.get("resource.in-use-exception"));
    }
}
