package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ActionModelAssembler;
import br.com.blitech.authorization.api.v1.model.ActionModel;
import br.com.blitech.authorization.api.v1.model.input.ActionInputModel;
import br.com.blitech.authorization.domain.entity.Action;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ActionAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ActionInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import br.com.blitech.authorization.domain.service.ActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static br.com.blitech.authorization.api.TestUtlis.createActionInputModel;
import static br.com.blitech.authorization.api.TestUtlis.createActionModel;
import static br.com.blitech.authorization.domain.TestUtils.createAction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActionControllerTest {

    @Mock
    private ActionService actionService;

    @Mock
    private ActionModelAssembler actionModelAssembler;

    @InjectMocks
    private ActionController actionController;

    private Action action = createAction();
    private ActionModel actionModel = createActionModel();
    private ActionInputModel actionInputModel = createActionInputModel();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Action> page = new PageImpl<>(Collections.singletonList(action));
        when(actionService.findAll(pageable)).thenReturn(page);
        when(actionModelAssembler.toModel(any())).thenReturn(actionModel);

        Page<ActionModel> result = actionController.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(actionService, times(1)).findAll(pageable);
    }

    @Test
    void testFind() throws ActionNotFoundException {
        when(actionService.findOrThrow(anyLong())).thenReturn(action);
        when(actionModelAssembler.toModel(any())).thenReturn(actionModel);

        ActionModel result = actionController.findById(1L);

        assertNotNull(result);
        assertEquals(action.getName(), result.getName());
        verify(actionService, times(1)).findOrThrow(1L);
    }

    @Test
    void testInsert() throws ActionAlreadyExistsException {
        MockedStatic mockedStatic = mockStatic(ResourceUriHelper.class);

        when(actionService.save(any())).thenReturn(action);
        when(actionModelAssembler.toEntity(any())).thenReturn(action);
        when(actionModelAssembler.toModel(any())).thenReturn(actionModel);

        ActionModel result = actionController.insert(actionInputModel);

        assertNotNull(result);
        assertEquals(action.getName(), result.getName());
        verify(actionService, times(1)).save(any());

        mockedStatic.close();
    }

    @Test
    void testUpdate() throws ActionAlreadyExistsException, ActionNotFoundException {
        when(actionService.findOrThrow(anyLong())).thenReturn(action);
        when(actionService.save(any())).thenReturn(action);
        when(actionModelAssembler.applyModel(any(), any())).thenReturn(action);
        when(actionModelAssembler.toModel(any())).thenReturn(actionModel);

        ActionModel result = actionController.update(1L, actionInputModel);

        assertNotNull(result);
        assertEquals(action.getName(), result.getName());
        verify(actionService, times(1)).findOrThrow(1L);
        verify(actionService, times(1)).save(any());
    }

    @Test
    void testDelete() throws ActionNotFoundException, ActionInUseException {
        doNothing().when(actionService).delete(anyLong());

        actionController.delete(1L);

        verify(actionService, times(1)).delete(1L);
    }
}