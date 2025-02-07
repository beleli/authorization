package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public class ProfileModel implements Loggable {

    @Schema(description = "Profile id", example = "1")
    private Long profileId;

    @Schema(description = "Profile name", example = "ADMIN")
    private String name;

    @Schema(description = "Profile resources")
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
