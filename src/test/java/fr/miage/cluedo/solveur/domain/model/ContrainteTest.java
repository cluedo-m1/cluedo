package fr.miage.cluedo.solveur.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

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
}