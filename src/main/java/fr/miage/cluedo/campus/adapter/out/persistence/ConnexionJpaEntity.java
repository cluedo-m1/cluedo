package fr.miage.cluedo.campus.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Une ligne de la table connexion (migration V4). */
@Entity
@Table(name = "connexion")
class ConnexionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String depart;

    @Column(nullable = false, length = 20)
    private String arrivee;

    @Column(nullable = false)
    private int duree;

    protected ConnexionJpaEntity() {
        // requis par JPA
    }

    String getDepart() {
        return depart;
    }

    String getArrivee() {
        return arrivee;
    }

    int getDuree() {
        return duree;
    }
}