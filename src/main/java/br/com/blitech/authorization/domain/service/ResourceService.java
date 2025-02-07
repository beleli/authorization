package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Resource;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ResourceAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ResourceInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import br.com.blitech.authorization.domain.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional(readOnly = true)
    public Resource findOrThrow(Long id) throws ResourceNotFoundException {
        return resourceRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<Resource> findAll(Pageable pageable) {
        return resourceRepository.findAll(pageable);
    }

    @Transactional(rollbackFor = ResourceAlreadyExistsException.class)
    public Resource save(Resource resource) throws ResourceAlreadyExistsException {
        try {
            resource = resourceRepository.save(resource);
            resourceRepository.flush();
            return resource;
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException();
        }
    }

    @Transactional(rollbackFor = ResourceInUseException.class)
    public void delete(Long id) throws ResourceNotFoundException, ResourceInUseException {
        try {
            resourceRepository.delete(this.findOrThrow(id));
            resourceRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException();
        }
    }
}
