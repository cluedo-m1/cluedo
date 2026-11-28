package fr.miage.cluedo.campus.adapter.in.web;

import fr.miage.cluedo.campus.domain.exception.BatimentInconnuException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Erreurs du module campus au format problem+json. */
@RestControllerAdvice(basePackageClasses = CampusExceptionHandler.class)
class CampusExceptionHandler {

    @ExceptionHandler(BatimentInconnuException.class)
    ProblemDetail batimentInconnu(BatimentInconnuException e) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        p.setTitle("Bâtiment introuvable");
        return p;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail donneeInvalide(IllegalArgumentException e) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        p.setTitle("Donnée invalide");
        return p;
    }
}