package fr.miage.cluedo.generation.application.service;

public final class GenerationImpossibleException
        extends RuntimeException {

    public GenerationImpossibleException(String message) {
        super(message);
    }
}