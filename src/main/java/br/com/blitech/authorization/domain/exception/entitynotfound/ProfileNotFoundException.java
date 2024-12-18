package br.com.blitech.authorization.domain.exception.entitynotfound;

import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;

public class ProfileNotFoundException extends EntityNotFoundException {
    public ProfileNotFoundException() {
        super(Messages.get("profile.not-found-exception"));
    }
}

