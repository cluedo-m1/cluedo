package fr.miage.cluedo.generation.domain;

import java.util.Objects;

public record Lieu(
        String nom,
        MoyenAcces moyenAccesRequis
) {
    public Lieu {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom du lieu est obligatoire");
        }

        Objects.requireNonNull(moyenAccesRequis);
    }
}