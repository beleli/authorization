package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ResourceInputModel implements Loggable {

    @NotBlank
    @Length(max = 255)
    @Schema(description = "Resource name", example = "My Resource")
    private String name;

    public String getName() { return name; }

    public ResourceInputModel(String name) { this.name = name; }
    public ResourceInputModel() { }
}
