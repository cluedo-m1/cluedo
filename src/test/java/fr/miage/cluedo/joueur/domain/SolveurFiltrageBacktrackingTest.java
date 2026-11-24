package fr.miage.cluedo.joueur.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SolveurFiltrageBacktrackingTest {

    private static final VariableId SUSPECT = new VariableId("SUSPECT");
    private static final VariableId LIEU = new VariableId("LIEU");
    private static final VariableId OBJET = new VariableId("OBJET");

    private static final ValeurId A = new ValeurId("A");
    private static final ValeurId B = new ValeurId("B");
    private static final ValeurId C = new ValeurId("C");
    private static final ValeurId X = new ValeurId("X");
    private static final ValeurId Y = new ValeurId("Y");
    private static final ValeurId UN = new ValeurId("1");
    private static final ValeurId DEUX = new ValeurId("2");

    private final SolveurFiltrageBacktracking solveur = new SolveurFiltrageBacktracking();

    // =========================================================================
    // A. Cas chiffrés
    // =========================================================================

    @Test
    void cas1_sans_contrainte_2x2x2_explore_tout_arbre() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();

        ResultatFiltrage res = solveur.filtrer(enquete, List.of(), new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(8L);
        assertThat(res.noeudsVisites()).isEqualTo(14L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 4L).containsEntry(B, 4L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 4L).containsEntry(Y, 4L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 4L).containsEntry(DEUX, 4L);
    }

    @Test
    void cas2_reference_fiche_exclusion_et_implication() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        List<ContrainteLogique> clauses = clausesReference();

        ResultatFiltrage res = solveur.filtrer(enquete, clauses, new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(3L);
        assertThat(res.noeudsVisites()).isEqualTo(8L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 0L).containsEntry(B, 3L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 1L).containsEntry(Y, 2L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 1L).containsEntry(DEUX, 2L);
    }

    @Test
    void cas3_reference_avec_hypothese_sur_derniere_variable() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        HypotheseJoueur hypothese = new HypotheseJoueur(Map.of(OBJET, DEUX));

        ResultatFiltrage res = solveur.filtrer(enquete, clausesReference(), hypothese);

        assertThat(res.totalScenariosCompatibles()).isEqualTo(2L);
        assertThat(res.noeudsVisites()).isEqualTo(8L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 0L).containsEntry(B, 2L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 1L).containsEntry(Y, 1L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 0L).containsEntry(DEUX, 2L);
    }

    @Test
    void cas4_reference_avec_hypothese_sur_premiere_variable() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        HypotheseJoueur hypothese = new HypotheseJoueur(Map.of(SUSPECT, B));

        ResultatFiltrage res = solveur.filtrer(enquete, clausesReference(), hypothese);

        assertThat(res.totalScenariosCompatibles()).isEqualTo(3L);
        assertThat(res.noeudsVisites()).isEqualTo(8L);
        assertThat(res.noeudsVisites()).isLessThan(14L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 0L).containsEntry(B, 3L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 1L).containsEntry(Y, 2L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 1L).containsEntry(DEUX, 2L);
    }

    @Test
    void cas5_hypothese_complete_valide() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        HypotheseJoueur hypothese = new HypotheseJoueur(Map.of(SUSPECT, B, LIEU, Y, OBJET, UN));

        ResultatFiltrage res = solveur.filtrer(enquete, clausesReference(), hypothese);

        assertThat(res.totalScenariosCompatibles()).isEqualTo(1L);
        assertThat(res.noeudsVisites()).isEqualTo(6L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 0L).containsEntry(B, 1L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 0L).containsEntry(Y, 1L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 1L).containsEntry(DEUX, 0L);
    }

    @Test
    void cas6_contradiction_totale_retourne_zero_partout() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        List<ContrainteLogique> clauses = List.of(
                new ContrainteLogique(List.of(new Litteral(SUSPECT, A, true))),
                new ContrainteLogique(List.of(new Litteral(SUSPECT, A, false))));

        ResultatFiltrage res = solveur.filtrer(enquete, clauses, new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isZero();
        assertThat(res.noeudsVisites()).isEqualTo(2L);
        res.comptageParVariable().values().forEach(compteurs ->
                assertThat(compteurs.values()).containsOnly(0L));
    }

    @Test
    void cas7_une_variable_trois_valeurs_avec_exclusion() {
        EnqueteFiltrage enquete = new EnqueteBuilder()
                .variable(SUSPECT, A, B, C)
                .construire();
        List<ContrainteLogique> clauses = List.of(
                new ContrainteLogique(List.of(new Litteral(SUSPECT, B, false))));

        ResultatFiltrage res = solveur.filtrer(enquete, clauses, new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(2L);
        assertThat(res.noeudsVisites()).isEqualTo(3L);
        assertThat(res.comptageParVariable().get(SUSPECT))
                .containsEntry(A, 1L)
                .containsEntry(B, 0L)
                .containsEntry(C, 1L);
    }

    @Test
    void cas8_domaine_taille_1_exclu_donne_zero_scenario() {
        EnqueteFiltrage enquete = new EnqueteBuilder()
                .variable(SUSPECT, A)
                .construire();
        List<ContrainteLogique> clauses = List.of(
                new ContrainteLogique(List.of(new Litteral(SUSPECT, A, false))));

        ResultatFiltrage res = solveur.filtrer(enquete, clauses, new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isZero();
        assertThat(res.noeudsVisites()).isEqualTo(1L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 0L);
    }

    // =========================================================================
    // B. Cas limites
    // =========================================================================

    @Test
    void cas9_dimension_n1_et_n4() {
        EnqueteFiltrage enqueteN1 = new EnqueteBuilder()
                .variable(SUSPECT, A, B)
                .construire();
        ResultatFiltrage resN1 = solveur.filtrer(enqueteN1, List.of(), new HypotheseJoueur(Map.of()));
        assertThat(resN1.totalScenariosCompatibles()).isEqualTo(2L);
        assertThat(resN1.noeudsVisites()).isEqualTo(2L);

        VariableId creneau = new VariableId("CRENEAU");
        EnqueteFiltrage enqueteN4 = new EnqueteBuilder()
                .variable(SUSPECT, A, B)
                .variable(LIEU, X, Y)
                .variable(OBJET, UN, DEUX)
                .variable(creneau, new ValeurId("MATIN"), new ValeurId("SOIR"))
                .construire();
        ResultatFiltrage resN4 = solveur.filtrer(enqueteN4, List.of(), new HypotheseJoueur(Map.of()));
        assertThat(resN4.totalScenariosCompatibles()).isEqualTo(16L);
        assertThat(resN4.noeudsVisites()).isEqualTo(30L);
    }

    @Test
    void cas10_clause_tautologique_ne_change_rien() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        ContrainteLogique tautologie = new ContrainteLogique(List.of(
                new Litteral(LIEU, X, true),
                new Litteral(LIEU, X, false)));

        ResultatFiltrage sans = solveur.filtrer(enquete, List.of(), new HypotheseJoueur(Map.of()));
        ResultatFiltrage avec = solveur.filtrer(enquete, List.of(tautologie), new HypotheseJoueur(Map.of()));

        assertThat(avec.totalScenariosCompatibles()).isEqualTo(sans.totalScenariosCompatibles());
        assertThat(avec.comptageParVariable()).isEqualTo(sans.comptageParVariable());
        assertThat(avec.noeudsVisites()).isEqualTo(sans.noeudsVisites());
    }

    @Test
    void cas11_clause_avec_deux_litteraux_sur_meme_variable() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        // Deux littéraux sur OBJET (profondeur 2) : (OBJET != 1) OU (OBJET != 1)
        ContrainteLogique clauseDouble = new ContrainteLogique(List.of(
                new Litteral(OBJET, UN, false),
                new Litteral(OBJET, UN, false)));

        ResultatFiltrage res = solveur.filtrer(enquete, List.of(clauseDouble), new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(4L);
        assertThat(res.noeudsVisites()).isEqualTo(14L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 0L).containsEntry(DEUX, 4L);
    }

    @Test
    void cas12_clause_sur_derniere_variable_seulement_aucun_elagage() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        List<ContrainteLogique> clauses = List.of(
                new ContrainteLogique(List.of(new Litteral(OBJET, UN, false))));

        ResultatFiltrage res = solveur.filtrer(enquete, clauses, new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(4L);
        assertThat(res.noeudsVisites()).isEqualTo(14L);
    }

    @Test
    void cas13_liste_vide_et_clause_dupliquee() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        ContrainteLogique exclA = new ContrainteLogique(List.of(new Litteral(SUSPECT, A, false)));

        ResultatFiltrage simple = solveur.filtrer(enquete, List.of(exclA), new HypotheseJoueur(Map.of()));
        ResultatFiltrage dupliquee = solveur.filtrer(enquete, List.of(exclA, exclA), new HypotheseJoueur(Map.of()));

        assertThat(dupliquee.totalScenariosCompatibles()).isEqualTo(simple.totalScenariosCompatibles());
        assertThat(dupliquee.comptageParVariable()).isEqualTo(simple.comptageParVariable());
        assertThat(dupliquee.noeudsVisites()).isEqualTo(simple.noeudsVisites());
    }

    // =========================================================================
    // C. Erreurs (fail fast) et immuabilité
    // =========================================================================

    @Test
    void cas14_parametres_null_levent_exception() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        HypotheseJoueur vide = new HypotheseJoueur(Map.of());

        assertThatThrownBy(() -> solveur.filtrer(null, List.of(), vide))
                .isInstanceOf(EnqueteInvalideException.class);
        assertThatThrownBy(() -> solveur.filtrer(enquete, null, vide))
                .isInstanceOf(EnqueteInvalideException.class);
        assertThatThrownBy(() -> solveur.filtrer(enquete, List.of(), null))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void cas15_variable_inconnue_ou_valeur_hors_domaine_levent_exception() {
        EnqueteFiltrage enquete = enqueteStandard2x2x2();
        VariableId inconnue = new VariableId("METEO");
        ValeurId horsDomaine = new ValeurId("Z");

        ContrainteLogique clauseVarInconnue = new ContrainteLogique(List.of(new Litteral(inconnue, A, true)));
        ContrainteLogique clauseValHorsDomaine = new ContrainteLogique(List.of(new Litteral(SUSPECT, horsDomaine, true)));

        assertThatThrownBy(() -> solveur.filtrer(enquete, List.of(clauseVarInconnue), new HypotheseJoueur(Map.of())))
                .isInstanceOf(EnqueteInvalideException.class);
        assertThatThrownBy(() -> solveur.filtrer(enquete, List.of(clauseValHorsDomaine), new HypotheseJoueur(Map.of())))
                .isInstanceOf(EnqueteInvalideException.class);
        assertThatThrownBy(() -> solveur.filtrer(enquete, List.of(), new HypotheseJoueur(Map.of(inconnue, A))))
                .isInstanceOf(EnqueteInvalideException.class);
        assertThatThrownBy(() -> solveur.filtrer(enquete, List.of(), new HypotheseJoueur(Map.of(SUSPECT, horsDomaine))))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void cas16_resultat_est_immuable() {
        ResultatFiltrage res = solveur.filtrer(enqueteStandard2x2x2(), List.of(), new HypotheseJoueur(Map.of()));
        Map<VariableId, Map<ValeurId, Long>> comptages = res.comptageParVariable();
        Map<ValeurId, Long> comptagesSuspect = comptages.get(SUSPECT);

        assertThatThrownBy(() -> comptages.put(new VariableId("X"), Map.of()))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> comptagesSuspect.put(A, 99L))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // D. Propriétés sur données aléatoires (graine fixe)
    // =========================================================================

    @Test
    void cas17_a_22_proprietes_sur_instances_aleatoires_n4() {
        for (long graine = 1L; graine <= 30L; graine++) {
            Random alea = new Random(graine);
            EnqueteFiltrage enquete = genererEnqueteAleatoire(alea, 4);
            List<ContrainteLogique> clauses = genererClausesAleatoires(alea, enquete.variables(), alea.nextInt(7));

            ResultatFiltrage res = solveur.filtrer(enquete, clauses, new HypotheseJoueur(Map.of()));

            // #17 Oracle naïf (produit cartésien sans appel à Litteral.estVrai)
            ResultatOracle oracle = calculerOracleNaif(enquete, clauses);
            assertThat(res.totalScenariosCompatibles())
                    .as("Graine %d : total différent de l'oracle", graine)
                    .isEqualTo(oracle.total);
            assertThat(res.comptageParVariable())
                    .as("Graine %d : comptages différents de l'oracle", graine)
                    .isEqualTo(oracle.compteurs);

            // #18 Conservation des comptages
            for (VariableEnquete variable : enquete.variables()) {
                long sommeVariable = res.comptageParVariable().get(variable.id()).values().stream()
                        .mapToLong(Long::longValue)
                        .sum();
                assertThat(sommeVariable)
                        .as("Graine %d, var %s : somme compteurs != total", graine, variable.id().code())
                        .isEqualTo(res.totalScenariosCompatibles());
            }

            // #19 Bornes du nombre de nœuds visités
            long noeudsArbreComplet = calculerNoeudsArbreComplet(enquete.variables());
            assertThat(res.noeudsVisites())
                    .as("Graine %d : bornes noeudsVisites", graine)
                    .isBetween(0L, noeudsArbreComplet);

            // #20 Monotonie : ajouter une clause ne fait jamais augmenter le total ni les compteurs
            ContrainteLogique clauseSupplementaire = genererClausesAleatoires(alea, enquete.variables(), 1).getFirst();
            List<ContrainteLogique> clausesEtendues = new ArrayList<>(clauses);
            clausesEtendues.add(clauseSupplementaire);
            ResultatFiltrage resApresAjout = solveur.filtrer(enquete, clausesEtendues, new HypotheseJoueur(Map.of()));

            assertThat(resApresAjout.totalScenariosCompatibles())
                    .isLessThanOrEqualTo(res.totalScenariosCompatibles());
            for (VariableEnquete variable : enquete.variables()) {
                for (ValeurId valeur : variable.domaine()) {
                    assertThat(resApresAjout.comptageParVariable().get(variable.id()).get(valeur))
                            .isLessThanOrEqualTo(res.comptageParVariable().get(variable.id()).get(valeur));
                }
            }

            // #21 Indépendance de l'ordre des clauses
            List<ContrainteLogique> clausesMelangees = new ArrayList<>(clauses);
            Collections.shuffle(clausesMelangees, new Random(graine + 1000L));
            ResultatFiltrage resMelange = solveur.filtrer(enquete, clausesMelangees, new HypotheseJoueur(Map.of()));

            assertThat(resMelange.totalScenariosCompatibles()).isEqualTo(res.totalScenariosCompatibles());
            assertThat(resMelange.comptageParVariable()).isEqualTo(res.comptageParVariable());
            assertThat(resMelange.noeudsVisites()).isEqualTo(res.noeudsVisites());

            // #22 Équivalence hypothèse = clause unitaire positive
            VariableEnquete varChoisie = enquete.variables().get(alea.nextInt(enquete.variables().size()));
            ValeurId valChoisie = varChoisie.domaine().get(alea.nextInt(varChoisie.domaine().size()));

            ResultatFiltrage viaHypothese = solveur.filtrer(
                    enquete, clauses, new HypotheseJoueur(Map.of(varChoisie.id(), valChoisie)));

            List<ContrainteLogique> avecClauseUnitaire = new ArrayList<>(clauses);
            avecClauseUnitaire.add(new ContrainteLogique(List.of(new Litteral(varChoisie.id(), valChoisie, true))));
            ResultatFiltrage viaClause = solveur.filtrer(
                    enquete, avecClauseUnitaire, new HypotheseJoueur(Map.of()));

            assertThat(viaHypothese.totalScenariosCompatibles()).isEqualTo(viaClause.totalScenariosCompatibles());
            assertThat(viaHypothese.comptageParVariable()).isEqualTo(viaClause.comptageParVariable());
        }
    }

    // =========================================================================
    // Outils de test (Builder & Oracle indépendant)
    // =========================================================================

    private static EnqueteFiltrage enqueteStandard2x2x2() {
        return new EnqueteBuilder()
                .variable(SUSPECT, A, B)
                .variable(LIEU, X, Y)
                .variable(OBJET, UN, DEUX)
                .construire();
    }

    private static List<ContrainteLogique> clausesReference() {
        // suspect != A  ET  (lieu = X => objet != 1) <=> (lieu != X OU objet != 1)
        return List.of(
                new ContrainteLogique(List.of(new Litteral(SUSPECT, A, false))),
                new ContrainteLogique(List.of(
                        new Litteral(LIEU, X, false),
                        new Litteral(OBJET, UN, false))));
    }

    private static EnqueteFiltrage genererEnqueteAleatoire(Random alea, int nbVariables) {
        EnqueteBuilder builder = new EnqueteBuilder();
        for (int i = 0; i < nbVariables; i++) {
            VariableId varId = new VariableId("V" + i);
            int tailleDomaine = 2 + alea.nextInt(3); // 2 à 4 valeurs
            ValeurId[] valeurs = new ValeurId[tailleDomaine];
            for (int j = 0; j < tailleDomaine; j++) {
                valeurs[j] = new ValeurId("V" + i + "_VAL" + j);
            }
            builder.variable(varId, valeurs);
        }
        return builder.construire();
    }

    private static List<ContrainteLogique> genererClausesAleatoires(
            Random alea, List<VariableEnquete> variables, int nbClauses) {
        List<ContrainteLogique> clauses = new ArrayList<>(nbClauses);
        for (int c = 0; c < nbClauses; c++) {
            int nbLitteraux = 1 + alea.nextInt(3); // 1 à 3 littéraux
            List<Litteral> litteraux = new ArrayList<>(nbLitteraux);
            for (int l = 0; l < nbLitteraux; l++) {
                VariableEnquete var = variables.get(alea.nextInt(variables.size()));
                ValeurId val = var.domaine().get(alea.nextInt(var.domaine().size()));
                boolean positif = alea.nextBoolean();
                litteraux.add(new Litteral(var.id(), val, positif));
            }
            clauses.add(new ContrainteLogique(litteraux));
        }
        return clauses;
    }

    private static long calculerNoeudsArbreComplet(List<VariableEnquete> variables) {
        long total = 0L;
        long produitNiveau = 1L;
        for (VariableEnquete variable : variables) {
            produitNiveau *= variable.domaine().size();
            total += produitNiveau;
        }
        return total;
    }

    private record ResultatOracle(long total, Map<VariableId, Map<ValeurId, Long>> compteurs) {}

    private static ResultatOracle calculerOracleNaif(
            EnqueteFiltrage enquete, List<ContrainteLogique> clauses) {
        List<VariableEnquete> vars = enquete.variables();
        Map<VariableId, Map<ValeurId, Long>> compteurs = new LinkedHashMap<>();
        for (VariableEnquete var : vars) {
            Map<ValeurId, Long> mapVar = new LinkedHashMap<>();
            for (ValeurId val : var.domaine()) {
                mapVar.put(val, 0L);
            }
            compteurs.put(var.id(), mapVar);
        }

        long[] total = {0L};
        Map<VariableId, ValeurId> tuple = new HashMap<>();
        enumererProduitCartesien(vars, 0, tuple, clauses, compteurs, total);
        return new ResultatOracle(total[0], compteurs);
    }

    private static void enumererProduitCartesien(
            List<VariableEnquete> vars,
            int index,
            Map<VariableId, ValeurId> tuple,
            List<ContrainteLogique> clauses,
            Map<VariableId, Map<ValeurId, Long>> compteurs,
            long[] total) {
        if (index == vars.size()) {
            if (satisfaitToutesSansDomaine(clauses, tuple)) {
                total[0]++;
                for (VariableEnquete var : vars) {
                    ValeurId choisie = tuple.get(var.id());
                    compteurs.get(var.id()).merge(choisie, 1L, Long::sum);
                }
            }
            return;
        }
        VariableEnquete courante = vars.get(index);
        for (ValeurId val : courante.domaine()) {
            tuple.put(courante.id(), val);
            enumererProduitCartesien(vars, index + 1, tuple, clauses, compteurs, total);
        }
    }

    private static boolean satisfaitToutesSansDomaine(
            List<ContrainteLogique> clauses, Map<VariableId, ValeurId> tuple) {
        for (ContrainteLogique clause : clauses) {
            boolean clauseVraie = false;
            for (Litteral lit : clause.litteraux()) {
                ValeurId assignee = tuple.get(lit.variable());
                boolean egal = assignee.equals(lit.valeur());
                boolean litVrai = lit.positif() ? egal : !egal;
                if (litVrai) {
                    clauseVraie = true;
                    break;
                }
            }
            if (!clauseVraie) {
                return false;
            }
        }
        return true;
    }

    private static final class EnqueteBuilder {
        private final List<VariableEnquete> variables = new ArrayList<>();

        private EnqueteBuilder variable(VariableId id, ValeurId... valeurs) {
            variables.add(new VariableEnquete(id, List.of(valeurs)));
            return this;
        }

        private EnqueteFiltrage construire() {
            return new EnqueteFiltrage(1L, "Enquête de test", variables, List.of());
        }
    }
}