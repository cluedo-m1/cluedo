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
    @Test 
    void uneSeuleSolutionQuandOnPrecise(){
        Variable coupable = new Variable("Coupable", new LinkedHashSet<>(List.of("Rafael", "Alexis", "Cristina","Paulo")));
        Variable lieu = new Variable("Lieu", new LinkedHashSet<>(List.of("imprimante", "serveur")));

        Contrainte rafael = new Contrainte(coupable, lieu,
                (c, l) -> !c.equals("Rafael") || l.equals("salle"),
                "Rafael n'a accès qu'à la salle");
        Contrainte alexis = new Contrainte(coupable, lieu,
                (c, l) -> !c.equals("Alexis") || l.equals("salle"),
                "Alexis n'a accès qu'à la salle");
        Contrainte cristina = new Contrainte(coupable, lieu,
                (c,l)-> !c.equals("Cristina") || l.equals("imprimante"),
                "Cristina n'a accès qu'à l'imprimante");
        Contrainte paulo = new Contrainte(coupable, lieu,
                (c, l) -> !c.equals("Paulo") || l.equals("salle"),
                "Paulo n'a accès qu'à la salle");

        int nb = RetourArriere.compterSolutions(List.of(coupable, lieu), List.of(rafael, alexis,cristina,paulo));

        assertThat(nb).isEqualTo(1);
    }
    @Test 
    void aucuneSolutionQuandEnqueteIncoherente(){
        Variable coupable = new Variable("Coupable",new LinkedHashSet<>(List.of("Rafael")));
        Variable lieu = new Variable("Lieu",new LinkedHashSet<>(List.of("imprimante")));

        Contrainte rafael = new Contrainte(coupable, lieu, 
        (c,l)-> !c.equals("Rafael") || l.equals("salle"),
        "Rafael n'a accès qu a la salle");
        int nb = RetourArriere.compterSolutions(List.of(coupable, lieu),List.of(rafael));
        assertThat(nb).isEqualTo(0);

    }
}