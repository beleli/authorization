package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

public class ActionModel implements Loggable {

    @Schema(description = "Action id", example = "1")
    private final Long actionId;

    @Schema(description = "Action name", example = "CREATE")
    private final String name;

    public String getName() { return name; }
    public Long getActionId() { return actionId; }

    public ActionModel(Long actionId, String name) {
        this.actionId = actionId;
        this.name = name;
    }
}
