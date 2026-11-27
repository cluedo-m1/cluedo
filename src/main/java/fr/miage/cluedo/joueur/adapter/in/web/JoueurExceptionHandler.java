package fr.miage.cluedo.joueur.adapter.in.web;

import fr.miage.cluedo.joueur.application.EnqueteIntrouvableException;
import fr.miage.cluedo.joueur.domain.EnqueteInvalideException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduit les exceptions du module en erreurs RFC 9457 (application/problem+json). Les erreurs de
 * format du corps ou de validation sont déjà traduites par Spring (spring.mvc.problemdetails.enabled).
 */
@RestControllerAdvice(assignableTypes = JoueurController.class)
class JoueurExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JoueurExceptionHandler.class);

    @ExceptionHandler(EnqueteIntrouvableException.class)
    ProblemDetail enqueteIntrouvable(EnqueteIntrouvableException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        probleme.setTitle("Enquête introuvable");
        return probleme;
    }

    @ExceptionHandler(EnqueteInvalideException.class)
    ProblemDetail requeteInvalide(EnqueteInvalideException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        probleme.setTitle("Requête invalide");
        return probleme;
    }

    /** Données incohérentes en base : ce n'est pas la faute du client, et on ne détaille pas la cause. */
    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail erreurInterne(IllegalStateException exception) {
        LOG.error("Erreur interne dans le module joueur", exception);
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue.");
        probleme.setTitle("Erreur interne");
        return probleme;
    }
}
