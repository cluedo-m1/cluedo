package fr.miage.cluedo.joueur.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dénombre les scénarios compatibles avec un ensemble de clauses par énumération à retour arrière.
 * Algorithme décrit dans doc/features/v0.1.0-F4-filtrage-hypotheses.md, section 3.
 */
public final class SolveurFiltrageBacktracking {

    /**
     * Filtre les scénarios d'une enquête selon les clauses actives et l'hypothèse du joueur.
     *
     * @param enquete les variables (dans l'ordre du parcours) et leurs domaines
     * @param clausesActives les clauses des indices activés par le joueur
     * @param hypothese l'hypothèse partielle ou complète formulée par le joueur
     * @return le total, le comptage par valeur et le nombre de nœuds visités
     * @throws EnqueteInvalideException si un paramètre est nul ou si une clause est incohérente avec l'enquête
     */
    public ResultatFiltrage filtrer(
            EnqueteFiltrage enquete,
            List<ContrainteLogique> clausesActives,
            HypotheseJoueur hypothese) {
        if (enquete == null || clausesActives == null || hypothese == null) {
            throw new EnqueteInvalideException(
                    "L'enquête, les clauses actives et l'hypothèse ne doivent pas être nulles.");
        }

        List<ContrainteLogique> toutesLesClauses =
                fusionnerEtValiderClauses(enquete, clausesActives, hypothese);
        List<List<ContrainteLogique>> clausesParProfondeur =
                indexerParProfondeur(enquete.variables(), toutesLesClauses);
        Map<VariableId, Map<ValeurId, Long>> compteurs = initialiserCompteurs(enquete.variables());

        Exploration exploration = new Exploration(enquete.variables(), clausesParProfondeur, compteurs);
        exploration.explorer(0);

        return new ResultatFiltrage(
                exploration.totalSolutions,
                compteurs,
                exploration.noeudsVisites);
    }

    private static List<ContrainteLogique> fusionnerEtValiderClauses(
            EnqueteFiltrage enquete,
            List<ContrainteLogique> clausesActives,
            HypotheseJoueur hypothese) {
        List<ContrainteLogique> clausesHypothese = hypothese.enClauses();
        List<ContrainteLogique> clauses =
                new ArrayList<>(clausesActives.size() + clausesHypothese.size());

        for (ContrainteLogique clause : clausesActives) {
            if (clause == null) {
                throw new EnqueteInvalideException("Une clause active ne peut pas être nulle.");
            }
            enquete.verifierCoherence(clause);
            clauses.add(clause);
        }
        for (ContrainteLogique clauseHypothese : clausesHypothese) {
            enquete.verifierCoherence(clauseHypothese);
            clauses.add(clauseHypothese);
        }
        return clauses;
    }

    private static List<List<ContrainteLogique>> indexerParProfondeur(
            List<VariableEnquete> variables, List<ContrainteLogique> clauses) {
        int n = variables.size();
        Map<VariableId, Integer> profondeurParVariable = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            profondeurParVariable.put(variables.get(i).id(), i);
        }

        List<List<ContrainteLogique>> parProfondeur = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            parProfondeur.add(new ArrayList<>());
        }

        for (ContrainteLogique clause : clauses) {
            int profondeurMax = -1;
            for (Litteral litteral : clause.litteraux()) {
                int profondeur = profondeurParVariable.get(litteral.variable());
                if (profondeur > profondeurMax) {
                    profondeurMax = profondeur;
                }
            }
            parProfondeur.get(profondeurMax).add(clause);
        }
        return parProfondeur;
    }

    private static Map<VariableId, Map<ValeurId, Long>> initialiserCompteurs(
            List<VariableEnquete> variables) {
        Map<VariableId, Map<ValeurId, Long>> compteurs = new LinkedHashMap<>();
        for (VariableEnquete variable : variables) {
            Map<ValeurId, Long> compteursVariable = new LinkedHashMap<>();
            for (ValeurId valeur : variable.domaine()) {
                compteursVariable.put(valeur, 0L);
            }
            compteurs.put(variable.id(), compteursVariable);
        }
        return compteurs;
    }

    private static final class Exploration {
        private final List<VariableEnquete> variables;
        private final List<List<ContrainteLogique>> clausesParProfondeur;
        private final Map<VariableId, Map<ValeurId, Long>> compteurs;
        private final Map<VariableId, ValeurId> assignation;
        private long totalSolutions;
        private long noeudsVisites;

        private Exploration(
                List<VariableEnquete> variables,
                List<List<ContrainteLogique>> clausesParProfondeur,
                Map<VariableId, Map<ValeurId, Long>> compteurs) {
            this.variables = variables;
            this.clausesParProfondeur = clausesParProfondeur;
            this.compteurs = compteurs;
            this.assignation = new HashMap<>(variables.size());
        }

        private void explorer(int profondeur) {
            if (profondeur == variables.size()) {
                enregistrerSolution();
                return;
            }

            VariableEnquete variableCourante = variables.get(profondeur);
            List<ContrainteLogique> clausesDuNiveau = clausesParProfondeur.get(profondeur);

            for (ValeurId candidate : variableCourante.domaine()) {
                noeudsVisites++;
                assignation.put(variableCourante.id(), candidate);

                if (sontToutesSatisfaites(clausesDuNiveau)) {
                    explorer(profondeur + 1);
                }
            }
            assignation.remove(variableCourante.id());
        }

        private void enregistrerSolution() {
            totalSolutions++;
            for (VariableEnquete variable : variables) {
                ValeurId valeurRetenue = assignation.get(variable.id());
                compteurs.get(variable.id()).merge(valeurRetenue, 1L, Long::sum);
            }
        }

        private boolean sontToutesSatisfaites(List<ContrainteLogique> clauses) {
            for (ContrainteLogique clause : clauses) {
                if (!estClauseSatisfaite(clause)) {
                    return false;
                }
            }
            return true;
        }

        private boolean estClauseSatisfaite(ContrainteLogique clause) {
            for (Litteral litteral : clause.litteraux()) {
                if (litteral.estVrai(assignation.get(litteral.variable()))) {
                    return true;
                }
            }
            return false;
        }
    }
}