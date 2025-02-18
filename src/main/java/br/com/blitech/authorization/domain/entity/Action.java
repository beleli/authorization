package br.com.blitech.authorization.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_action")
public class Action extends UpdateControl{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_action")
    private Long id;

    @Column(name = "ds_name", nullable = false)
    private String name;

    public Long getId() { return id; }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Action(String name) {
        this.name = name;
    }

    public Action(Long id) {
        this.id = id;
    }

    public Action() { }

    @Override
    protected void normalizeFields() {
        this.name = setString(name);
    }
}
