package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;

public class ActionModel extends Loggable {
    private final Long actionId;
    private final String name;

    public String getName() { return name; }
    public Long getActionId() { return actionId; }

    public ActionModel(Long actionId, String name) {
        this.actionId = actionId;
        this.name = name;
    }
}
