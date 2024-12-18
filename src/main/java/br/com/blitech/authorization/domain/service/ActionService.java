package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Action;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ActionAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ActionInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import br.com.blitech.authorization.domain.repository.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActionService {

    @Autowired
    private ActionRepository actionRepository;

    @Transactional(readOnly = true)
    public Action findOrThrow(Long id) throws ActionNotFoundException {
        return actionRepository.findById(id).orElseThrow(ActionNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<Action> findAll(Pageable pageable) {
        return actionRepository.findAll(pageable);
    }

    @Transactional(rollbackFor = ActionAlreadyExistsException.class)
    public Action save(Action action) throws ActionAlreadyExistsException {
        try {
            action = actionRepository.save(action);
            actionRepository.flush();
            return action;
        } catch (DataIntegrityViolationException e) {
            throw new ActionAlreadyExistsException();
        }
    }

    @Transactional(rollbackFor = ActionInUseException.class)
    public void delete(Long id) throws ActionNotFoundException, ActionInUseException {
        try {
            actionRepository.delete(findOrThrow(id));
            actionRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ActionInUseException();
        }
    }
}
