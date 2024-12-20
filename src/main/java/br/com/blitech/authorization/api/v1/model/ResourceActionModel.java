package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;

import java.util.List;
import java.util.Set;

public class ResourceActionModel extends Loggable {
    private Long resourceId;
    private String name;
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
