package fr.miage.cluedo.campus.adapter.in.web;

import fr.miage.cluedo.campus.application.port.in.ResultatVerification;

/** dureeMinimale vaut null (absent du JSON utile) quand aucun chemin ne relie les deux bâtiments. */
public record VerificationTrajetReponse(String depart, String arrivee, int tempsEcoule,
                                        boolean possible, Integer dureeMinimale) {

    static VerificationTrajetReponse depuis(String depart, String arrivee, int tempsEcoule,
                                            ResultatVerification r) {
        Integer duree = r.dureeMinimale().isPresent() ? r.dureeMinimale().getAsInt() : null;
        return new VerificationTrajetReponse(depart, arrivee, tempsEcoule, r.possible(), duree);
    }
}