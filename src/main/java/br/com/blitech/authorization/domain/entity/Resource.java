package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_resource")
public class Resource extends UpdateControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resource")
    private Long id;

    @Column(name = "ds_name", nullable = false, unique = true)
    private String name;

    public Long getId() { return id; }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Resource(String name) {
        this.name = name;
    }

    public Resource(Long id) {
        this.id = id;
    }

    public Resource() { }

    @Override
    protected void prePersist() {
        normalizeFields();
        super.prePersist();
    }

    @Override
    protected void preUpdate() {
        normalizeFields();
        super.preUpdate();
    }

    private void normalizeFields() {
        this.name = setString(name);
    }
}
