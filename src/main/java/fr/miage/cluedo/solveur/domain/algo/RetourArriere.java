package fr.miage.cluedo.solveur.domain.algo;

import fr.miage.cluedo.solveur.domain.model.Contrainte;
import fr.miage.cluedo.solveur.domain.model.Variable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RetourArriere {

    private RetourArriere() {
    }

    /**
     * Compte les solutions en s'arrêtant à 2 : le résultat vaut 0, 1 ou 2 («
     * plusieurs »).
     */
    public static int compterSolutions(List<Variable> variables, List<Contrainte> contraintes) {
        if (!Ac3.ac3(contraintes)) {
            return 0; // enquête incohérente
        }
        for (Variable v : variables) {
            if (v.estVide()) {
                return 0; // un domaine vide : aucune solution
            }
        }

        Variable choisie = null;
        for (Variable v : variables) {
            if (v.taille() > 1) {
                choisie = v;
                break;
            }
        }
        if (choisie == null) {
            return 1; // tous les domaines sont des singletons : une solution complète
        }
        // TODO 1 : trouver une variable dont le domaine a plus d'une valeur.
        // S'il n'y en a aucune, tous les domaines sont des singletons : renvoyer 1.

        int total = 0;
        for (String valeur : new ArrayList<>(choisie.getDomaine())) {

            // 1. sauvegarder le domaine de TOUTES les variables
            Map<Variable, Set<String>> sauvegardes = new LinkedHashMap<>();
            for (Variable v : variables) {
                sauvegardes.put(v, v.sauvegarder());
            }

            // 2. fixer choisie à cette valeur
            choisie.fixer(valeur);

            // 3. compter les solutions avec cette valeur
            total += compterSolutions(variables, contraintes);
            /* TROU 2 : l'appel récursif */;

            // 4. annuler : remettre chaque domaine comme avant
            for (Variable v : variables) {
                v.restaurer(sauvegardes.get(v));
            }

            // 5. inutile de continuer si on a déjà « plusieurs » solutions
            if (total >= 2) {
                break;
            }
        }
        return total;
    }
}