package fr.miage.cluedo.joueur.adapter.in.web;

import fr.miage.cluedo.joueur.domain.ResultatFiltrage;
import java.util.List;

/**
 * Résultat du filtrage. Les comptages sont des listes ordonnées (et non des objets JSON) : l'ordre des
 * variables et des valeurs est celui de l'enquête, et un objet JSON ne le garantit pas.
 */
public record FiltrageReponse(
        long totalScenariosCompatibles, long noeudsVisites, List<VariableCompteDto> comptageParVariable) {

    public record VariableCompteDto(String variable, List<ValeurCompteDto> valeurs) {
    }

    public record ValeurCompteDto(String valeur, long scenarios) {
    }

    static FiltrageReponse depuis(ResultatFiltrage resultat) {
        List<VariableCompteDto> comptages = resultat.comptageParVariable().entrySet().stream()
                .map(variable -> new VariableCompteDto(
                        variable.getKey().code(),
                        variable.getValue().entrySet().stream()
                                .map(valeur -> new ValeurCompteDto(valeur.getKey().code(), valeur.getValue()))
                                .toList()))
                .toList();
        return new FiltrageReponse(resultat.totalScenariosCompatibles(), resultat.noeudsVisites(), comptages);
    }
}
