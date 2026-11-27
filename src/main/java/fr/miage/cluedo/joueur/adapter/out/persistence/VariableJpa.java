package fr.miage.cluedo.joueur.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Ligne de joueur_variable : une dimension de l'enquête, classée par {@code ordre}. */
@Entity
@Table(name = "joueur_variable")
class VariableJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enquete_id", nullable = false)
    private long enqueteId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int ordre;

    protected VariableJpa() {
        // requis par JPA
    }

    Long getId() {
        return id;
    }

    String getCode() {
        return code;
    }
}
