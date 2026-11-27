package fr.miage.cluedo.solveur.adapter.in.web;

import fr.miage.cluedo.solveur.domain.exception.EnqueteInvalideException;
import fr.miage.cluedo.solveur.domain.exception.EnqueteIncoherenteException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatusCode;

@RestControllerAdvice
public class ErreursWebHandler {

    @ExceptionHandler(EnqueteInvalideException.class)
    public ProblemDetail enqueteInvalide(EnqueteInvalideException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(EnqueteIncoherenteException.class)
    public ProblemDetail enqueteIncoherente(EnqueteIncoherenteException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), e.getMessage());
    }
}