package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_profile")
public class Profile extends UpdateControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profile")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_application", nullable = false)
    private Application application;

    @Column(name = "ds_name", nullable = false)
    private String name;

    @Column(name = "ds_group", nullable = false)
    private String group;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProfileResourceAction> resourceActions = new HashSet<>();

    public Long getId() { return id; }
    public Application getApplication() { return application; }
    public String getName() { return name; }
    public String getGroup() { return group; }
    public Set<ProfileResourceAction> getResourceActions() { return resourceActions; }

    public void setApplication(Application application) { this.application = application; }
    public void setName(String name) { this.name = name; }
    public void setGroup(String group) { this.group = group; }
    public void setResourceActions(Set<ProfileResourceAction> resourceActions) { this.resourceActions = resourceActions; }

    public Profile(Application application, String name, String group) {
        this.application = application;
        this.name = setString(name);
        this.group = setString(group, false);
    }

    public Profile(Long id) { this.id = id; }

    public Profile() { }

    @Override
    protected void normalizeFields() {
        this.name = setString(name);
        this.group = setString(group, false);
    }
}
