package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ResourceModelAssembler;
import br.com.blitech.authorization.api.v1.model.ResourceModel;
import br.com.blitech.authorization.api.v1.model.input.ResourceInputModel;
import br.com.blitech.authorization.domain.entity.Resource;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ResourceAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ResourceInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import br.com.blitech.authorization.domain.service.ResourceService;
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

import static br.com.blitech.authorization.api.TestUtlis.createResourceInputModel;
import static br.com.blitech.authorization.api.TestUtlis.createResourceModel;
import static br.com.blitech.authorization.domain.TestUtils.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResourceControllerTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceModelAssembler resourceModelAssembler;

    @InjectMocks
    private ResourceController resourceController;

    private Resource resource = createResource();
    private ResourceModel resourceModel = createResourceModel();
    private ResourceInputModel resourceInputModel = createResourceInputModel();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Resource> page = new PageImpl<>(Collections.singletonList(resource));
        when(resourceService.findAll(pageable)).thenReturn(page);
        when(resourceModelAssembler.toModel(any())).thenReturn(resourceModel);

        Page<ResourceModel> result = resourceController.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(resourceService, times(1)).findAll(pageable);
    }

    @Test
    void testFind() throws ResourceNotFoundException {
        when(resourceService.findOrThrow(anyLong())).thenReturn(resource);
        when(resourceModelAssembler.toModel(any())).thenReturn(resourceModel);

        ResourceModel result = resourceController.find(1L);

        assertNotNull(result);
        assertEquals(resource.getName(), result.getName());
        verify(resourceService, times(1)).findOrThrow(1L);
    }

    @Test
    void testInsert() throws ResourceAlreadyExistsException {
        MockedStatic mockedStatic = mockStatic(ResourceUriHelper.class);

        when(resourceService.save(any())).thenReturn(resource);
        when(resourceModelAssembler.toEntity(any())).thenReturn(resource);
        when(resourceModelAssembler.toModel(any())).thenReturn(resourceModel);

        ResourceModel result = resourceController.insert(resourceInputModel);

        assertNotNull(result);
        assertEquals(resource.getName(), result.getName());
        verify(resourceService, times(1)).save(any());

        mockedStatic.close();
    }

    @Test
    void testUpdate() throws ResourceAlreadyExistsException, ResourceNotFoundException {
        when(resourceService.findOrThrow(anyLong())).thenReturn(resource);
        when(resourceService.save(any())).thenReturn(resource);
        when(resourceModelAssembler.applyModel(any(), any())).thenReturn(resource);
        when(resourceModelAssembler.toModel(any())).thenReturn(resourceModel);

        ResourceModel result = resourceController.update(1L, resourceInputModel);

        assertNotNull(result);
        assertEquals(resource.getName(), result.getName());
        verify(resourceService, times(1)).findOrThrow(1L);
        verify(resourceService, times(1)).save(any());
    }

    @Test
    void testDelete() throws ResourceNotFoundException, ResourceInUseException {
        doNothing().when(resourceService).delete(anyLong());

        resourceController.delete(1L);

        verify(resourceService, times(1)).delete(1L);
    }
}