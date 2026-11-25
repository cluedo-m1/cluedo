package fr.miage.cluedo.campus.domain.model;

public record Batiment(String code, String nom) {

    public Batiment {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code du bâtiment ne doit pas être vide");
        }
    }
}