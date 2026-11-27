package fr.miage.cluedo.joueur.application;

/** Aucune enquête ne porte cet identifiant (l'adaptateur web la traduira en 404). */
public class EnqueteIntrouvableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EnqueteIntrouvableException(long enqueteId) {
        super("Enquête introuvable : " + enqueteId);
    }
}
