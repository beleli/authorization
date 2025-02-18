package br.com.blitech.authorization.domain.exception.entityinuse;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityInUseException;

public class ServiceUserInUseException extends EntityInUseException {
    public ServiceUserInUseException() {
        super(Messages.get("service-user.in-use-exception"));
    }
}
