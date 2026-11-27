package fr.miage.cluedo.solveur.application.service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import fr.miage.cluedo.solveur.domain.exception.EnqueteInvalideException;
import fr.miage.cluedo.solveur.domain.model.Exclusion;
import fr.miage.cluedo.solveur.domain.model.Implication;
import fr.miage.cluedo.solveur.domain.model.Indice;
import fr.miage.cluedo.solveur.domain.model.Resultat;

class ResoudreEnqueteServiceTest {

    @Test
    void resoutLEnqueteDeLaMatinee() {
        ResoudreEnqueteService service = new ResoudreEnqueteService();

        Map<String, Set<String>> domaines = new LinkedHashMap<>();
        domaines.put("Coupable", new LinkedHashSet<>(List.of("Alice", "Bob", "Chloé")));
        domaines.put("Lieu", new LinkedHashSet<>(List.of("salle", "imprimante", "serveur")));

        List<Indice> indices = List.of(
                new Exclusion("Lieu", "salle"),
                new Implication("Coupable", "Alice", "Lieu", Set.of("salle")),
                new Implication("Coupable", "Chloé", "Lieu", Set.of("salle")));

        Resultat resultat = service.resoudre(domaines, indices);

        assertThat(resultat.nombreSolutions()).isEqualTo(2);
        assertThat(resultat.domaines().get("Coupable")).containsExactlyInAnyOrder("Bob");
        assertThat(resultat.domaines().get("Lieu")).containsExactlyInAnyOrder("imprimante", "serveur");
    }
    @Test 
    void resoutAvecUneSeuleSolutionQuandBobEstContraint(){
         ResoudreEnqueteService service = new ResoudreEnqueteService();

        Map<String, Set<String>> domaines = new LinkedHashMap<>();
        domaines.put("Coupable", new LinkedHashSet<>(List.of("Alice", "Bob", "Chloé")));
        domaines.put("Lieu", new LinkedHashSet<>(List.of("salle", "imprimante", "serveur")));

        List<Indice> indices = List.of(
                new Exclusion("Lieu", "salle"),
                new Implication("Coupable", "Alice", "Lieu", Set.of("salle")),
                new Implication("Coupable", "Chloé", "Lieu", Set.of("salle")),
                new Implication("Coupable", "Bob","Lieu",Set.of("imprimante")));

        Resultat resultat = service.resoudre(domaines, indices);

        assertThat(resultat.nombreSolutions()).isEqualTo(1);
        assertThat(resultat.domaines().get("Lieu")).containsExactly("imprimante");
        assertThat(resultat.domaines().get("Coupable")).containsExactly("Bob");
    }
    @Test
    void refuseUnIndiceSurUneVariableInconnue() {
        ResoudreEnqueteService service = new ResoudreEnqueteService();
        Map<String, Set<String>> domaines = Map.of("Lieu", Set.of("salle", "serveur"));
        List<Indice> indices = List.of(new Exclusion("Lieux", "salle")); // faute de frappe voulue

        assertThatThrownBy(() -> service.resoudre(domaines, indices))
                .isInstanceOf(EnqueteInvalideException.class)
                .hasMessageContaining("Lieux");
    }

}