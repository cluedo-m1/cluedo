package fr.miage.cluedo.joueur.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.miage.cluedo.joueur.domain.ContrainteLogique;
import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;
import fr.miage.cluedo.joueur.domain.HypotheseJoueur;
import fr.miage.cluedo.joueur.domain.IndiceEnquete;
import fr.miage.cluedo.joueur.domain.Litteral;
import fr.miage.cluedo.joueur.domain.ResultatFiltrage;
import fr.miage.cluedo.joueur.domain.SolveurFiltrageBacktracking;
import fr.miage.cluedo.joueur.domain.ValeurId;
import fr.miage.cluedo.joueur.domain.VariableEnquete;
import fr.miage.cluedo.joueur.domain.VariableId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("demo")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:joueur_persistence_it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
@Import(EnquetePersistenceAdapter.class)
class EnquetePersistenceAdapterIT {

    @Autowired
    private EnquetePersistenceAdapter adapter;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void p1_charge_enquete_demo_avec_3_variables_et_5_indices() {
        EnqueteFiltrage enquete = adapter.trouverParId(1L).orElseThrow();

        assertThat(enquete.titre()).isEqualTo("Fuite du sujet d'examen de POO");
        assertThat(enquete.variables())
                .extracting(v -> v.id().code())
                .containsExactly("SUSPECT", "LIEU", "OBJET");
        assertThat(enquete.indices()).hasSize(5);
    }

    @Test
    void p2_respecte_la_colonne_ordre_meme_inversee_par_rapport_aux_ids() {
        jdbc.update("INSERT INTO joueur_enquete (id, titre) VALUES (?, ?)", 50L, "Enquête ordre inversé");
        // id=100 a ordre=2, id=200 a ordre=1 => l'ordre retourné doit être VAR_PREMIERE puis VAR_SECONDE
        jdbc.update(
                "INSERT INTO joueur_variable (id, enquete_id, code, ordre) VALUES (?, ?, ?, ?)",
                100L, 50L, "VAR_SECONDE", 2);
        jdbc.update(
                "INSERT INTO joueur_variable (id, enquete_id, code, ordre) VALUES (?, ?, ?, ?)",
                200L, 50L, "VAR_PREMIERE", 1);

        // Valeurs de VAR_PREMIERE (id=200) avec ids inversés par rapport à ordre
        jdbc.update(
                "INSERT INTO joueur_valeur_domaine (id, variable_id, code, ordre) VALUES (?, ?, ?, ?)",
                300L, 200L, "VAL_B", 2);
        jdbc.update(
                "INSERT INTO joueur_valeur_domaine (id, variable_id, code, ordre) VALUES (?, ?, ?, ?)",
                400L, 200L, "VAL_A", 1);
        jdbc.update(
                "INSERT INTO joueur_valeur_domaine (id, variable_id, code, ordre) VALUES (?, ?, ?, ?)",
                500L, 100L, "VAL_UNIQUE", 1);

        EnqueteFiltrage enquete = adapter.trouverParId(50L).orElseThrow();

        assertThat(enquete.variables())
                .extracting(v -> v.id().code())
                .containsExactly("VAR_PREMIERE", "VAR_SECONDE");
        assertThat(enquete.variables().getFirst().domaine())
                .extracting(ValeurId::code)
                .containsExactly("VAL_A", "VAL_B");
    }

    @Test
    void p3_aller_retour_des_litteraux_et_polarites() {
        EnqueteFiltrage enquete = adapter.trouverParId(1L).orElseThrow();
        Map<Long, IndiceEnquete> parId = enquete.indices().stream()
                .collect(Collectors.toMap(IndiceEnquete::id, i -> i));

        // Indice 1 : 1 littéral négatif
        List<Litteral> lits1 = parId.get(1L).contrainte().litteraux();
        assertThat(lits1).hasSize(1);
        assertThat(lits1.getFirst().positif()).isFalse();

        // Indice 3 : 2 littéraux positifs
        List<Litteral> lits3 = parId.get(3L).contrainte().litteraux();
        assertThat(lits3).hasSize(2);
        assertThat(lits3).allMatch(Litteral::positif);

        // Indice 4 : 2 littéraux négatifs
        List<Litteral> lits4 = parId.get(4L).contrainte().litteraux();
        assertThat(lits4).hasSize(2);
        assertThat(lits4).noneMatch(Litteral::positif);
    }

    @Test
    void p4_enquete_inexistante_retourne_empty() {
        assertThat(adapter.trouverParId(999L)).isEmpty();
    }

