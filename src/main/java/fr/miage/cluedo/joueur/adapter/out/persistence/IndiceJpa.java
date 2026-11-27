package fr.miage.cluedo.joueur.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Ligne de joueur_indice : l'énoncé d'un indice ; sa clause est dans joueur_litteral. */
@Entity
@Table(name = "joueur_indice")
class IndiceJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enquete_id", nullable = false)
    private long enqueteId;

    @Column(nullable = false)
    private String libelle;

    protected IndiceJpa() {
        // requis par JPA
    }

    Long getId() {
        return id;
    }

    String getLibelle() {
        return libelle;
    }
}
