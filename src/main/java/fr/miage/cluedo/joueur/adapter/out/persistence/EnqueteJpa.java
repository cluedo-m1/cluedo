package fr.miage.cluedo.joueur.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Ligne de joueur_enquete. Simple support de persistance : aucune règle métier ici. */
@Entity
@Table(name = "joueur_enquete")
class EnqueteJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    protected EnqueteJpa() {
        // requis par JPA
    }

    Long getId() {
        return id;
    }

    String getTitre() {
        return titre;
    }
}