    @Test
    void p5_cle_composite_refuse_valeur_hors_du_domaine_de_la_variable() {
        Long variableSuspectId = jdbc.queryForObject(
                "SELECT id FROM joueur_variable WHERE enquete_id = 1 AND code = 'SUSPECT'", Long.class);
        Long valeurLieuId = jdbc.queryForObject(
                "SELECT vd.id FROM joueur_valeur_domaine vd JOIN joueur_variable v ON vd.variable_id = v.id "
                        + "WHERE v.enquete_id = 1 AND v.code = 'LIEU' FETCH FIRST 1 ROW ONLY",
                Long.class);
        Long indiceId = jdbc.queryForObject(
                "SELECT id FROM joueur_indice WHERE enquete_id = 1 FETCH FIRST 1 ROW ONLY", Long.class);

        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO joueur_litteral (id, indice_id, variable_id, valeur_id, positif) VALUES (?, ?, ?, ?, ?)",
                9999L, indiceId, variableSuspectId, valeurLieuId, true))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void p6_contraintes_unicite_et_ordre_positif_en_base() {
        // Doublon (enquete_id, ordre) sur joueur_variable (ordre 1 existe déjà sur enquête 1)
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO joueur_variable (id, enquete_id, code, ordre) VALUES (?, ?, ?, ?)",
                9991L, 1L, "DOUBLON_ORDRE", 1))
                .isInstanceOf(DataIntegrityViolationException.class);

        Long variableSuspectId = jdbc.queryForObject(
                "SELECT id FROM joueur_variable WHERE enquete_id = 1 AND code = 'SUSPECT'", Long.class);
        String codeExistant = jdbc.queryForObject(
                "SELECT code FROM joueur_valeur_domaine WHERE variable_id = ? FETCH FIRST 1 ROW ONLY",
                String.class,
                variableSuspectId);

        // Doublon (variable_id, code) sur joueur_valeur_domaine
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO joueur_valeur_domaine (id, variable_id, code, ordre) VALUES (?, ?, ?, ?)",
                9992L, variableSuspectId, codeExistant, 99))
                .isInstanceOf(DataIntegrityViolationException.class);

        // Ordre négatif refusé
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO joueur_variable (id, enquete_id, code, ordre) VALUES (?, ?, ?, ?)",
                9993L, 1L, "ORDRE_NEGATIF", -1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void p7_suppression_enquete_vide_les_5_tables_en_cascade() {
        jdbc.update("DELETE FROM joueur_enquete WHERE id = 1");

        Integer nbEnquetes = jdbc.queryForObject(
                "SELECT COUNT(*) FROM joueur_enquete WHERE id = 1", Integer.class);
        Integer nbVariables = jdbc.queryForObject(
                "SELECT COUNT(*) FROM joueur_variable WHERE enquete_id = 1", Integer.class);
        Integer nbValeurs = jdbc.queryForObject(
                "SELECT COUNT(*) FROM joueur_valeur_domaine WHERE variable_id NOT IN (SELECT id FROM joueur_variable)",
                Integer.class);
        Integer nbIndices = jdbc.queryForObject(
                "SELECT COUNT(*) FROM joueur_indice WHERE enquete_id = 1", Integer.class);
        Integer nbLitteraux = jdbc.queryForObject(
                "SELECT COUNT(*) FROM joueur_litteral WHERE indice_id NOT IN (SELECT id FROM joueur_indice)",
                Integer.class);

        assertThat(nbEnquetes).isZero();
        assertThat(nbVariables).isZero();
        assertThat(nbValeurs).isZero();
        assertThat(nbIndices).isZero();
        assertThat(nbLitteraux).isZero();
    }

    @Test
    void p8_bout_en_bout_adaptateur_vers_solveur_sur_5_indices_actifs() {
        EnqueteFiltrage enquete = adapter.trouverParId(1L).orElseThrow();
        Set<Long> tousLesIndices = enquete.indices().stream()
                .map(IndiceEnquete::id)
                .collect(Collectors.toSet());

        SolveurFiltrageBacktracking solveur = new SolveurFiltrageBacktracking();
        ResultatFiltrage resultat = solveur.filtrer(
                enquete,
                enquete.contraintesActives(tousLesIndices),
                new HypotheseJoueur(Map.of()));

        long attenduExhaustif = compterExhaustivement(enquete.variables(), enquete.indices());

        assertThat(resultat.totalScenariosCompatibles())
                .isEqualTo(attenduExhaustif)
                .isEqualTo(18L);
        assertThat(resultat.noeudsVisites()).isEqualTo(180L);
        for (VariableEnquete variable : enquete.variables()) {
            long somme = resultat.comptageParVariable().get(variable.id()).values().stream()
                    .mapToLong(Long::longValue)
                    .sum();
            assertThat(somme).isEqualTo(18L);
        }
    }

    private static long compterExhaustivement(
            List<VariableEnquete> variables, List<IndiceEnquete> indices) {
        long valides = 0L;
        VariableEnquete v0 = variables.get(0);
        VariableEnquete v1 = variables.get(1);
        VariableEnquete v2 = variables.get(2);

        for (ValeurId val0 : v0.domaine()) {
            for (ValeurId val1 : v1.domaine()) {
                for (ValeurId val2 : v2.domaine()) {
                    Map<VariableId, ValeurId> tuple = new HashMap<>();
                    tuple.put(v0.id(), val0);
                    tuple.put(v1.id(), val1);
                    tuple.put(v2.id(), val2);

                    if (satisfaitTousLesIndices(indices, tuple)) {
                        valides++;
                    }
                }
            }
        }
        return valides;
    }

    private static boolean satisfaitTousLesIndices(
            List<IndiceEnquete> indices, Map<VariableId, ValeurId> tuple) {
        for (IndiceEnquete indice : indices) {
            boolean clauseVraie = false;
            for (Litteral lit : indice.contrainte().litteraux()) {
                boolean egal = tuple.get(lit.variable()).equals(lit.valeur());
                if (lit.positif() ? egal : !egal) {
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

    @Test
    void p9_indice_sans_litteral_en_base_leve_illegal_state_exception() {
        jdbc.update(
                "INSERT INTO joueur_indice (id, enquete_id, libelle) VALUES (?, ?, ?)",
                999L, 1L, "Indice corrompu sans littéral");

        assertThatThrownBy(() -> adapter.trouverParId(1L))
                .isInstanceOf(IllegalStateException.class);
    }
}