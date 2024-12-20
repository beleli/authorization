package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ActionInputModel extends Loggable {

    @NotBlank
    @Length(max = 255)
    private String name;

    public String getName() { return name; }

    public ActionInputModel(String name) {
        this.name = name;
    }
}
