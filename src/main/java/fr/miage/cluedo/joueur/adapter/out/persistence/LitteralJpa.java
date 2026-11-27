package fr.miage.cluedo.joueur.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Ligne de joueur_litteral : un littéral (variable, valeur, positif) d'une clause d'indice. */
@Entity
@Table(name = "joueur_litteral")
class LitteralJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "indice_id", nullable = false)
    private long indiceId;

    @Column(name = "variable_id", nullable = false)
    private long variableId;

    @Column(name = "valeur_id", nullable = false)
    private long valeurId;

    @Column(nullable = false)
    private boolean positif;

    protected LitteralJpa() {
        // requis par JPA
    }

    long getIndiceId() {
        return indiceId;
    }

    long getVariableId() {
        return variableId;
    }

    long getValeurId() {
        return valeurId;
    }

    boolean isPositif() {
        return positif;
    }
}
