package fr.miage.cluedo.solveur.domain.model;
import java.util.Set;

public record Implication(String variableX, String valeurX,
                          String variableY, Set<String> valeursAutoriseesY) implements Indice { }