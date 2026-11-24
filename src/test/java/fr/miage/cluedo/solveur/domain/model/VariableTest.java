package fr.miage.cluedo.solveur.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;

class VariableTest {

    @Test
    void retirerUneValeurPresenteRenvoieTrueEtLaSupprime() {
        Variable lieu = new Variable("Lieu", Set.of("salle", "serveur"));

        assertThat(lieu.retirer("salle")).isTrue();
        assertThat(lieu.getDomaine()).containsExactly("serveur");
    }

    @Test
    void retirerUneValeurAbsenteRenvoieFalse() {
        Variable lieu = new Variable("Lieu", Set.of("salle"));

        assertThat(lieu.retirer("imprimante")).isFalse();
    }

    @Test
    void domaineVideApresAvoirToutRetire() {
        Variable lieu = new Variable("Lieu", Set.of("salle"));
        lieu.retirer("salle");

        assertThat(lieu.estVide()).isTrue();
    }
}