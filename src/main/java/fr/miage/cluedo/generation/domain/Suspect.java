package fr.miage.cluedo.generation.domain;

import java.util.Set;

public record Suspect(
        String nom,
        Set<MoyenAcces> moyensAcces,
        Set<Horaire> horairesPresence
) {
    public Suspect {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom du suspect est obligatoire");
        }

        moyensAcces = Set.copyOf(moyensAcces);
        horairesPresence = Set.copyOf(horairesPresence);
    }
}