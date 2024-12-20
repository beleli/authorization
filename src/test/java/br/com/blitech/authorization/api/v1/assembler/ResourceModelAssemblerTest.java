package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ResourceModel;
import br.com.blitech.authorization.api.v1.model.input.ResourceInputModel;
import br.com.blitech.authorization.domain.entity.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.blitech.authorization.domain.TestUtils.createResource;
import static org.junit.jupiter.api.Assertions.*;

class ResourceModelAssemblerTest {

    private ResourceModelAssembler resourceModelAssembler;

    @BeforeEach
    void setUp() {
        resourceModelAssembler = new ResourceModelAssembler();
    }

    @Test
    void testToModel() {
        Resource resource = createResource();
        ResourceModel resourceModel = resourceModelAssembler.toModel(resource);

        assertNotNull(resourceModel);
        assertEquals(resource.getId(), resourceModel.getResourceId());
        assertEquals(resource.getName(), resourceModel.getName());
    }

    @Test
    void testToEntity() {
        ResourceInputModel resourceInputModel = new ResourceInputModel("Test Resource");
        Resource resource = resourceModelAssembler.toEntity(resourceInputModel);

        assertNotNull(resource);
        assertEquals(resourceInputModel.getName(), resource.getName());
    }

    @Test
    void testApplyModel() {
        Resource resource = createResource();
        ResourceInputModel resourceInputModel = new ResourceInputModel("New Resource");
        Resource updatedResource = resourceModelAssembler.applyModel(resource, resourceInputModel);

        assertNotNull(updatedResource);
        assertEquals(resource.getId(), updatedResource.getId());
        assertEquals(resourceInputModel.getName(), updatedResource.getName());
    }
}