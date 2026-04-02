package com.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "horaires")
public class Horaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JourDeLaSemaine jourDeLaSemaine;

    private String heureDebut = "Pas spécifié";

    private String heureFin = "Pas spécifié";

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Resident resident;

    public Horaire() {}

    public Horaire(JourDeLaSemaine jourDeLaSemaine) {
        this.jourDeLaSemaine = jourDeLaSemaine;
    }

    public Long getId() { return id; }
    public JourDeLaSemaine getJourDeLaSemaine() { return jourDeLaSemaine; }
    public void setJourDeLaSemaine(JourDeLaSemaine jourDeLaSemaine) { this.jourDeLaSemaine = jourDeLaSemaine; }
    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }
    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }
    public Resident getResident() { return resident; }
    public void setResident(Resident resident) { this.resident = resident; }
}
