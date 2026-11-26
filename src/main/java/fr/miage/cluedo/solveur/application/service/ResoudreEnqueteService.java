package fr.miage.cluedo.solveur.application.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.miage.cluedo.solveur.application.port.in.ResoudreEnqueteUseCase;
import fr.miage.cluedo.solveur.domain.algo.RetourArriere;
import fr.miage.cluedo.solveur.domain.model.Contrainte;
import fr.miage.cluedo.solveur.domain.model.Exclusion;
import fr.miage.cluedo.solveur.domain.model.Implication;
import fr.miage.cluedo.solveur.domain.model.Indice;
import fr.miage.cluedo.solveur.domain.model.Resultat;
import fr.miage.cluedo.solveur.domain.model.Variable;

public class ResoudreEnqueteService implements ResoudreEnqueteUseCase {

    @Override
    public Resultat resoudre(Map<String, Set<String>> domaines, List<Indice> indices) {
        Map<String, Variable> variables = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> entree : domaines.entrySet()) {
            variables.put(entree.getKey(), new Variable(entree.getKey(), entree.getValue()));
        }

        List<Contrainte> contraintes = new ArrayList<>();
        for (Indice indice : indices) {
            if (indice instanceof Exclusion e) {
                variables.get(e.variable()).retirer(e.valeur());
            } else if (indice instanceof Implication i) {
                contraintes.add(new Contrainte(
                        variables.get(i.variableX()), // la variable X (ex. Coupable)
                        variables.get(i.variableY()), // la variable Y (ex. Lieu)
                        (x, y) -> !x.equals(i.valeurX()) || i.valeursAutoriseesY().contains(y),
                        i.variableX() + " = " + i.valeurX() + " impose " + i.variableY() + " dans "
                                + i.valeursAutoriseesY()));
            }
        }

        int nb = RetourArriere.compterSolutions(new ArrayList<>(variables.values()), contraintes);

        Map<String, Set<String>> restants = new LinkedHashMap<>();
        for (Map.Entry<String, Variable> entree : variables.entrySet()) {
            restants.put(entree.getKey(), new LinkedHashSet<>(entree.getValue().getDomaine()));
        }
        return new Resultat(nb, restants);
    }
}