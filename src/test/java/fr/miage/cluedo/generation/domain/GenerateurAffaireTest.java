package fr.miage.cluedo.generation.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GenerateurAffaireTest {

    private static final List<Suspect> SUSPECTS = List.of(
            new Suspect("Alice", Set.of(MoyenAcces.CLE),
                    Set.of(Horaire.values())),
            new Suspect("Thomas", Set.of(MoyenAcces.BADGE),
                    Set.of(Horaire.values())),
            new Suspect("Emma",
                    Set.of(MoyenAcces.CLE, MoyenAcces.BADGE),
                    Set.of(Horaire.values())),
            new Suspect("Lucas", Set.of(),
                    Set.of(Horaire.values()))
    );

    private static final List<Lieu> LIEUX = List.of(
            new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE),
            new Lieu("Secrétariat", MoyenAcces.CLE),
            new Lieu("Salle informatique", MoyenAcces.BADGE)
    );

    private static final List<Horaire> HORAIRES =
            List.of(Horaire.values());

    private static final List<MoyenAcces> MOYENS =
            List.of(MoyenAcces.values());

    private final GenerateurAffaire generateur =
            new GenerateurAffaire();

    private Enquete generer(long seed) {
        return generateur.generer(
                SUSPECTS, LIEUX, HORAIRES, MOYENS, seed
        ).orElseThrow();
    }

    @Test
    void doitGenererUneEnqueteComplete() {

        Enquete enquete = generer(42L);

        assertNotNull(enquete.id());
        assertNotNull(enquete.solution());
        assertFalse(enquete.indices().isEmpty());

        assertEquals(SUSPECTS, enquete.suspects());
        assertEquals(LIEUX, enquete.lieux());
        assertEquals(HORAIRES, enquete.horaires());
        assertEquals(MOYENS, enquete.moyensAcces());
        assertEquals(42L, enquete.seed());
    }

    @Test
    void doitGarantirUneSolutionUniqueSur100Seeds() {

        VerificateurUnicite verificateur =
                new VerificateurUnicite();

        for (long seed = 0; seed < 100; seed++) {

            Enquete enquete = generer(seed);

            int nombreSolutions = verificateur.compterSolutions(
                    enquete.suspects(),
                    enquete.lieux(),
                    enquete.horaires(),
                    enquete.moyensAcces(),
                    enquete.indices());

            assertEquals(1, nombreSolutions,
                    "Unicite non respectee pour la seed " + seed);
        }
    }

    @Test
    void doitRespecterLesContraintesMetier() {

        for (long seed = 0; seed < 100; seed++) {

            Solution solution = generer(seed).solution();

            assertTrue(Contrainte.peutAcceder(
                    solution.coupable(), solution.lieu()));

            assertTrue(Contrainte.estPresent(
                    solution.coupable(), solution.horaire()));

            assertEquals(
                    solution.lieu().moyenAccesRequis(),
                    solution.moyenAcces());
        }
    }

    @Test
    void aucunIndiceNeDoitContredireLaSolution() {

        for (long seed = 0; seed < 100; seed++) {

            Enquete enquete = generer(seed);
            Solution solution = enquete.solution();

            for (Indice indice : enquete.indices()) {

                String valeur = switch (indice.variable()) {
                    case SUSPECT -> solution.coupable().nom();
                    case LIEU -> solution.lieu().nom();
                    case HORAIRE -> solution.horaire().name();
                    case MOYEN_ACCES -> solution.moyenAcces().name();
                };

                assertEquals(
                        Indice.Operateur.DIFFERENT,
                        indice.operateur());

                assertNotEquals(valeur, indice.valeur());
            }
        }
    }

    @Test
    void memeSeedDoitReproduireLeContenu() {

        Enquete premiere = generer(42L);
        Enquete seconde = generer(42L);

        assertEquals(premiere.solution(), seconde.solution());
        assertEquals(premiere.indices(), seconde.indices());
        assertEquals(premiere.seed(), seconde.seed());
    }

    @Test
    void chaqueEnqueteDoitAvoirSonIdentifiant() {

        Enquete premiere = generer(42L);
        Enquete seconde = generer(42L);

        assertNotEquals(premiere.id(), seconde.id());
    }

    @Test
    void doitRefuserUneEnqueteImpossible() {

        Suspect lucas = new Suspect(
                "Lucas",
                Set.of(),
                Set.of(Horaire.NEUF_HEURES));

        Lieu salle = new Lieu(
                "Salle informatique", MoyenAcces.BADGE);

        Optional<Enquete> resultat = generateur.generer(
                List.of(lucas),
                List.of(salle),
                HORAIRES,
                MOYENS,
                42L);

        assertTrue(resultat.isEmpty());
    }

    @Test
    void doitGererUnDomaineVide() {

        Optional<Enquete> resultat = generateur.generer(
                List.of(),
                LIEUX,
                HORAIRES,
                MOYENS,
                42L);

        assertTrue(resultat.isEmpty());
    }
}