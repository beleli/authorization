package br.com.blitech.authorization.api.v1.assembler;

import org.springframework.stereotype.Component;

import br.com.blitech.authorization.api.v1.model.ApplicationModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationInputModel;
import br.com.blitech.authorization.domain.entity.Application;
import org.jetbrains.annotations.NotNull;

@Component
public class ApplicationModelAssembler {

    public ApplicationModel toModel(@NotNull Application application) {
        return new ApplicationModel (
            application.getId(),
            application.getName(),
            application.getUser()
        );
    }

    public Application toEntity(@NotNull ApplicationInputModel applicationInputModel) {
        return new Application(
            applicationInputModel.getName(),
            applicationInputModel.getUser(),
            applicationInputModel.getPassword()
        );
    }

    public Application applyModel(@NotNull Application application, @NotNull ApplicationInputModel applicationInputModel) {
        application.setName(applicationInputModel.getName());
        application.setUser(applicationInputModel.getUser());
        application.setPassword(applicationInputModel.getPassword());
        return application;
    }
}
