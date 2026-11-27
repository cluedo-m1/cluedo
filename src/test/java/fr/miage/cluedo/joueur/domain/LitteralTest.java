package fr.miage.cluedo.joueur.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class LitteralTest {

    private static final VariableId LIEU = new VariableId("LIEU");
    private static final ValeurId AMHPI = new ValeurId("AMPHI");
    private static final ValeurId SALLE_TP = new ValeurId("SALLE_TP");

    @Test
    void quatre_combinaisons_polarite_et_egalite() {
        Litteral positif = new Litteral(LIEU, AMHPI, true);
        Litteral negatif = new Litteral(LIEU, AMHPI, false);

        // Positif + égalité -> true ; Positif + différence -> false
        assertThat(positif.estVrai(AMHPI)).isTrue();
        assertThat(positif.estVrai(SALLE_TP)).isFalse();

        // Négatif + égalité -> false ; Négatif + différence -> true
        assertThat(negatif.estVrai(AMHPI)).isFalse();
        assertThat(negatif.estVrai(SALLE_TP)).isTrue();
    }

    @Test
    void refuse_variable_ou_valeur_nulle() {
        assertThatThrownBy(() -> new Litteral(null, AMHPI, true))
                .isInstanceOf(EnqueteInvalideException.class);
        assertThatThrownBy(() -> new Litteral(LIEU, null, true))
                .isInstanceOf(EnqueteInvalideException.class);
    }
}