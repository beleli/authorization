package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

public class ResourceModel extends Loggable {

    @Schema(description = "Resource id", example = "1")
    private Long resourceId;

    @Schema(description = "Resource name", example = "My Resource")
    private String name;

    public String getName() { return name; }
    public Long getResourceId() { return resourceId; }

    public ResourceModel(Long resourceId, String name) {
        this.resourceId = resourceId;
        this.name = name;
    }
}
