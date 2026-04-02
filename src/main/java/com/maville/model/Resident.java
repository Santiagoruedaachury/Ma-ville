package com.maville.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("RESIDENT")
public class Resident extends User {

    private LocalDate dateNaissance;

    private String adresseResidentielle;

    @JsonIgnore
    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horaire> horaires = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    private List<Requete> requetes = new ArrayList<>();

    public Resident() {}

    public Resident(String nom, String courriel, String password, LocalDate dateNaissance, String adresse) {
        super(nom, courriel, password, Role.RESIDENT);
        this.dateNaissance = dateNaissance;
        this.adresseResidentielle = adresse;
        initialiserHoraires();
    }

    public void initialiserHoraires() {
        horaires.clear();
        for (JourDeLaSemaine jour : JourDeLaSemaine.values()) {
            Horaire h = new Horaire(jour);
            h.setResident(this);
            horaires.add(h);
        }
    }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getAdresseResidentielle() { return adresseResidentielle; }
    public void setAdresseResidentielle(String adresseResidentielle) { this.adresseResidentielle = adresseResidentielle; }
    public List<Horaire> getHoraires() { return horaires; }
    public List<Requete> getRequetes() { return requetes; }
}
