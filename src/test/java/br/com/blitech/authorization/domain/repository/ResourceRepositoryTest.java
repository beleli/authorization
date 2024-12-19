package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static br.com.blitech.authorization.domain.TestUtils.createResource;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ResourceRepositoryTest {

    @Autowired
    private ResourceRepository resourceRepository;

    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = createResource();
        resource = resourceRepository.save(resource);
    }

    @Test
    void testSaveResource() {
        Resource newResource = new Resource();
        newResource.setName("NewResource");
        Resource savedResource = resourceRepository.save(newResource);
        assertNotNull(savedResource.getId());
        assertEquals("NewResource".toLowerCase(), savedResource.getName());
        assertNotNull(newResource.getCreateDate());
    }

    @Test
    void testFindById() {
        Optional<Resource> foundResource = resourceRepository.findById(resource.getId());
        assertTrue(foundResource.isPresent());
        assertEquals(resource.getName(), foundResource.get().getName());
    }

    @Test
    void testDeleteResource() {
        resourceRepository.delete(resource);
        Optional<Resource> deletedResource = resourceRepository.findById(resource.getId());
        assertFalse(deletedResource.isPresent());
    }

    @Test
    void testUpdateResource() {
        resource.setName("UpdatedResource");
        Resource updatedResource = resourceRepository.save(resource);
        resourceRepository.flush();
        assertEquals("UpdatedResource".toLowerCase(), updatedResource.getName());
        assertNotNull(updatedResource.getUpdateDate());
    }
}