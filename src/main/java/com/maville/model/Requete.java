package com.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requetes")
public class Requete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titreDuTravail;

    @Column(length = 2000)
    private String description;

    private String dateDeDebut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTravaux typeDeTravaux;

    @Column(nullable = false)
    private boolean ouvert = true;

    @ManyToOne
    @JoinColumn(name = "resident_id", nullable = false)
    @JsonIgnoreProperties({"horaires", "requetes", "notifications", "password"})
    private Resident proprietaire;

    @JsonIgnore
    @OneToMany(mappedBy = "requete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures = new ArrayList<>();

    public Requete() {}

    public Requete(String titreDuTravail, String description, String dateDeDebut,
                   TypeTravaux typeDeTravaux, Resident proprietaire) {
        this.titreDuTravail = titreDuTravail;
        this.description = description;
        this.dateDeDebut = dateDeDebut;
        this.typeDeTravaux = typeDeTravaux;
        this.proprietaire = proprietaire;
    }

    public Long getId() { return id; }
    public String getTitreDuTravail() { return titreDuTravail; }
    public void setTitreDuTravail(String titreDuTravail) { this.titreDuTravail = titreDuTravail; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDateDeDebut() { return dateDeDebut; }
    public void setDateDeDebut(String dateDeDebut) { this.dateDeDebut = dateDeDebut; }
    public TypeTravaux getTypeDeTravaux() { return typeDeTravaux; }
    public void setTypeDeTravaux(TypeTravaux typeDeTravaux) { this.typeDeTravaux = typeDeTravaux; }
    public boolean isOuvert() { return ouvert; }
    public void setOuvert(boolean ouvert) { this.ouvert = ouvert; }
    public Resident getProprietaire() { return proprietaire; }
    public void setProprietaire(Resident proprietaire) { this.proprietaire = proprietaire; }
    public List<Candidature> getCandidatures() { return candidatures; }
}
