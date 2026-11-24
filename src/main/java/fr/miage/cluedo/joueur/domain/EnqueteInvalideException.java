package fr.miage.cluedo.joueur.domain;

/** Violation d'un invariant du domaine « joueur » (levée dès la construction : fail fast). */
public class EnqueteInvalideException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EnqueteInvalideException(String message) {
        super(message);
    }
}
