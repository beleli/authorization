package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;

import java.util.Set;

public class ProfileModel extends Loggable {
    private Long profileId;
    private String name;
    private Set<ResourceActionModel> resources;

    public String getName() { return name; }
    public Long getProfileId() { return profileId; }
    public Set<ResourceActionModel> getResourceActions() { return resources; }

    public ProfileModel(Long profileId, String name, Set<ResourceActionModel> resources) {
        this.profileId = profileId;
        this.name = name;
        this.resources = resources;
    }
}
