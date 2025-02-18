package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_application")
public class Application extends UpdateControl{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_application")
    private Long id;

    @Column(name = "ds_name", nullable = false, unique = true)
    private String name;

    @Column(name = "ds_user", nullable = false, unique = true)
    private String user;

    @Column(name = "ds_password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "application")
    private Set<Profile> profiles = new HashSet<>();

    @OneToMany(mappedBy = "application")
    private Set<ServiceUser> serviceUsers = new HashSet<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public Set<Profile> getProfiles() { return profiles; }

    public void setName(String name) { this.name = name; }
    public void setUser(String user) { this.user = user; }
    public void setPassword(String password) { this.password = password; }
    public void setProfiles(Set<Profile> profiles) { this.profiles = profiles; }
    public void setServiceUsers(Set<ServiceUser> serviceUsers) { this.serviceUsers = serviceUsers; }

    public Application(String name, String user, String password) {
        this.name = name;
        this.user = user;
        this.password = password;
    }

    public Application(Long id) { this.id = id; }

    public Application() { }

    @Override
    protected void prePersist() {
        normalizeFields();
        super.prePersist();
    }

    @Override
    protected void preUpdate() {
        normalizeFields();
        super.preUpdate();
    }

    private void normalizeFields() {
        this.name = setString(name);
        this.user = setString(user);
    }
}
