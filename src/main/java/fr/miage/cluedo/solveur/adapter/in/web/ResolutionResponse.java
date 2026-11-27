package fr.miage.cluedo.solveur.adapter.in.web;

import java.util.Map;
import java.util.Set;

public record ResolutionResponse(int nombreSolutions, Map<String, Set<String>> domaines) { }