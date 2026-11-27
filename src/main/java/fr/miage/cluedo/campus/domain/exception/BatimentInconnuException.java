package fr.miage.cluedo.campus.domain.exception;

public class BatimentInconnuException extends RuntimeException {

    public BatimentInconnuException(String code) {
        super("Bâtiment inconnu : " + code);
    }
}