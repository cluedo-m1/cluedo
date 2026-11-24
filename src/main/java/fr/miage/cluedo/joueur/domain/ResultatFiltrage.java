package fr.miage.cluedo.joueur.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Résultat d'un filtrage.
 *
 * @param totalScenariosCompatibles nombre de scénarios complets satisfaisant toutes les clauses
 * @param comptageParVariable       pour chaque variable et chaque valeur de son domaine, le nombre de scénarios
 *                                  compatibles qui la contiennent (0 = valeur éliminée) ; l'ordre d'itération
 *                                  est celui des variables et des domaines de l'enquête
 * @param noeudsVisites             nombre de nœuds explorés par la recherche (mesure de l'élagage)
 */
public record ResultatFiltrage(
        long totalScenariosCompatibles,
        Map<VariableId, Map<ValeurId, Long>> comptageParVariable,
        long noeudsVisites) {

    public ResultatFiltrage {
        if (totalScenariosCompatibles < 0 || noeudsVisites < 0) {
            throw new EnqueteInvalideException("Un résultat ne peut pas contenir de compteur négatif");
        }
        comptageParVariable = copieProfonde(comptageParVariable);
    }

    // Copie défensive qui préserve l'ordre d'insertion (Map.copyOf ne le préserve pas).
    private static Map<VariableId, Map<ValeurId, Long>> copieProfonde(
            Map<VariableId, Map<ValeurId, Long>> source) {
        Map<VariableId, Map<ValeurId, Long>> copie = new LinkedHashMap<>();
        source.forEach((variable, compteurs) ->
                copie.put(variable, Collections.unmodifiableMap(new LinkedHashMap<>(compteurs))));
        return Collections.unmodifiableMap(copie);
    }
}
