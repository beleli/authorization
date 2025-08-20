package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ActionInputModel implements Loggable {

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @Schema(description = "Action name", example = "CREATE")
    private String name;

    public String getName() { return name; }

    public ActionInputModel(String name) { this.name = name; }
    public ActionInputModel() { }
}
