package fr.miage.cluedo.solveur.application.port.in;
import fr.miage.cluedo.solveur.domain.model.Resultat;
import fr.miage.cluedo.solveur.domain.model.Indice;

import java.util.Map;
import java.util.Set;
import java.util.List;

public interface ResoudreEnqueteUseCase {
    Resultat resoudre(Map<String, Set<String>> variables, List<Indice> indices);
}