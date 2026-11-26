package fr.miage.cluedo.solveur.domain.model;

import java.util.Set;
import java.util.Map;

public record Resultat(int nombreSolutions, Map<String, Set<String>> domaines) { }