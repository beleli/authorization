package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileInputModel implements Loggable {

    @NotBlank
    @Length(max = 255)
    @Schema(description = "Profile name", example = "My Profile")
    private String name;

    @NotBlank
    @Length(max = 255)
    @Schema(description = "Profile domain group", example = "My Group")
    private String group;

    @NotNull
    @Size(min = 1)
    @Schema(description = "Profile resources")
    private List<ResourceActionInputModel> resources;

    public String getName() { return name; }
    public String getGroup() { return group; }
    public List<ResourceActionInputModel> getResources() { return resources; }

    public ProfileInputModel(String name, String group, List<ResourceActionInputModel> resources) {
        this.name = name;
        this.group = group;
        this.resources = resources;
    }
}
