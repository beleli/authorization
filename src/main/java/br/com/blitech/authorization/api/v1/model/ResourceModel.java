package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;

public class ResourceModel extends Loggable {
    private Long resourceId;
    private String name;

    public String getName() { return name; }
    public Long getResourceId() { return resourceId; }

    public ResourceModel(Long resourceId, String name) {
        this.resourceId = resourceId;
        this.name = name;
    }
}
