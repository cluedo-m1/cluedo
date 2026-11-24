package fr.miage.cluedo.joueur.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EnqueteFiltrageTest {

    private static final VariableId SUSPECT = new VariableId("SUSPECT");
    private static final VariableId LIEU = new VariableId("LIEU");
    private static final ValeurId A = new ValeurId("A");
    private static final ValeurId B = new ValeurId("B");
    private static final ValeurId X = new ValeurId("X");

    @Test
    void selectionne_uniquement_les_clauses_des_indices_actifs() {
        ContrainteLogique clause1 = new ContrainteLogique(List.of(new Litteral(SUSPECT, A, false)));
        ContrainteLogique clause2 = new ContrainteLogique(List.of(new Litteral(LIEU, X, true)));

        EnqueteFiltrage enquete = new EnqueteFiltrage(
                1L,
                "Enquête de test",
                List.of(
                        new VariableEnquete(SUSPECT, List.of(A, B)),
                        new VariableEnquete(LIEU, List.of(X))),
                List.of(
                        new IndiceEnquete(10L, "Alibi de A", clause1),
                        new IndiceEnquete(20L, "Incident en X", clause2)));

        assertThat(enquete.contraintesActives(Set.of(20L))).containsExactly(clause2);
        assertThat(enquete.contraintesActives(Set.of())).isEmpty();
    }

    @Test
    void refuse_un_identifiant_indice_inconnu() {
        EnqueteFiltrage enquete = new EnqueteFiltrage(
                1L,
                "Enquête de test",
                List.of(new VariableEnquete(SUSPECT, List.of(A, B))),
                List.of(new IndiceEnquete(10L, "Alibi de A",
                        new ContrainteLogique(List.of(new Litteral(SUSPECT, A, false))))));

        assertThatThrownBy(() -> enquete.contraintesActives(Set.of(999L)))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void refuse_une_variable_declaree_deux_fois() {
        VariableEnquete v1 = new VariableEnquete(SUSPECT, List.of(A));
        VariableEnquete v2 = new VariableEnquete(SUSPECT, List.of(B));

        assertThatThrownBy(() -> new EnqueteFiltrage(1L, "Enquête invalide", List.of(v1, v2), List.of()))
                .isInstanceOf(EnqueteInvalideException.class);
    }
}