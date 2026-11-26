package fr.miage.cluedo.solveur.domain.algo;

import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import fr.miage.cluedo.solveur.domain.model.Contrainte;
import fr.miage.cluedo.solveur.domain.model.Variable;

class RetourArriereTest {

    @Test
    void deuxSolutionsQuandLeLieuResteAmbigu() {
        Variable coupable = new Variable("Coupable", new LinkedHashSet<>(List.of("Rafael", "Alexis", "Cristina","Paulo")));
        Variable lieu = new Variable("Lieu", new LinkedHashSet<>(List.of("imprimante", "serveur")));

        Contrainte rafael = new Contrainte(coupable, lieu,
                (c, l) -> !c.equals("Rafael") || l.equals("salle"),
                "Rafael n'a accès qu'à la salle");
        Contrainte alexis = new Contrainte(coupable, lieu,
                (c, l) -> !c.equals("Alexis") || l.equals("salle"),
                "Alexis n'a accès qu'à la salle");

        int nb = RetourArriere.compterSolutions(List.of(coupable, lieu), List.of(rafael, alexis));

        assertThat(nb).isEqualTo(2);
    }
}