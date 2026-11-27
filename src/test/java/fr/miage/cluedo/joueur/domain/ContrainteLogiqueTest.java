package fr.miage.cluedo.joueur.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ContrainteLogiqueTest {

    private static final Litteral LIT = new Litteral(new VariableId("SUSPECT"), new ValeurId("A"), false);

    @Test
    void refuse_clause_nulle_ou_vide() {
        assertThatThrownBy(() -> new ContrainteLogique(null))
                .isInstanceOf(EnqueteInvalideException.class);
        assertThatThrownBy(() -> new ContrainteLogique(List.of()))
                .isInstanceOf(EnqueteInvalideException.class);
    }

    @Test
    void copie_defensivement_la_liste_de_litteraux() {
        List<Litteral> source = new ArrayList<>();
        source.add(LIT);

        ContrainteLogique clause = new ContrainteLogique(source);
        source.clear();

        assertThat(clause.litteraux()).containsExactly(LIT);
        assertThatThrownBy(() -> clause.litteraux().add(LIT))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}