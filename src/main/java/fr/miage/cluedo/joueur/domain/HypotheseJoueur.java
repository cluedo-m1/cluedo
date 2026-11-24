package fr.miage.cluedo.joueur.domain;

import java.util.List;
import java.util.Map;

/** Assignation partielle choisie par le joueur : de 0 à n variables fixées à une valeur. */
public record HypotheseJoueur(Map<VariableId, ValeurId> valeursFixees) {

    public HypotheseJoueur {
        valeursFixees = valeursFixees == null ? Map.of() : Map.copyOf(valeursFixees);
    }

    public static HypotheseJoueur aucune() {
        return new HypotheseJoueur(Map.of());
    }

    /** Chaque variable fixée devient une clause unitaire positive « variable = valeur ». */
    public List<ContrainteLogique> enClauses() {
        return valeursFixees.entrySet().stream()
                .map(e -> new ContrainteLogique(List.of(new Litteral(e.getKey(), e.getValue(), true))))
                .toList();
    }
}
