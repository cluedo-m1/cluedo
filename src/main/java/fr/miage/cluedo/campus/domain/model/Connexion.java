package fr.miage.cluedo.campus.domain.model;

import java.util.Objects;

public record Connexion(Batiment depart, Batiment arrivee, int duree) {

    public Connexion {
        Objects.requireNonNull(depart, "Le départ est obligatoire");
        Objects.requireNonNull(arrivee, "L'arrivée est obligatoire");

        if (duree <= 0) {
            throw new IllegalArgumentException(
                "La durée doit être strictement positive"
            );
        }

        if (depart.equals(arrivee)) {
            throw new IllegalArgumentException(
                "Une connexion ne peut pas relier un bâtiment à lui-même"
            );
        }
    }
}