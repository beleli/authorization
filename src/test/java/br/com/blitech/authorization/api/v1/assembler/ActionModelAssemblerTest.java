package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ActionModel;
import br.com.blitech.authorization.api.v1.model.input.ActionInputModel;
import br.com.blitech.authorization.domain.entity.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.blitech.authorization.domain.TestUtils.createAction;
import static org.junit.jupiter.api.Assertions.*;

class ActionModelAssemblerTest {

    private ActionModelAssembler actionModelAssembler;

    @BeforeEach
    void setUp() {
        actionModelAssembler = new ActionModelAssembler();
    }

    @Test
    void testToModel() {
        Action action = createAction();
        ActionModel actionModel = actionModelAssembler.toModel(action);

        assertNotNull(actionModel);
        assertEquals(action.getId(), actionModel.getActionId());
        assertEquals(action.getName(), actionModel.getName());
    }

    @Test
    void testToEntity() {
        ActionInputModel actionInputModel = new ActionInputModel("Test Action");
        Action action = actionModelAssembler.toEntity(actionInputModel);

        assertNotNull(action);
        assertEquals(actionInputModel.getName(), action.getName());
    }

    @Test
    void testApplyModel() {
        Action action = createAction();
        ActionInputModel actionInputModel = new ActionInputModel("New Action");
        Action updatedAction = actionModelAssembler.applyModel(action, actionInputModel);

        assertNotNull(updatedAction);
        assertEquals(action.getId(), updatedAction.getId());
        assertEquals(actionInputModel.getName(), updatedAction.getName());
    }
}