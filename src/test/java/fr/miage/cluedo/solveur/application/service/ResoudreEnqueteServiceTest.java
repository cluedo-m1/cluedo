package fr.miage.cluedo.solveur.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.miage.cluedo.solveur.domain.model.Exclusion;
import fr.miage.cluedo.solveur.domain.model.Implication;
import fr.miage.cluedo.solveur.domain.model.Indice;
import fr.miage.cluedo.solveur.domain.model.Resultat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

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
    
}