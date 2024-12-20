package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ProfileModel;
import br.com.blitech.authorization.api.v1.model.input.ProfileInputModel;
import br.com.blitech.authorization.domain.entity.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.blitech.authorization.api.TestUtlis.createProfileInputModel;
import static br.com.blitech.authorization.domain.TestUtils.createProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProfileModelAssemblerTest {

    private ProfileModelAssembler profileModelAssembler;

    @BeforeEach
    void setUp() {
        profileModelAssembler = new ProfileModelAssembler();
    }

    @Test
    void testToModel() {
        Profile profile = createProfile();
        ProfileModel profileModel = profileModelAssembler.toModel(profile);

        assertNotNull(profileModel);
        assertEquals(profile.getId(), profileModel.getProfileId());
        assertEquals(profile.getName(), profileModel.getName());
        assertEquals(profile.getResourceActions().size(), profileModel.getResourceActions().size());
    }

    @Test
    void testToEntity() {
        ProfileInputModel profileInputModel = createProfileInputModel();
        Profile profile = profileModelAssembler.toEntity(1L, profileInputModel);

        assertNotNull(profile);
        assertEquals(profileInputModel.getName(), profile.getName());
        assertEquals(profileInputModel.getResources().size(), profile.getResourceActions().size());
    }

    @Test
    void testApplyModel() {
        Profile profile = createProfile();
        ProfileInputModel profileInputModel = createProfileInputModel();
        Profile updatedProfile = profileModelAssembler.applyModel(1L, profile, profileInputModel);

        assertNotNull(updatedProfile);
        assertEquals(profile.getId(), updatedProfile.getId());
        assertEquals(profileInputModel.getName(), updatedProfile.getName());
        assertEquals(profileInputModel.getResources().size(), updatedProfile.getResourceActions().size());
    }
}