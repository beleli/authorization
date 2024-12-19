package br.com.blitech.authorization.api.v1.assembler;

import br.com.blitech.authorization.api.v1.model.ResourceModel;
import br.com.blitech.authorization.api.v1.model.input.ResourceInputModel;
import br.com.blitech.authorization.domain.entity.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ResourceModelAssembler {

    public ResourceModel toModel(@NotNull Resource resource) {
        return new ResourceModel (
            resource.getId(),
            resource.getName()
        );
    }

    public Resource toEntity(@NotNull ResourceInputModel resourceInputModel) {
        return new Resource(
            resourceInputModel.getName()
        );
    }

    public Resource applyModel(@NotNull Resource resource, @NotNull ResourceInputModel resourceInputModel) {
        resource.setName(resourceInputModel.getName());
        return resource;
    }
}
