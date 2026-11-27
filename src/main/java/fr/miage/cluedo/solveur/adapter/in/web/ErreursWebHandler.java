package fr.miage.cluedo.solveur.adapter.in.web;

import fr.miage.cluedo.solveur.domain.exception.EnqueteInvalideException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErreursWebHandler {

    @ExceptionHandler(EnqueteInvalideException.class)
    public ProblemDetail enqueteInvalide(EnqueteInvalideException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}