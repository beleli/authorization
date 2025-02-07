package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ResourceActionInputModel implements Loggable {

    @Schema(description = "Resource id", example = "1")
    private Long resourceId;

    @Schema(description = "List of action ids", example = "[1, 2, 3]")
    private List<Long> actions;

    public Long getResourceId() { return resourceId; }
    public List<Long> getActions() { return actions; }

    public ResourceActionInputModel(Long resourceId, List<Long> actions) {
        this.resourceId = resourceId;
        this.actions = actions;
    }
}
