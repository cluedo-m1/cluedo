package fr.miage.cluedo.campus.application.port.in;

/** Port d'entrée : le trajet est-il possible dans le temps écoulé ? */
public interface VerificationTrajetUseCase {

    ResultatVerification verifier(String codeDepart, String codeArrivee, int tempsEcoule);
}