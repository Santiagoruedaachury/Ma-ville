package com.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INTERVENANT")
public class Intervenant extends User {

    @Enumerated(EnumType.STRING)
    private IntervenantType type;

    @Column(unique = true)
    private Integer identifiantVille;

    @JsonIgnore
    @OneToMany(mappedBy = "intervenant", cascade = CascadeType.ALL)
    private List<Projet> projets = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "intervenant", cascade = CascadeType.ALL)
    private List<Candidature> candidatures = new ArrayList<>();

    public Intervenant() {}

    public Intervenant(String nom, String courriel, String password, IntervenantType type, int identifiantVille) {
        super(nom, courriel, password, Role.INTERVENANT);
        this.type = type;
        this.identifiantVille = identifiantVille;
    }

    public IntervenantType getType() { return type; }
    public void setType(IntervenantType type) { this.type = type; }
    public Integer getIdentifiantVille() { return identifiantVille; }
    public void setIdentifiantVille(Integer identifiantVille) { this.identifiantVille = identifiantVille; }
    public List<Projet> getProjets() { return projets; }
    public List<Candidature> getCandidatures() { return candidatures; }
}
