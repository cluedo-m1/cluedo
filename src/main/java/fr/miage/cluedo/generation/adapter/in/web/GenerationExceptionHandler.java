package fr.miage.cluedo.generation.adapter.in.web;

import fr.miage.cluedo.generation.application.service.GenerationImpossibleException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(
        assignableTypes = GenerationController.class)
public class GenerationExceptionHandler {

    @ExceptionHandler(GenerationImpossibleException.class)
    public ProblemDetail generationImpossible(
            GenerationImpossibleException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail donneesInvalides(
            IllegalArgumentException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage());
    }
}