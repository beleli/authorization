package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

public class ApplicationModel extends Loggable {

    @Schema(description = "Application id", example = "1")
    private Long applicationId;

    @Schema(description = "Application name", example = "My Application")
    private String name;

    @MaskProperty(format = LogMaskFormat.NAME)
    @Schema(description = "Application user", example = "admin")
    private String user;

    public Long getApplicationId() { return applicationId; }
    public String getName() { return name; }
    public String getUser() { return user; }

    public ApplicationModel(Long applicationId, String name, String user) {
        this.applicationId = applicationId;
        this.name = name;
        this.user = user;
    }
}
