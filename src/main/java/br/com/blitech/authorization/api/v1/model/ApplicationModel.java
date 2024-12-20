package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.LogMaskFormat;
import br.com.blitech.authorization.core.log.Loggable;
import br.com.blitech.authorization.core.log.MaskProperty;

public class ApplicationModel extends Loggable {
    private Long applicationId;
    private String name;
    @MaskProperty(format = LogMaskFormat.NAME)
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
