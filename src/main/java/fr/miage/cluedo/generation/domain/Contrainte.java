package fr.miage.cluedo.generation.domain;

public final class Contrainte {

    private Contrainte() {
    }

    public static boolean peutAcceder(
            Suspect suspect, Lieu lieu) {

        if (lieu.moyenAccesRequis() == MoyenAcces.ACCES_LIBRE) {
            return true;
        }

        return suspect.moyensAcces().contains(
                lieu.moyenAccesRequis());
    }

    public static boolean estPresent(
            Suspect suspect, Horaire horaire) {

        return suspect.horairesPresence().contains(horaire);
    }
}