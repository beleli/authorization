package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ApplicationModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationPasswordInputModel;
import br.com.blitech.authorization.domain.entity.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.blitech.authorization.api.TestUtlis.createApplicationInputModel;
import static br.com.blitech.authorization.api.TestUtlis.createApplicationPasswordInputModel;
import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationModelAssemblerTest {

    private ApplicationModelAssembler applicationModelAssembler;

    @BeforeEach
    void setUp() {
        applicationModelAssembler = new ApplicationModelAssembler();
    }

    @Test
    void testToModel() {
        Application application = createApplication();
        ApplicationModel applicationModel = applicationModelAssembler.toModel(application);

        assertNotNull(applicationModel);
        assertEquals(application.getId(), applicationModel.getApplicationId());
        assertEquals(application.getName(), applicationModel.getName());
    }

    @Test
    void testToEntity() {
        ApplicationPasswordInputModel applicationPasswordInputModel = createApplicationPasswordInputModel();
        Application application = applicationModelAssembler.toEntity(applicationPasswordInputModel);

        assertNotNull(application);
        assertEquals(applicationPasswordInputModel.getName(), application.getName());
        assertEquals(applicationPasswordInputModel.getUser(), application.getUser());
        assertEquals(applicationPasswordInputModel.getPassword(), application.getPassword());
    }

    @Test
    void testApplyModel() {
        Application application = createApplication();
        ApplicationInputModel applicationInputModel = createApplicationInputModel();
        Application updatedApplication = applicationModelAssembler.applyModel(application, applicationInputModel);

        assertNotNull(updatedApplication);
        assertEquals(application.getId(), updatedApplication.getId());
        assertEquals(applicationInputModel.getName(), updatedApplication.getName());
        assertEquals(applicationInputModel.getUser(), updatedApplication.getUser());
    }
}