package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@MappedSuperclass
abstract public class UpdateControl {

    @Column(name = "dt_create", nullable = false)
    private OffsetDateTime createDate;

    @Column(name = "dt_update")
    private OffsetDateTime updateDate;

    public OffsetDateTime getCreateDate() { return createDate; }
    public OffsetDateTime getUpdateDate() { return updateDate; }

    @PrePersist
    public void prePersist() { createDate = OffsetDateTime.now(); }

    @PreUpdate
    public void preUpdate() { updateDate = OffsetDateTime.now(); }
}
