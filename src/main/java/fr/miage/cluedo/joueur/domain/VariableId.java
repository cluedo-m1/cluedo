package fr.miage.cluedo.joueur.domain;

/** Identifiant métier d'une dimension de l'enquête (ex. « SUSPECT », « LIEU », « OBJET »). */
public record VariableId(String code) {

    public VariableId {
        if (code == null || code.isBlank()) {
            throw new EnqueteInvalideException("Le code d'une variable ne peut pas être vide");
        }
    }
}
