package fr.miage.cluedo.solveur.domain.model;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Test
    void restaurerRemetLeDomaineDAvant(){
        Variable lieu = new Variable("Lieu", Set.of("salle","serveur"));
        Set<String> sauvegarde = lieu.sauvegarder();
        lieu.retirer("salle");
        assertThat(lieu.getDomaine());
        lieu.restaurer(sauvegarde);
        assertThat(lieu.getDomaine());
    }
    @Test
    void laSauvegardeEstIndependanteDuDomaine() {
        Variable lieu = new Variable("Lieu", Set.of("salle", "serveur"));
        Set<String> sauvegarde = lieu.sauvegarder();

        lieu.retirer("salle");

        assertThat(sauvegarde).containsExactlyInAnyOrder("salle", "serveur");
    }
    @Test 
    void testSurFixer(){
        Variable lieu = new Variable("lieu",Set.of("salle","serveur"));
        lieu.fixer("salle");

        assertThat(lieu.getDomaine()).containsExactly("salle");
    }
}