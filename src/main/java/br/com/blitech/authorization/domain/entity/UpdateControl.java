package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@MappedSuperclass
abstract public class UpdateControl extends BaseEntity {

    @Column(name = "dt_create", nullable = false)
    private OffsetDateTime createDate;

    @Column(name = "dt_update")
    private OffsetDateTime updateDate;

    public OffsetDateTime getCreateDate() { return createDate; }
    public OffsetDateTime getUpdateDate() { return updateDate; }

    @PrePersist
    protected void prePersist() { createDate = OffsetDateTime.now(); }

    @PreUpdate
    protected void preUpdate() {
        updateDate = OffsetDateTime.now();
    }
}
