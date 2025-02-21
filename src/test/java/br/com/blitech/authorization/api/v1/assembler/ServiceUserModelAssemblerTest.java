package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ServiceUserModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserInputModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserPasswordInputModel;
import br.com.blitech.authorization.domain.entity.ServiceUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.blitech.authorization.api.TestUtlis.createServiceUserInputModel;
import static br.com.blitech.authorization.api.TestUtlis.createServiceUserPasswordInputModel;
import static br.com.blitech.authorization.domain.TestUtils.createServiceUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceUserModelAssemblerTest {

    private ServiceUserModelAssembler serviceUserModelAssembler;

    @BeforeEach
    void setUp() {
        serviceUserModelAssembler = new ServiceUserModelAssembler();
    }

    @Test
    void testToModel() {
        ServiceUser serviceUser = createServiceUser();
        ServiceUserModel serviceUserModel = serviceUserModelAssembler.toModel(serviceUser);

        assertNotNull(serviceUserModel);
        assertEquals(serviceUser.getId(), serviceUserModel.getServiceUserId());
        assertEquals(serviceUser.getName(), serviceUserModel.getName());
        assertEquals(serviceUser.getProfile().getName(), serviceUserModel.getProfile());
    }

    @Test
    void testToEntity() {
        ServiceUserPasswordInputModel serviceUserPasswordInputModel = createServiceUserPasswordInputModel();
        ServiceUser serviceUser = serviceUserModelAssembler.toEntity(1L, serviceUserPasswordInputModel);

        assertNotNull(serviceUser);
        assertEquals(serviceUserPasswordInputModel.getName(), serviceUser.getName());
        assertEquals(serviceUserPasswordInputModel.getPassword(), serviceUser.getPassword());
        assertEquals(serviceUserPasswordInputModel.getProfileId(), serviceUser.getProfile().getId());
        assertEquals(1L, serviceUser.getApplication().getId());
    }

    @Test
    void testApplyModel() {
        ServiceUser serviceUser = createServiceUser();
        ServiceUserInputModel serviceUserInputModel = createServiceUserInputModel();
        ServiceUser updatedServiceUser = serviceUserModelAssembler.applyModel(serviceUser, serviceUserInputModel);

        assertNotNull(updatedServiceUser);
        assertEquals(serviceUser.getId(), updatedServiceUser.getId());
        assertEquals(serviceUserInputModel.getName(), updatedServiceUser.getName());
        assertEquals(serviceUserInputModel.getProfileId(), updatedServiceUser.getProfile().getId());
    }
}