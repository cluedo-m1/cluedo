package fr.miage.cluedo.generation.domain;

import java.util.Objects;

public record Indice(
        Variable variable,
        Operateur operateur,
        String valeur,
        String description
) {

    public enum Variable {
        SUSPECT,
        LIEU,
        HORAIRE,
        MOYEN_ACCES
    }

    public enum Operateur {
        EGAL,
        DIFFERENT,
        AVANT,
        APRES
    }

    public Indice {
        Objects.requireNonNull(variable);
        Objects.requireNonNull(operateur);
        Objects.requireNonNull(valeur);
        Objects.requireNonNull(description);
    }
}