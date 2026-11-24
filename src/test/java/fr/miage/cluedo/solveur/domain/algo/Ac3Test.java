package fr.miage.cluedo.solveur.domain.algo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import fr.miage.cluedo.solveur.domain.model.Contrainte;
import fr.miage.cluedo.solveur.domain.model.Variable;

class Ac3Test {

    @Test
    void reviseRetireLesValeursSansSupport() {
        // 1. préparer : Bob avant Alice, pour que l'ordre révèle le bug de aUnSup
        Variable coupable = new Variable("Coupable", new LinkedHashSet<>(List.of("Bob", "Alice")));
        Variable lieu = new Variable("Lieu", Set.of("imprimante", "serveur"));
        Contrainte aliceSeulementSalle = new Contrainte(
                coupable, lieu,
                (c, l) -> !c.equals("Alice") || l.equals("salle"),
                "Alice n'a accès qu'à la salle");

        // 2. agir
        boolean modifie = Ac3.revise(aliceSeulementSalle);

        // 3. vérifier
        assertThat(modifie).isTrue();
        assertThat(coupable.getDomaine()).containsExactly("Bob");
    }
    @Test
    void reviseNeRetireRienQuandToutAUnSupport() {
        // 1. préparer : Bob avant Alice, pour que l'ordre révèle le bug de aUnSup
        Variable coupable = new Variable("Coupable", new LinkedHashSet<>(List.of("Bob")));
        Variable lieu = new Variable("Lieu", Set.of("imprimante", "serveur"));
        Contrainte aliceSeulementSalle = new Contrainte(
                coupable, lieu,
                (c, l) -> !c.equals("Alice") || l.equals("salle"),
                "Bob n'a accès qu'à la salle");

        // 2. agir
        boolean modifie = Ac3.revise(aliceSeulementSalle);

        // 3. vérifier
        assertThat(modifie).isFalse();
        assertThat(coupable.getDomaine()).containsExactly("Bob");
    }
    @Test
    void reviseVideLeDomaineQuandAucuneValeurNAUnSupport() {
        // 1. préparer : Bob avant Alice, pour que l'ordre révèle le bug de aUnSup
        Variable coupable = new Variable("Coupable", new LinkedHashSet<>(List.of("Alice")));
        Variable lieu = new Variable("Lieu", Set.of("imprimante", "serveur"));
        Contrainte aliceSeulementSalle = new Contrainte(
                coupable, lieu,
                (c, l) -> !c.equals("Alice") || l.equals("salle"),
                "Alice n'a aucun lieu compatible");

        // 2. agir
        boolean modifie = Ac3.revise(aliceSeulementSalle);

        // 3. vérifier
        assertThat(modifie).isTrue();
        assertThat(coupable.estVide()).isTrue();
    }
}