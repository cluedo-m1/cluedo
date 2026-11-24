package fr.miage.cluedo.joueur.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.miage.cluedo.joueur.application.port.out.EnqueteRepositoryPort;
import fr.miage.cluedo.joueur.application.service.FiltrageHypothesesService;
import fr.miage.cluedo.joueur.domain.ContrainteLogique;
import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;
import fr.miage.cluedo.joueur.domain.EnqueteInvalideException;
import fr.miage.cluedo.joueur.domain.HypotheseJoueur;
import fr.miage.cluedo.joueur.domain.IndiceEnquete;
import fr.miage.cluedo.joueur.domain.Litteral;
import fr.miage.cluedo.joueur.domain.ResultatFiltrage;
import fr.miage.cluedo.joueur.domain.SolveurFiltrageBacktracking;
import fr.miage.cluedo.joueur.domain.ValeurId;
import fr.miage.cluedo.joueur.domain.VariableEnquete;
import fr.miage.cluedo.joueur.domain.VariableId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FiltrageHypothesesServiceTest {

    private static final VariableId SUSPECT = new VariableId("SUSPECT");
    private static final VariableId LIEU = new VariableId("LIEU");
    private static final VariableId OBJET = new VariableId("OBJET");

    private static final ValeurId A = new ValeurId("A");
    private static final ValeurId B = new ValeurId("B");
    private static final ValeurId X = new ValeurId("X");
    private static final ValeurId Y = new ValeurId("Y");
    private static final ValeurId UN = new ValeurId("1");
    private static final ValeurId DEUX = new ValeurId("2");

    private FauxDepotEnquete depot;
    private FiltrageHypothesesService service;

    @BeforeEach
    void initialiser() {
        // Enquête 1 : référence 2x2x2 avec indice 1 (suspect != A) et indice 2 (lieu = X => objet != 1)
        EnqueteFiltrage enquete1 = new EnqueteFiltrage(
                1L,
                "Enquête de référence",
                List.of(
                        new VariableEnquete(SUSPECT, List.of(A, B)),
                        new VariableEnquete(LIEU, List.of(X, Y)),
                        new VariableEnquete(OBJET, List.of(UN, DEUX))),
                List.of(
                        new IndiceEnquete(
                                1L,
                                "A a un alibi",
                                new ContrainteLogique(List.of(new Litteral(SUSPECT, A, false)))),
                        new IndiceEnquete(
                                2L,
                                "Si lieu X alors pas objet 1",
                                new ContrainteLogique(List.of(
                                        new Litteral(LIEU, X, false),
                                        new Litteral(OBJET, UN, false))))));

        // Enquête 2 : autre enquête contenant l'indice 99
        EnqueteFiltrage enquete2 = new EnqueteFiltrage(
                2L,
                "Autre enquête",
                List.of(new VariableEnquete(SUSPECT, List.of(A, B))),
                List.of(new IndiceEnquete(
                        99L,
                        "Indice d'une autre enquête",
                        new ContrainteLogique(List.of(new Litteral(SUSPECT, B, false))))));

        depot = new FauxDepotEnquete(Map.of(1L, enquete1, 2L, enquete2));
        service = new FiltrageHypothesesService(depot, new SolveurFiltrageBacktracking());
    }

    @Test
    void s1_enquete_inconnue_leve_enquete_introuvable() {
        HypotheseJoueur vide = new HypotheseJoueur(Map.of());

        assertThatThrownBy(() -> service.filtrer(404L, Set.of(), vide))
                .isInstanceOf(EnqueteIntrouvableException.class);
    }

    @Test
    void s2_indice_inconnu_leve_enquete_invalide() {
        HypotheseJoueur vide = new HypotheseJoueur(Map.of());

        assertThatThrownBy(() -> service.filtrer(1L, Set.of(777L), vide))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void s3_indice_appartenant_a_une_autre_enquete_est_refuse() {
        HypotheseJoueur vide = new HypotheseJoueur(Map.of());

        assertThatThrownBy(() -> service.filtrer(1L, Set.of(99L), vide))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void s4_indices_actifs_null_leve_enquete_invalide() {
        HypotheseJoueur vide = new HypotheseJoueur(Map.of());

        assertThatThrownBy(() -> service.filtrer(1L, null, vide))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void s5_aucun_indice_actif_aucune_hypothese_donne_8_scenarios() {
        ResultatFiltrage res = service.filtrer(1L, Set.of(), new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(8L);
        assertThat(res.noeudsVisites()).isEqualTo(14L);
    }

    @Test
    void s6_seul_indice_1_actif_donne_4_scenarios() {
        // suspect != A => (B,X,1), (B,X,2), (B,Y,1), (B,Y,2)
        ResultatFiltrage res = service.filtrer(1L, Set.of(1L), new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(4L);
        assertThat(res.noeudsVisites()).isEqualTo(8L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 0L).containsEntry(B, 4L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 2L).containsEntry(Y, 2L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 2L).containsEntry(DEUX, 2L);
    }

    @Test
    void s7_seul_indice_2_actif_donne_6_scenarios() {
        // lieu = X => objet != 1 exclut uniquement (A,X,1) et (B,X,1)
        ResultatFiltrage res = service.filtrer(1L, Set.of(2L), new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(6L);
        assertThat(res.noeudsVisites()).isEqualTo(14L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 3L).containsEntry(B, 3L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 2L).containsEntry(Y, 4L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 2L).containsEntry(DEUX, 4L);
    }

    @Test
    void s8_indices_1_et_2_actifs_donnent_3_scenarios_de_reference() {
        ResultatFiltrage res = service.filtrer(1L, Set.of(1L, 2L), new HypotheseJoueur(Map.of()));

        assertThat(res.totalScenariosCompatibles()).isEqualTo(3L);
        assertThat(res.noeudsVisites()).isEqualTo(8L);
        assertThat(res.comptageParVariable().get(SUSPECT)).containsEntry(A, 0L).containsEntry(B, 3L);
        assertThat(res.comptageParVariable().get(LIEU)).containsEntry(X, 1L).containsEntry(Y, 2L);
        assertThat(res.comptageParVariable().get(OBJET)).containsEntry(UN, 1L).containsEntry(DEUX, 2L);
    }

    @Test
    void s9_hypothese_invalide_remonte_enquete_invalide() {
        HypotheseJoueur invalide = new HypotheseJoueur(Map.of(new VariableId("INCONNUE"), A));

        assertThatThrownBy(() -> service.filtrer(1L, Set.of(), invalide))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void s10_depot_interroge_une_seule_fois_avec_le_bon_identifiant() {
        service.filtrer(1L, Set.of(1L), new HypotheseJoueur(Map.of()));

        assertThat(depot.appels).containsExactly(1L);
    }

    private static final class FauxDepotEnquete implements EnqueteRepositoryPort {
        private final Map<Long, EnqueteFiltrage> stockage;
        private final List<Long> appels = new ArrayList<>();

        private FauxDepotEnquete(Map<Long, EnqueteFiltrage> stockage) {
            this.stockage = stockage;
        }

        @Override
        public Optional<EnqueteFiltrage> trouverParId(long id) {
            appels.add(id);
            return Optional.ofNullable(stockage.get(id));
        }
    }
}