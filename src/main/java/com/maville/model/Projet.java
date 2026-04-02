package com.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "projets")
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTravaux typeTravaux;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProjet statutProjet = StatutProjet.PREVU;

    @Column(nullable = false)
    private String dateDebut;

    private String dateFin;

    private String quartierAffecte;

    @ManyToOne
    @JoinColumn(name = "intervenant_id")
    @JsonIgnoreProperties({"projets", "candidatures", "notifications", "password"})
    private Intervenant intervenant;

    public Projet() {}

    public Projet(String titre, String description, TypeTravaux typeTravaux,
                  String dateDebut, String dateFin, String quartierAffecte) {
        this.titre = titre;
        this.description = description;
        this.typeTravaux = typeTravaux;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.quartierAffecte = quartierAffecte;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TypeTravaux getTypeTravaux() { return typeTravaux; }
    public void setTypeTravaux(TypeTravaux typeTravaux) { this.typeTravaux = typeTravaux; }
    public StatutProjet getStatutProjet() { return statutProjet; }
    public void setStatutProjet(StatutProjet statutProjet) { this.statutProjet = statutProjet; }
    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }
    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }
    public String getQuartierAffecte() { return quartierAffecte; }
    public void setQuartierAffecte(String quartierAffecte) { this.quartierAffecte = quartierAffecte; }
    public Intervenant getIntervenant() { return intervenant; }
    public void setIntervenant(Intervenant intervenant) { this.intervenant = intervenant; }
}
