package com.maville.config;

import com.maville.model.*;
import com.maville.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ResidentRepository residentRepository;
    private final IntervenantRepository intervenantRepository;
    private final ProjetRepository projetRepository;
    private final RequeteRepository requeteRepository;
    private final HoraireRepository horaireRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ResidentRepository residentRepository,
                      IntervenantRepository intervenantRepository,
                      ProjetRepository projetRepository,
                      RequeteRepository requeteRepository,
                      HoraireRepository horaireRepository,
                      PasswordEncoder passwordEncoder) {
        this.residentRepository = residentRepository;
        this.intervenantRepository = intervenantRepository;
        this.projetRepository = projetRepository;
        this.requeteRepository = requeteRepository;
        this.horaireRepository = horaireRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (residentRepository.count() > 0) return;

        // --- Residents ---
        Resident r1 = createResident("Alice Dupont", "alice@mail.com", "1990-05-15", "123 Rue Principale, Plateau-Mont-Royal");
        Resident r2 = createResident("Bob Martin", "bob@mail.com", "1985-08-22", "456 Avenue du Parc, Ville-Marie");
        Resident r3 = createResident("Charlie Tremblay", "charlie@mail.com", "2000-01-10", "789 Boulevard Saint-Laurent, Rosemont");
        Resident r4 = createResident("Diana Gagnon", "diana@mail.com", "1995-12-03", "101 Rue Sainte-Catherine, Mercier");
        Resident r5 = createResident("Étienne Lavoie", "etienne@mail.com", "1988-07-19", "202 Chemin de la Côte-des-Neiges, Outremont");

        // --- Intervenants ---
        Intervenant i1 = createIntervenant("Construction ABC", "abc@construction.com", IntervenantType.PRIVE, 1001);
        Intervenant i2 = createIntervenant("Ville de Montréal - DTP", "dtp@montreal.ca", IntervenantType.PUBLIC, 2001);
        Intervenant i3 = createIntervenant("Plomberie Express", "info@plomberieexpress.com", IntervenantType.PARTICULIER, 3001);
        Intervenant i4 = createIntervenant("Électricité Pro", "contact@elecpro.com", IntervenantType.PRIVE, 1002);
        Intervenant i5 = createIntervenant("Paysagement Vert", "info@paysvert.com", IntervenantType.PARTICULIER, 3002);

        // --- Projets ---
        projetRepository.save(buildProjet("Réfection de la chaussée", "Travaux de réfection sur la rue Principale",
                TypeTravaux.TRAVAUX_ROUTIERS, StatutProjet.EN_COURS, "2024-09-01", "2024-12-15", "Plateau-Mont-Royal", i1));
        projetRepository.save(buildProjet("Installation de nouvelles conduites", "Remplacement des conduites d'eau vétustes",
                TypeTravaux.TRAVAUX_GAZ_ELECTRICITE, StatutProjet.PREVU, "2025-01-10", "2025-06-30", "Ville-Marie", i2));
        projetRepository.save(buildProjet("Aménagement paysager du parc", "Création d'un nouvel espace vert",
                TypeTravaux.ENTRETIEN_PAYSAGER, StatutProjet.PREVU, "2025-03-01", "2025-09-01", "Rosemont", i5));
        projetRepository.save(buildProjet("Rénovation du réseau électrique", "Mise à jour du réseau électrique souterrain",
                TypeTravaux.TRAVAUX_GAZ_ELECTRICITE, StatutProjet.SUSPENDU, "2024-06-01", "2024-11-30", "Mercier", i4));

        // --- Requêtes ---
        requeteRepository.save(buildRequete("Réparation de nid-de-poule", "Nid-de-poule dangereux devant le 123 Rue Principale",
                "2024-10-01", TypeTravaux.TRAVAUX_ROUTIERS, r1));
        requeteRepository.save(buildRequete("Élagage d'arbres", "Branches qui bloquent la visibilité au coin de la rue",
                "2024-10-15", TypeTravaux.ENTRETIEN_PAYSAGER, r2));
        requeteRepository.save(buildRequete("Fuite d'eau", "Fuite visible sur le trottoir devant le 789 Boulevard Saint-Laurent",
                "2024-11-01", TypeTravaux.TRAVAUX_SOUTERRAINS, r3));
        requeteRepository.save(buildRequete("Lampadaire défectueux", "Lampadaire ne fonctionne plus depuis 2 semaines",
                "2024-11-05", TypeTravaux.TRAVAUX_GAZ_ELECTRICITE, r4));
        requeteRepository.save(buildRequete("Trottoir endommagé", "Dalle de trottoir soulevée créant un risque de chute",
                "2024-11-10", TypeTravaux.CONSTRUCTION_RENOVATION, r5));
    }

    private Resident createResident(String nom, String courriel, String dateNaissance, String adresse) {
        Resident r = new Resident();
        r.setNom(nom);
        r.setCourriel(courriel);
        r.setPassword(passwordEncoder.encode("password123"));
        r.setRole(Role.RESIDENT);
        r.setDateNaissance(LocalDate.parse(dateNaissance));
        r.setAdresseResidentielle(adresse);
        r = residentRepository.save(r);

        for (JourDeLaSemaine jour : JourDeLaSemaine.values()) {
            Horaire h = new Horaire();
            h.setJourDeLaSemaine(jour);
            h.setHeureDebut("Pas spécifié");
            h.setHeureFin("Pas spécifié");
            h.setResident(r);
            horaireRepository.save(h);
        }
        return r;
    }

    private Intervenant createIntervenant(String nom, String courriel, IntervenantType type, int identifiantVille) {
        Intervenant i = new Intervenant();
        i.setNom(nom);
        i.setCourriel(courriel);
        i.setPassword(passwordEncoder.encode("password123"));
        i.setRole(Role.INTERVENANT);
        i.setType(type);
        i.setIdentifiantVille(identifiantVille);
        return intervenantRepository.save(i);
    }

    private Projet buildProjet(String titre, String description, TypeTravaux type,
                                StatutProjet statut, String debut, String fin, String quartier, Intervenant i) {
        Projet p = new Projet();
        p.setTitre(titre);
        p.setDescription(description);
        p.setTypeTravaux(type);
        p.setStatutProjet(statut);
        p.setDateDebut(debut);
        p.setDateFin(fin);
        p.setQuartierAffecte(quartier);
        p.setIntervenant(i);
        return p;
    }

    private Requete buildRequete(String titre, String description, String dateDebut,
                                  TypeTravaux type, Resident proprietaire) {
        Requete r = new Requete();
        r.setTitreDuTravail(titre);
        r.setDescription(description);
        r.setDateDeDebut(dateDebut);
        r.setTypeDeTravaux(type);
        r.setOuvert(true);
        r.setProprietaire(proprietaire);
        return r;
    }
}
