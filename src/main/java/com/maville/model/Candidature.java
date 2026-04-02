package com.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "candidatures")
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requete_id", nullable = false)
    @JsonIgnoreProperties({"candidatures", "proprietaire"})
    private Requete requete;

    @ManyToOne
    @JoinColumn(name = "intervenant_id", nullable = false)
    @JsonIgnoreProperties({"projets", "candidatures", "notifications", "password"})
    private Intervenant intervenant;

    public Candidature() {}

    public Candidature(Requete requete, Intervenant intervenant) {
        this.requete = requete;
        this.intervenant = intervenant;
    }

    public Long getId() { return id; }
    public Requete getRequete() { return requete; }
    public void setRequete(Requete requete) { this.requete = requete; }
    public Intervenant getIntervenant() { return intervenant; }
    public void setIntervenant(Intervenant intervenant) { this.intervenant = intervenant; }
}
