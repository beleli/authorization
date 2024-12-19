package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Resource;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ResourceAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ResourceInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import br.com.blitech.authorization.domain.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static br.com.blitech.authorization.domain.TestUtils.createResource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOrThrow() throws ResourceNotFoundException {
        Long resourceId = 1L;
        Resource resource = createResource();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        Resource result = resourceService.findOrThrow(resourceId);
        assertEquals(resource, result);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> resourceService.findOrThrow(resourceId));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Resource> page = new PageImpl<>(Collections.singletonList(createResource()));
        when(resourceRepository.findAll(pageable)).thenReturn(page);

        Page<Resource> result = resourceService.findAll(pageable);
        assertEquals(page, result);
    }

    @Test
    void testSave() throws ResourceAlreadyExistsException {
        Resource resource = createResource();
        when(resourceRepository.save(resource)).thenReturn(resource);

        Resource result = resourceService.save(resource);
        assertEquals(resource, result);

        doThrow(DataIntegrityViolationException.class).when(resourceRepository).save(resource);
        assertThrows(ResourceAlreadyExistsException.class, () -> resourceService.save(resource));
    }

    @Test
    void testDelete() {
        Long resourceId = 1L;
        Resource resource = createResource();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        doNothing().when(resourceRepository).delete(resource);

        assertDoesNotThrow(() -> resourceService.delete(resourceId));

        doThrow(DataIntegrityViolationException.class).when(resourceRepository).delete(resource);
        assertThrows(ResourceInUseException.class, () -> resourceService.delete(resourceId));
    }
}