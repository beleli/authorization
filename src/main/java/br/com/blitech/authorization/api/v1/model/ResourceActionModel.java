package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;

public class ResourceActionModel implements Loggable {

    @Schema(description = "Resource id", example = "1")
    private Long resourceId;

    @Schema(description = "Resource name", example = "My Resource")
    private String name;

    @Schema(description = "Resource actions")
    private Set<ActionModel> actions;

    public Long getResourceId() { return resourceId; }
    public String getName() { return name; }
    public Set<ActionModel> getActions() { return actions; }

    public ResourceActionModel(Long resourceId, String name, Set<ActionModel> actions) {
        this.resourceId = resourceId;
        this.name = name;
        this.actions = actions;
    }
}
