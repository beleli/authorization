package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static br.com.blitech.authorization.domain.TestUtils.createAction;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ActionRepositoryTest {

    @Autowired
    private ActionRepository actionRepository;

    private Action action;

    @BeforeEach
    void setUp() {
        action = createAction();
        action = actionRepository.save(action);
    }

    @Test
    void testSaveAction() {
        Action newAction = new Action();
        newAction.setName("NewAction");
        Action savedAction = actionRepository.save(newAction);
        assertNotNull(savedAction.getId());
        assertEquals("NewAction".toLowerCase(), savedAction.getName());
        assertNotNull(newAction.getCreateDate());
    }

    @Test
    void testFindById() {
        Optional<Action> foundAction = actionRepository.findById(action.getId());
        assertTrue(foundAction.isPresent());
        assertEquals(action.getName(), foundAction.get().getName());
    }

    @Test
    void testDeleteAction() {
        actionRepository.delete(action);
        Optional<Action> deletedAction = actionRepository.findById(action.getId());
        assertFalse(deletedAction.isPresent());
    }

    @Test
    void testUpdateAction() {
        action.setName("UpdatedAction");
        Action updatedAction = actionRepository.save(action);
        actionRepository.flush();
        assertEquals("UpdatedAction".toLowerCase(), updatedAction.getName());
        assertNotNull(updatedAction.getUpdateDate());
    }
}