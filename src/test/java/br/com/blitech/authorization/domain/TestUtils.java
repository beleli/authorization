package br.com.blitech.authorization.domain;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.entity.Resource;
import br.com.blitech.authorization.domain.entity.Action;
import org.jetbrains.annotations.NotNull;

public class TestUtils {

    @NotNull
    public static Action createAction() {
        return new Action("action");
    }

    @NotNull
    public static Resource createResource() {
        return new Resource("resource");
    }

    @NotNull
    public static Application createApplication() {
        return new Application("application", "token", "user", "password");
    }

    @NotNull
    public static Application createExistentApplication() {
        return new Application(1L);
    }


    @NotNull
    public static Profile createProfile() {
        return new Profile(createApplication(), "profile","group");
    }

    @NotNull
    public static ProfileResourceAction createProfileResourceAction() {
        return new ProfileResourceAction(createProfile(), createResource(), createAction());
    }
}
