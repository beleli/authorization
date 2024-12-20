package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;

import java.util.List;

public class ResourceActionInputModel extends Loggable {
    private Long resourceId;
    private List<Long> actions;

    public Long getResourceId() { return resourceId; }
    public List<Long> getActions() { return actions; }

    public ResourceActionInputModel(Long resourceId, List<Long> actions) {
        this.resourceId = resourceId;
        this.actions = actions;
    }
}
