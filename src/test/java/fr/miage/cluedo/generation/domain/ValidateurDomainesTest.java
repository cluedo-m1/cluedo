package fr.miage.cluedo.generation.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ValidateurDomainesTest {

    private static final Suspect ALICE = new Suspect(
            "Alice",
            Set.of(MoyenAcces.CLE),
            Set.of(Horaire.values()));

    private static final Lieu BIBLIOTHEQUE =
            new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE);

    private static final List<Horaire> HORAIRES =
            List.of(Horaire.values());

    private static final List<MoyenAcces> MOYENS =
            List.of(MoyenAcces.values());

    @Test
    void doitAccepterDesDomainesValides() {

        assertDoesNotThrow(() ->
                ValidateurDomaines.valider(
                        List.of(ALICE),
                        List.of(BIBLIOTHEQUE),
                        HORAIRES,
                        MOYENS));
    }

    @Test
    void doitRefuserLesSuspectsEnDouble() {

        assertThrows(IllegalArgumentException.class, () ->
                ValidateurDomaines.valider(
                        List.of(ALICE, ALICE),
                        List.of(BIBLIOTHEQUE),
                        HORAIRES,
                        MOYENS));
    }

    @Test
    void doitRefuserDeuxSuspectsDeMemeNom() {

        Suspect autreAlice = new Suspect(
                "Alice",
                Set.of(MoyenAcces.BADGE),
                Set.of(Horaire.values()));

        assertThrows(IllegalArgumentException.class, () ->
                ValidateurDomaines.valider(
                        List.of(ALICE, autreAlice),
                        List.of(BIBLIOTHEQUE),
                        HORAIRES,
                        MOYENS));
    }

    @Test
    void doitRefuserLesHorairesEnDouble() {

        assertThrows(IllegalArgumentException.class, () ->
                ValidateurDomaines.valider(
                        List.of(ALICE),
                        List.of(BIBLIOTHEQUE),
                        List.of(
                                Horaire.NEUF_HEURES,
                                Horaire.NEUF_HEURES),
                        MOYENS));
    }

    @Test
    void doitRefuserDeuxLieuxDeMemeNom() {

        Lieu autreBibliotheque = new Lieu(
                "Bibliothèque", MoyenAcces.BADGE);

        assertThrows(IllegalArgumentException.class, () ->
                ValidateurDomaines.valider(
                        List.of(ALICE),
                        List.of(BIBLIOTHEQUE, autreBibliotheque),
                        HORAIRES,
                        MOYENS));
    }

    @Test
    void doitRefuserUnDomaineAbsent() {

        assertThrows(IllegalArgumentException.class, () ->
                ValidateurDomaines.valider(
                        null,
                        List.of(BIBLIOTHEQUE),
                        HORAIRES,
                        MOYENS));
    }

    @Test
    void leGenerateurDoitRefuserLesDoublons() {

        assertThrows(IllegalArgumentException.class, () ->
                new GenerateurAffaire().generer(
                        List.of(ALICE, ALICE),
                        List.of(BIBLIOTHEQUE),
                        HORAIRES,
                        MOYENS,
                        42L));
    }
}