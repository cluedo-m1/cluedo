package fr.miage.cluedo.generation.domain;

import static org.junit.jupiter.api.Assertions.*;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GenerateurIndicesTest {

    private static final Suspect EMMA = new Suspect(
            "Emma", Set.of(MoyenAcces.CLE, MoyenAcces.BADGE), Set.of(Horaire.values()));
    private static final List<Suspect> SUSPECTS = List.of(
            new Suspect("Alice", Set.of(MoyenAcces.CLE), Set.of(Horaire.values())),
            new Suspect("Thomas", Set.of(MoyenAcces.BADGE), Set.of(Horaire.values())),
            EMMA,
            new Suspect("Lucas", Set.of(), Set.of(Horaire.values())));
    private static final Lieu SALLE = new Lieu("Salle informatique", MoyenAcces.BADGE);
    private static final List<Lieu> LIEUX = List.of(
            new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE),
            new Lieu("Secrétariat", MoyenAcces.CLE),
            SALLE);
    private static final List<Horaire> HORAIRES = List.of(Horaire.values());
    private static final List<MoyenAcces> MOYENS = List.of(MoyenAcces.values());

    private static final Solution SOLUTION =
            new Solution(EMMA, SALLE, Horaire.NEUF_HEURES, MoyenAcces.BADGE);

    private static String valeurSecrete(Solution s, Indice.Variable v) {
        return switch (v) {
            case SUSPECT -> s.coupable().nom();
            case LIEU -> s.lieu().nom();
            case HORAIRE -> s.horaire().name();
            case MOYEN_ACCES -> s.moyenAcces().name();
        };
    }

    private List<Indice> generer() {
        return new GenerateurIndices().generer(SOLUTION, SUSPECTS, LIEUX, HORAIRES, MOYENS);
    }

    @Test
    void doitGenererAuMoinsUnIndice() {
        assertFalse(generer().isEmpty());
    }

    @Test
    void aucunIndiceNeDoitContredireLaSolution() {
        for (Indice indice : generer()) {
            assertEquals(Indice.Operateur.DIFFERENT, indice.operateur());
            assertNotEquals(valeurSecrete(SOLUTION, indice.variable()), indice.valeur());
        }
    }

    @Test
    void ilNeDoitPasYAvoirDeDoublons() {
        List<Indice> indices = generer();
        assertEquals(indices.size(), new HashSet<>(indices).size());
    }

        @Test
    void chaqueVariableDoitAvoirAuMoinsUnIndice() {
        Set<Indice.Variable> couvertes = new HashSet<>();
        for (Indice indice : generer()) {
            couvertes.add(indice.variable());
        }
        assertEquals(EnumSet.allOf(Indice.Variable.class), couvertes);
    }
}