package br.com.blitech.authorization.domain;

import br.com.blitech.authorization.domain.entity.*;
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
        return new Application("application", "user", "password");
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

    @NotNull
    public static ServiceUser createServiceUser() {
        return new ServiceUser(createApplication(), createProfile(), "user", "password123");
    }
}
