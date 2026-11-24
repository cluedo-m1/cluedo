package fr.miage.cluedo.joueur.adapter.in.web;

import fr.miage.cluedo.joueur.domain.HypotheseJoueur;
import fr.miage.cluedo.joueur.domain.ValeurId;
import fr.miage.cluedo.joueur.domain.VariableId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Corps du POST de filtrage.
 *
 * @param indicesActifsIds ids des indices cochés par le joueur (liste vide autorisée, absence refusée)
 * @param hypothese        variable (code) vers valeur (code) ; absente ou vide = aucune hypothèse
 */
public record FiltrageRequete(
        @NotNull Set<@NotNull Long> indicesActifsIds,
        Map<@NotBlank String, @NotBlank String> hypothese) {

    HypotheseJoueur versHypothese() {
        if (hypothese == null) {
            return HypotheseJoueur.aucune();
        }
        Map<VariableId, ValeurId> valeursFixees = new LinkedHashMap<>();
        hypothese.forEach((variable, valeur) -> valeursFixees.put(new VariableId(variable), new ValeurId(valeur)));
        return new HypotheseJoueur(valeursFixees);
    }
}
