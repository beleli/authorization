package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;

import java.util.List;

public class ProfileModel extends Loggable {
    private Long profileId;
    private String name;
    private List<ResourceActionModel> resources;

    public String getName() { return name; }
    public Long getProfileId() { return profileId; }
    public List<ResourceActionModel> getResourceActions() { return resources; }

    public ProfileModel(Long profileId, String name, List<ResourceActionModel> resources) {
        this.profileId = profileId;
        this.name = name;
        this.resources = resources;
    }
}
