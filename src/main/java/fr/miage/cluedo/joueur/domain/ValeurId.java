package fr.miage.cluedo.joueur.domain;

/** Identifiant métier d'une valeur possible d'une variable (ex. « Prof. Leblanc », « Amphi A »). */
public record ValeurId(String code) {

    public ValeurId {
        if (code == null || code.isBlank()) {
            throw new EnqueteInvalideException("Le code d'une valeur ne peut pas être vide");
        }
    }
}
