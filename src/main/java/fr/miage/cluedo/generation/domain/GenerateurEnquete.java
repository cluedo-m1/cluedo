package fr.miage.cluedo.generation.domain;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public final class GenerateurEnquete {

    public Optional<Solution> generer(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces
    ) {

        List<List<?>> domaines = List.of(
                suspects,
                lieux,
                horaires,
                moyensAcces
        );

        Map<Integer, Object> affectation = new HashMap<>();

        return backtracking(0, domaines, affectation);
    }

    private Optional<Solution> backtracking(
            int niveau,
            List<List<?>> domaines,
            Map<Integer, Object> affectation
    ) {

        // 1. Condition d'arrêt
        if (niveau == domaines.size()) {

            return Optional.of(new Solution(
                    (Suspect) affectation.get(0),
                    (Lieu) affectation.get(1),
                    (Horaire) affectation.get(2),
                    (MoyenAcces) affectation.get(3)
            ));
        }

        // 2. Parcourir les valeurs du domaine courant
        for (Object valeur : domaines.get(niveau)) {

            // Affecter une valeur à la variable
            affectation.put(niveau, valeur);

            // 3. Vérifier les contraintes
            if (respecteContraintes(affectation)) {

                // 4. Passer récursivement au niveau suivant
                Optional<Solution> resultat = backtracking(
                        niveau + 1,
                        domaines,
                        affectation
                );

                // Une solution valide a été trouvée
                if (resultat.isPresent()) {
                    return resultat;
                }
            }

            // 5. Retour arrière
            affectation.remove(niveau);
        }

        // Aucune solution trouvée
        return Optional.empty();
    }

    private boolean respecteContraintes(
            Map<Integer, Object> affectation
    ) {

        Suspect suspect = (Suspect) affectation.get(0);
        Lieu lieu = (Lieu) affectation.get(1);
        Horaire horaire = (Horaire) affectation.get(2);
        MoyenAcces moyenAcces =
                (MoyenAcces) affectation.get(3);

        // Contrainte 1 : accès au lieu
        if (suspect != null && lieu != null) {
            if (!Contrainte.peutAcceder(suspect, lieu)) {
                return false;
            }
        }

        // Contrainte 2 : présence du suspect
        if (suspect != null && horaire != null) {
            if (!Contrainte.estPresent(suspect, horaire)) {
                return false;
            }
        }

        // Contrainte 3 : compatibilité du moyen d'accès
        if (lieu != null && moyenAcces != null) {
            if (lieu.moyenAccesRequis() != moyenAcces) {
                return false;
            }
        }

        return true;
    }
}