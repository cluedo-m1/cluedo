package fr.miage.cluedo.solveur.domain.exception;

public class EnqueteIncoherenteException extends RuntimeException{
    public EnqueteIncoherenteException(String message){
        super(message);
    }
}
