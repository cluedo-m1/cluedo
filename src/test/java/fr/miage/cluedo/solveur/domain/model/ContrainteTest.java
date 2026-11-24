package fr.miage.cluedo.solveur.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ContrainteTest {

    @Test
    void bobNEstPasConcerneParLIndiceDAlice() {
        // 1. préparer
        Variable coupable = new Variable("Coupable", Set.of("Alice", "Bob", "Chloé"));
        Variable lieu = new Variable("Lieu", Set.of("salle", "imprimante", "serveur"));
        Contrainte aliceSeulementSalle = new Contrainte(
                coupable, lieu,
                (c, l) -> !c.equals("Alice") || l.equals("salle"),
                "Alice n'a accès qu'à la salle");

        // 2 et 3. agir et vérifier
        assertThat(aliceSeulementSalle.autorise("Bob", "serveur")).isTrue();
    }
    @Test
    void inverseDonneLeMemeResultatAvecLesValeursInversees() {
        // 1. préparer : la règle d'Alice
        Variable coupable = new Variable("Coupable", new LinkedHashSet<>(Set.of("Bob", "Alice")));
        Variable lieu = new Variable("Lieu", Set.of("imprimante", "serveur"));
        Contrainte c = new Contrainte(
                coupable, lieu,
                (co, l) -> !co.equals("Alice") || l.equals("salle"),
                "Alice n'a accès qu'à la salle");

        // 2. agir + 3. vérifier
        assertThat(c.inverse().autorise("serveur", "Alice")).isEqualTo(c.autorise("Alice", "serveur"));
        assertThat(c.inverse().autorise("serveur", "Bob")).isEqualTo(c.autorise("Bob", "serveur"));
    }
}