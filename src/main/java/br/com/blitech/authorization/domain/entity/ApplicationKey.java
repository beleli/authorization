package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_application_key")
public class ApplicationKey extends UpdateControl{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_application_key")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_application", nullable = false)
    private Application application;

    @Column(name = "id_key", nullable = false)
    private Long keyId;

    @Column(name = "ds_private_key", nullable = false)
    private byte[] privateKey;

    @Column(name = "ds_public_key", nullable = false)
    private byte[] publicKey;

    public Long getId() { return id; }
    public Application getApplication() { return application; }
    public Long getKeyId() { return keyId; }
    public byte[] getPrivateKey() { return privateKey; }
    public byte[] getPublicKey() {return publicKey; }

    public void setApplication(Application application) { this.application = application; }
    public void setKeyId(Long keyId) { this.keyId = keyId; }
    public void setPrivateKey(byte[] privateKey) { this.privateKey = privateKey; }
    public void setPublicKey(byte[] publicKey) { this.publicKey = publicKey; }

    public ApplicationKey(Application application, Long keyId, byte[] privateKey, byte[] publicKey) {
        this.application = application;
        this.keyId = keyId;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public ApplicationKey() { }
}
