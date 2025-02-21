package br.com.blitech.authorization.domain;

import br.com.blitech.authorization.domain.entity.*;
import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
        return new Application("application", "user", "password", false);
    }

    @NotNull
    public static Application createExistentApplication() {
        return new Application(1L);
    }

    @NotNull
    public static ApplicationKey createApplicationKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = createKeyPair();
        byte[] privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()).getBytes();
        byte[] publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()).getBytes();
        return new ApplicationKey(createApplication(), 1L, privateKey, publicKey);
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

    @NotNull
    static public KeyPair createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
