package br.com.blitech.authorization.domain.exception.entityinuse;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityInUseException;

public class ProfileInUseException extends EntityInUseException {
    public ProfileInUseException() {
        super(Messages.get("profile.in-use-exception"));
    }
}
