package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ActionModel;
import br.com.blitech.authorization.api.v1.model.input.ActionInputModel;
import br.com.blitech.authorization.domain.entity.Action;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ActionModelAssembler {

    public ActionModel toModel(@NotNull Action action) {
        return new ActionModel (
            action.getId(),
            action.getName()
        );
    }

    public Action toEntity(@NotNull ActionInputModel actionInputModel) {
        return new Action(
            actionInputModel.getName()
        );
    }

    public Action applyModel(@NotNull Action action, @NotNull ActionInputModel actionInputModel) {
        action.setName(actionInputModel.getName());
        return action;
    }
}
