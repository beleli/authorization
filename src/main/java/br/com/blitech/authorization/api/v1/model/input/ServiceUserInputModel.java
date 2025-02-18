package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ServiceUserInputModel implements Loggable {

    @NotBlank
    @Length(max = 255)
    @MaskProperty(format = LogMaskFormat.NAME)
    @Schema(description = "User name", example = "userName")
    private String name;

    @Schema(description = "Profile id", example = "1")
    private Long profileId;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    @Schema(description = "User password", example = "admin123")
    private String password;

    public String getName() { return name; }
    public Long getProfileId() { return profileId; }
    public String getPassword() { return password; }

    public ServiceUserInputModel(String name, Long profileId, String password) {
        this.name = name;
        this.profileId = profileId;
        this.password = password;
    }
}
