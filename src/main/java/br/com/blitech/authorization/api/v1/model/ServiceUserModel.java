package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

public class ServiceUserModel implements Loggable {

    @Schema(description = "Service User id", example = "1")
    private Long serviceUserId;

    @MaskProperty(format = LogMaskFormat.NAME)
    @Schema(description = "User name", example = "My User")
    private String name;

    @Schema(description = "Application profile", example = "admin")
    private String profile;

    public Long getServiceUserId() { return serviceUserId; }
    public String getName() { return name; }
    public String getProfile() { return profile; }

    public ServiceUserModel(Long serviceUserId, String name, String profile) {
        this.serviceUserId = serviceUserId;
        this.name = name;
        this.profile = profile;
    }
}

