package br.com.blitech.authorization.api;

import br.com.blitech.authorization.api.v1.model.*;
import br.com.blitech.authorization.api.v1.model.input.*;
import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hibernate.internal.util.collections.CollectionHelper.listOf;
import static org.springframework.boot.context.properties.bind.Bindable.setOf;

public class TestUtlis {

    @NotNull
    public static ActionModel createActionModel() {
        return new ActionModel(1L, "action");
    }

    @NotNull
    public static ActionInputModel createActionInputModel() {
        return new ActionInputModel("action");
    }

    @NotNull
    public static ResourceModel createResourceModel() {
        return new ResourceModel(1L, "resource");
    }

    @NotNull
    public static ResourceInputModel createResourceInputModel() {
        return new ResourceInputModel("resource");
    }

    @NotNull
    public static ApplicationModel createApplicationModel() {
        return new ApplicationModel(1L, "application", "user");
    }

    @NotNull
    public static ApplicationInputModel createApplicationInputModel() {
        return new ApplicationInputModel("application", "token", "user", "password");
    }

    @NotNull
    public static LoginUserInputModel createLoginUserInputModel() {
        return new LoginUserInputModel("user", "password", "application");
    }

    @NotNull
    public static LoginApplicationInputModel createLoginApplicationInputModel() {
        return new LoginApplicationInputModel("user", "password");
    }

    @NotNull
    public static ResourceActionModel createResourceActionModel() {
        return new ResourceActionModel(1L, "resource", Set.of(createActionModel()));
    }

    @NotNull
    public static ProfileModel createProfileModel() {
        return new ProfileModel(1L, "profile", Set.of(createResourceActionModel()));
    }

    @NotNull
    public static ResourceActionInputModel createResourceActionInputModel() {
        return new ResourceActionInputModel(1L, listOf(1L));
    }

    @NotNull
    public static ProfileInputModel createProfileInputModel() {
        return new ProfileInputModel("profile", "group", listOf(createResourceActionInputModel()));
    }

    @NotNull
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
