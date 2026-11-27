package fr.miage.cluedo.campus.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Une ligne de la table batiment (migration V4). */
@Entity
@Table(name = "batiment")
class BatimentJpaEntity {

    @Id
    @Column(length = 20)
    private String code;

    @Column(nullable = false, length = 120)
    private String nom;

    protected BatimentJpaEntity() {
        // requis par JPA
    }

    String getCode() {
        return code;
    }

    String getNom() {
        return nom;
    }
}