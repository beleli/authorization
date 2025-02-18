package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_service_user")
public class ServiceUser extends UpdateControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service_user")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_application", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "id_profile")
    private Profile profile;

    @Column(name = "ds_name", nullable = false, unique = true)
    private String name;

    @Column(name = "ds_password", nullable = false)
    private String password;

    public Long getId() { return id; }
    public Application getApplication() { return application; }
    public Profile getProfile() { return profile; }
    public String getName() { return name; }
    public String getPassword() { return password; }

    public void setApplication(Application application) { this.application = application; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }

    public ServiceUser(Application application, Profile profile, String name, String password) {
        this.application = application;
        this.profile = profile;
        this.name = name;
        this.password = password;
    }

    public ServiceUser() { }

    @Override
    protected void normalizeFields() {
        this.name = setString(name);
    }
}
