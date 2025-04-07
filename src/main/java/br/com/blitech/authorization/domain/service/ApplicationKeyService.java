package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ApplicationKey;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationKeyNotFoundException;
import br.com.blitech.authorization.domain.repository.ApplicationKeyRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class ApplicationKeyService {
    private final ApplicationKeyRepository applicationKeyRepository;

    @Autowired
    public ApplicationKeyService(ApplicationKeyRepository applicationKeyRepository) {
        this.applicationKeyRepository = applicationKeyRepository;
    }

    @Transactional
    public ApplicationKey save(@NotNull Application application) throws NoSuchAlgorithmException {
        var keyId = applicationKeyRepository.countByApplicationId(application.getId()) + 1;
        KeyPair keyPair = generateKeyPair();
        byte[] privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()).getBytes();
        byte[] publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()).getBytes();
        return applicationKeyRepository.save(new ApplicationKey(application, keyId, privateKey, publicKey));
    }

    @Transactional(readOnly = true)
    public boolean hasKey(Long applicationId) {
        return applicationKeyRepository.countByApplicationId(applicationId) > 0;
    }

    @Transactional(readOnly = true)
    public Key getLastPrivateKey(@NotNull Application application) throws NoSuchAlgorithmException, InvalidKeySpecException, ApplicationKeyNotFoundException {
        var keys = applicationKeyRepository.findByApplicationIdOrderByIdDesc(application.getId());
        if (keys.isEmpty()) throw new ApplicationKeyNotFoundException();

        KeyFactory keyFactory = KeyFactory.getInstance(SignatureAlgorithm.RS256.getFamilyName());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keys.get(0).getPrivateKey()));

        return keyFactory.generatePrivate(privateKeySpec);
    }

    @Transactional(readOnly = true)
    public Pair<Long, Key> getLastPublicKey(@NotNull Application application) throws NoSuchAlgorithmException, InvalidKeySpecException, ApplicationKeyNotFoundException {
        var keys = applicationKeyRepository.findByApplicationIdOrderByIdDesc(application.getId());
        if (keys.isEmpty()) throw new ApplicationKeyNotFoundException();

        KeyFactory keyFactory = KeyFactory.getInstance(SignatureAlgorithm.RS256.getFamilyName());
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keys.get(0).getPublicKey()));

        return Pair.of(keys.get(0).getKeyId(), keyFactory.generatePublic(publicKeySpec));
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SignatureAlgorithm.RS256.getFamilyName());
        keyPairGenerator.initialize(SignatureAlgorithm.RS256.getMinKeyLength());
        return keyPairGenerator.generateKeyPair();
    }

    @Transactional
    public void deleteKeys(@NotNull Application application) {
        var keys = applicationKeyRepository.findByApplicationIdOrderByIdDesc(application.getId());
        for (var key : keys) {
            applicationKeyRepository.delete(key);
        }
    }
}
