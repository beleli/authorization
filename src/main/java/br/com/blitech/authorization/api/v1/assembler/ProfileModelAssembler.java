package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ActionModel;
import br.com.blitech.authorization.api.v1.model.ProfileModel;
import br.com.blitech.authorization.api.v1.model.ResourceActionModel;
import br.com.blitech.authorization.api.v1.model.input.ProfileInputModel;
import br.com.blitech.authorization.api.v1.model.input.ResourceActionInputModel;
import br.com.blitech.authorization.domain.entity.Action;
import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.entity.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProfileModelAssembler {

    public ProfileModel toModel(@NotNull Profile profile) {
        return new ProfileModel(
            profile.getId(),
            profile.getName(),
            profile.getResourceActions().stream()
                .collect(Collectors.groupingBy(
                        pra -> pra.getResource().getId(),
                        Collectors.mapping(
                                pra -> new ActionModel(pra.getAction().getId(), pra.getAction().getName()),
                                Collectors.toSet()
                        )
                ))
                .entrySet().stream()
                .map(entry -> {
                    Long resourceId = entry.getKey();
                    String resourceName = profile.getResourceActions().stream()
                            .filter(pra -> pra.getResource().getId().equals(resourceId))
                            .findFirst()
                            .map(pra -> pra.getResource().getName())
                            .orElse(null);
                    return new ResourceActionModel(resourceId, resourceName, entry.getValue());
                })
                .collect(Collectors.toList())
            );
        }

    public Profile toEntity(Long applicationId, @NotNull ProfileInputModel profileInputModel) {
        Profile profile = new Profile(
                null,
                new Application(applicationId),
                profileInputModel.getName(),
                profileInputModel.getGroup()
        );
        profile.setResourceActions(getProfileResourceActions(profile, profileInputModel));
        return profile;
    }

    public Profile applyModel(Long applicationId, @NotNull Profile profile, @NotNull ProfileInputModel profileInputModel) {
        profile.setApplication(new Application(applicationId));
        profile.setName(profileInputModel.getName());

        Set<ProfileResourceAction> updatedResourceActions = getProfileResourceActions(profile, profileInputModel);

        Set<ProfileResourceAction> existingResourceActions = profile.getResourceActions();
        existingResourceActions.removeIf(existing ->
                updatedResourceActions.stream().noneMatch(updated ->
                        updated.getResource().getId().equals(existing.getResource().getId()) &&
                                updated.getAction().getId().equals(existing.getAction().getId())
                )
        );
        for (ProfileResourceAction updated : updatedResourceActions) {
            if (existingResourceActions.stream().noneMatch(existing ->
                    existing.getResource().getId().equals(updated.getResource().getId()) &&
                            existing.getAction().getId().equals(updated.getAction().getId())
            )) {
                existingResourceActions.add(updated);
            }
        }

        profile.setResourceActions(existingResourceActions);
        return profile;
    }

    @NotNull
    private Set<ProfileResourceAction> getProfileResourceActions(@NotNull Profile profile, @NotNull ProfileInputModel profileInputModel) {
        Set<ProfileResourceAction> updatedResourceActions = new HashSet<>();
        for (ResourceActionInputModel resourceActionInputModel : profileInputModel.getResources()) {
            Resource resource = new Resource(resourceActionInputModel.getResourceId());
            for (Long actionId : resourceActionInputModel.getActions()) {
                Action action = new Action(actionId);
                ProfileResourceAction resourceAction = new ProfileResourceAction(profile, resource, action);
                updatedResourceActions.add(resourceAction);
            }
        }
        return updatedResourceActions;
    }
}
