package fr.miage.cluedo.solveur.adapter.in.web;

import java.util.Set;

public record IndiceDto(String type, String variable, String valeur, String variableX, String valeurX, String variableY, Set<String> valeursAutoriseesY) {
    
}
