package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_profile_resource_action")
public class ProfileResourceAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profile_resource_action")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_profile", nullable = false)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "id_resource", nullable = false)
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "id_action", nullable = false)
    private Action action;

    public Long getId() { return id; }
    public Profile getProfile() { return profile; }
    public Resource getResource() { return resource; }
    public Action getAction() { return action; }

    public String getAuthority() {
        return (resource.getName() + "." + action.getName()).toUpperCase();
    }

    public ProfileResourceAction(Profile profile, Resource resource, Action action) {
        this.profile = profile;
        this.resource = resource;
        this.action = action;
    }

    public ProfileResourceAction() { }
}
