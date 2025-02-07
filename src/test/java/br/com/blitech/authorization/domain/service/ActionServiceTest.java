package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Action;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ActionAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ActionInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import br.com.blitech.authorization.domain.repository.ActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static br.com.blitech.authorization.domain.TestUtils.createAction;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActionServiceTest {

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private ActionService actionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOrThrowWhenActionExists() throws ActionNotFoundException {
        Action action = createAction();
        when(actionRepository.findById(anyLong())).thenReturn(Optional.of(action));

        Action result = actionService.findOrThrow(1L);

        assertNotNull(result);
        assertEquals(action, result);
    }

    @Test
    void testFindOrThrowWhenActionDoesNotExist() {
        when(actionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ActionNotFoundException.class, () -> actionService.findOrThrow(1L));
    }

    @Test
    void testFindAll() {
        Page<Action> actions = new PageImpl<>(List.of(createAction()));
        when(actionRepository.findAll(any(Pageable.class))).thenReturn(actions);

        Page<Action> result = actionService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(actions, result);
    }

    @Test
    void testSaveActionSuccess() throws ActionAlreadyExistsException {
        Action action = createAction();
        when(actionRepository.save(any(Action.class))).thenReturn(action);

        Action result = actionService.save(action);

        assertNotNull(result);
        assertEquals(action, result);
        verify(actionRepository).flush();
    }

    @Test
    void testSaveActionThrowsActionAlreadyExistsException() {
        when(actionRepository.save(any(Action.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ActionAlreadyExistsException.class, () -> actionService.save(createAction()));
    }

    @Test
    void testDeleteActionSuccess() {
        Action action = createAction();
        when(actionRepository.findById(anyLong())).thenReturn(Optional.of(action));
        doNothing().when(actionRepository).delete(any(Action.class));

        assertDoesNotThrow(() -> actionService.delete(1L));

        verify(actionRepository).flush();
    }

    @Test
    void testDeleteActionThrowsActionNotFoundException() {
        when(actionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ActionNotFoundException.class, () -> actionService.delete(1L));
    }

    @Test
    void testDeleteActionThrowsActionInUseException() {
        Action action = createAction();
        when(actionRepository.findById(anyLong())).thenReturn(Optional.of(action));
        doThrow(DataIntegrityViolationException.class).when(actionRepository).delete(any(Action.class));

        assertThrows(ActionInUseException.class, () -> actionService.delete(1L));
    }
}