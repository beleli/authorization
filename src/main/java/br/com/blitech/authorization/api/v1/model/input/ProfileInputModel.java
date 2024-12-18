package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileInputModel extends Loggable {

    @NotBlank
    @Length(max = 255)
    private String name;

    @NotBlank
    @Length(max = 255)
    private String group;

    @NotNull
    @Size(min = 1)
    private List<ResourceActionInputModel> resources;

    public String getName() { return name; }
    public String getGroup() { return group; }
    public List<ResourceActionInputModel> getResources() { return resources; }
}
