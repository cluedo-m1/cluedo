package fr.miage.cluedo.joueur.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Ligne de joueur_valeur_domaine : une valeur possible d'une variable, classée par {@code ordre}. */
@Entity
@Table(name = "joueur_valeur_domaine")
class ValeurDomaineJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variable_id", nullable = false)
    private long variableId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int ordre;

    protected ValeurDomaineJpa() {
        // requis par JPA
    }

    Long getId() {
        return id;
    }

    long getVariableId() {
        return variableId;
    }

    String getCode() {
        return code;
    }
}
