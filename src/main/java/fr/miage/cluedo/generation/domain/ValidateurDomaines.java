package fr.miage.cluedo.generation.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public final class ValidateurDomaines {

    private ValidateurDomaines() {
    }

    public static void valider(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces) {

        verifierDomaine(suspects, "suspects");
        verifierDomaine(lieux, "lieux");
        verifierDomaine(horaires, "horaires");
        verifierDomaine(moyensAcces, "moyens d'acces");

        verifierDomaine(
                suspects.stream().map(Suspect::nom).toList(),
                "noms des suspects");

        verifierDomaine(
                lieux.stream().map(Lieu::nom).toList(),
                "noms des lieux");
    }

    private static <T> void verifierDomaine(
            List<T> valeurs, String nom) {

        if (valeurs == null) {
            throw new IllegalArgumentException(
                    "Domaine absent : " + nom);
        }

        if (valeurs.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Valeur nulle dans le domaine : " + nom);
        }

        if (new HashSet<>(valeurs).size() != valeurs.size()) {
            throw new IllegalArgumentException(
                    "Doublons dans le domaine : " + nom);
        }
    }
}