package fr.miage.cluedo.generation.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VerificateurUniciteTest {

    private static final Suspect EMMA = new Suspect(
            "Emma",
            Set.of(MoyenAcces.CLE, MoyenAcces.BADGE),
            Set.of(Horaire.values()));

    private static final Suspect LUCAS = new Suspect(
            "Lucas",
            Set.of(),
            Set.of(Horaire.values()));

    private static final Lieu BIBLIOTHEQUE =
            new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE);

    private static final Lieu SALLE =
            new Lieu("Salle informatique", MoyenAcces.BADGE);

    private static final List<Suspect> SUSPECTS =
            List.of(EMMA, LUCAS);

    private static final List<Lieu> LIEUX =
            List.of(BIBLIOTHEQUE, SALLE);

    private static final List<Horaire> HORAIRES =
            List.of(Horaire.values());

    private static final List<MoyenAcces> MOYENS =
            List.of(MoyenAcces.values());

    private final VerificateurUnicite verificateur =
            new VerificateurUnicite();

    @Test
    void doitDetecterUneEnqueteSansSolution() {

        int resultat = verificateur.compterSolutions(
                List.of(LUCAS),
                List.of(SALLE),
                HORAIRES,
                MOYENS,
                List.of());

        assertEquals(0, resultat);
    }

    @Test
    void doitDetecterUneEnqueteAmbigue() {

        int resultat = verificateur.compterSolutions(
                SUSPECTS,
                LIEUX,
                HORAIRES,
                MOYENS,
                List.of());

        assertEquals(2, resultat);
    }

    @Test
    void doitConfirmerUneSolutionUnique() {

        Solution solution = new Solution(
                EMMA,
                SALLE,
                Horaire.NEUF_HEURES,
                MoyenAcces.BADGE);

        List<Indice> indices = new GenerateurIndices().generer(
                solution,
                SUSPECTS,
                LIEUX,
                HORAIRES,
                MOYENS);

        int resultat = verificateur.compterSolutions(
                SUSPECTS,
                LIEUX,
                HORAIRES,
                MOYENS,
                indices);

        assertEquals(1, resultat);
    }

    @Test
    void doitDetecterDesIndicesContradictoires() {

        List<Indice> indices = List.of(
                new Indice(
                        Indice.Variable.SUSPECT,
                        Indice.Operateur.DIFFERENT,
                        "Emma",
                        "Le coupable n'est pas Emma."),

                new Indice(
                        Indice.Variable.SUSPECT,
                        Indice.Operateur.DIFFERENT,
                        "Lucas",
                        "Le coupable n'est pas Lucas.")
        );

        int resultat = verificateur.compterSolutions(
                SUSPECTS,
                LIEUX,
                HORAIRES,
                MOYENS,
                indices);

        assertEquals(0, resultat);
    }

    @Test
    void doitAccepterUneSolutionUniqueSansIndice() {

        int resultat = verificateur.compterSolutions(
                List.of(EMMA),
                List.of(SALLE),
                List.of(Horaire.NEUF_HEURES),
                List.of(MoyenAcces.BADGE),
                List.of());

        assertEquals(1, resultat);
    }

    @Test
    void doitRefuserUnOperateurNonPrisEnCharge() {

        Indice indice = new Indice(
                Indice.Variable.SUSPECT,
                Indice.Operateur.EGAL,
                "Emma",
                "Le coupable est Emma.");

        assertThrows(
                IllegalArgumentException.class,
                () -> verificateur.compterSolutions(
                        SUSPECTS,
                        LIEUX,
                        HORAIRES,
                        MOYENS,
                        List.of(indice)));
    }
}