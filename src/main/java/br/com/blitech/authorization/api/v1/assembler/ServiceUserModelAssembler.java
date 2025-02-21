package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ServiceUserModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserInputModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserPasswordInputModel;
import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.entity.ServiceUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ServiceUserModelAssembler {

    public ServiceUserModel toModel(@NotNull ServiceUser serviceUser) {
        return new ServiceUserModel (
            serviceUser.getId(),
            serviceUser.getName(),
            serviceUser.getProfile().getName()
        );
    }

    public ServiceUser toEntity(Long applicationId, @NotNull ServiceUserPasswordInputModel serviceUserPasswordInputModel) {
        return new ServiceUser(
            new Application(applicationId),
            getProfile(serviceUserPasswordInputModel.getProfileId()),
            serviceUserPasswordInputModel.getName(),
            serviceUserPasswordInputModel.getPassword()
        );
    }

    public ServiceUser applyModel(@NotNull ServiceUser serviceUser, @NotNull ServiceUserInputModel serviceUserInputModel) {
        serviceUser.setName(serviceUserInputModel.getName());
        serviceUser.setProfile(getProfile(serviceUserInputModel.getProfileId()));
        return serviceUser;
    }

    private Profile getProfile(Long profileId) {
        if (profileId != null) {
            return new Profile(profileId);
        }
        return null;
    }
}
