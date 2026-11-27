package fr.miage.cluedo.generation.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public final class GenerateurEnquete {

    private final Long seed; // null = pas de mélange

    public GenerateurEnquete() {
        this.seed = null;
    }

    public GenerateurEnquete(long seed) {
        this.seed = seed;
    }

    public Optional<Solution> generer(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces
    ) {
        // Le Random est créé ici, à chaque appel : même seed = même mélange
        Random alea = (seed == null) ? null : new Random(seed);

        List<List<?>> domaines = List.of(
                melanger(suspects, alea),
                melanger(lieux, alea),
                melanger(horaires, alea),
                melanger(moyensAcces, alea)
        );

        Map<Integer, Object> affectation = new HashMap<>();

        return backtracking(0, domaines, affectation);
    }

    // Mélange une COPIE : la liste d'origine n'est jamais modifiée
    private <T> List<T> melanger(List<T> domaine, Random alea) {
        List<T> copie = new ArrayList<>(domaine);
        if (alea != null) {
            Collections.shuffle(copie, alea);
        }
        return copie;
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

            affectation.put(niveau, valeur);

            // 3. Vérifier les contraintes
            if (respecteContraintes(affectation)) {

                // 4. Passer au niveau suivant
                Optional<Solution> resultat = backtracking(
                        niveau + 1,
                        domaines,
                        affectation
                );

                if (resultat.isPresent()) {
                    return resultat;
                }
            }

            // 5. Retour arrière
            affectation.remove(niveau);
        }

        return Optional.empty();
    }

    private boolean respecteContraintes(
            Map<Integer, Object> affectation
    ) {

        Suspect suspect = (Suspect) affectation.get(0);
        Lieu lieu = (Lieu) affectation.get(1);
        Horaire horaire = (Horaire) affectation.get(2);
        MoyenAcces moyenAcces = (MoyenAcces) affectation.get(3);

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