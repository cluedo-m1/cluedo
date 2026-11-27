package fr.miage.cluedo.generation.domain;

import java.util.Objects;

public record Solution(
        Suspect coupable,
        Lieu lieu,
        Horaire horaire,
        MoyenAcces moyenAcces
) {
    public Solution {
        Objects.requireNonNull(coupable);
        Objects.requireNonNull(lieu);
        Objects.requireNonNull(horaire);
        Objects.requireNonNull(moyenAcces);
    }
}