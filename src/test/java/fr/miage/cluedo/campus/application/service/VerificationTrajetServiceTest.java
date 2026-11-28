package fr.miage.cluedo.campus.application.service;

import fr.miage.cluedo.campus.application.port.in.ResultatVerification;
import fr.miage.cluedo.campus.application.port.out.GrapheCampusRepository;
import fr.miage.cluedo.campus.domain.exception.BatimentInconnuException;
import fr.miage.cluedo.campus.domain.model.Batiment;
import fr.miage.cluedo.campus.domain.model.Connexion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

class VerificationTrajetServiceTest {

    private static final Batiment BU = new Batiment("BU", "Bibliothèque");
    private static final Batiment RESTO = new Batiment("RESTO", "Restaurant");
    private static final Batiment AMPHI = new Batiment("AMPHI", "Amphithéâtre");
    private static final Batiment LABO = new Batiment("LABO", "Laboratoire");
    private static final Batiment GYMNASE = new Batiment("GYMNASE", "Gymnase");

    private static class FauxCampus implements GrapheCampusRepository {

        private final List<Batiment> batiments =
                List.of(BU, RESTO, AMPHI, LABO, GYMNASE);

        private final List<Connexion> connexions = List.of(
                new Connexion(BU, AMPHI, 4),
                new Connexion(AMPHI, BU, 4),
                new Connexion(BU, RESTO, 2),
                new Connexion(RESTO, BU, 2),
                new Connexion(RESTO, AMPHI, 1),
                new Connexion(AMPHI, RESTO, 1),
                new Connexion(AMPHI, LABO, 3),
                new Connexion(LABO, AMPHI, 3),
                new Connexion(RESTO, LABO, 7),
                new Connexion(LABO, RESTO, 7)
        );

        @Override
        public Optional<Batiment> batiment(String code) {
            return batiments.stream()
                    .filter(b -> b.code().equals(code))
                    .findFirst();
        }

        @Override
        public List<Connexion> connexions() {
            return connexions;
        }
    }

    private final VerificationTrajetService service =
            new VerificationTrajetService(new FauxCampus());

    @Test
    void regle_principale_5minutesPour6_estImpossible() {
        ResultatVerification r = service.verifier("BU", "LABO", 5);

        assertFalse(r.possible());
        assertEquals(OptionalInt.of(6), r.dureeMinimale());
    }

    @Test
    void tempsExact_estPossible() {
        ResultatVerification r = service.verifier("BU", "LABO", 6);

        assertTrue(r.possible());
        assertEquals(OptionalInt.of(6), r.dureeMinimale());
    }

    @Test
    void tempsSuffisant_estPossible() {
        ResultatVerification r = service.verifier("BU", "LABO", 10);

        assertTrue(r.possible());
    }

    @Test
    void memeLieu_estPossible() {
        ResultatVerification r = service.verifier("BU", "BU", 0);

        assertTrue(r.possible());
        assertEquals(OptionalInt.of(0), r.dureeMinimale());
    }

    @Test
    void aucunChemin_estImpossible() {
        ResultatVerification r = service.verifier("BU", "GYMNASE", 10);

        assertFalse(r.possible());
        assertTrue(r.dureeMinimale().isEmpty());
    }

    @Test
    void tempsNegatif_declencheException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.verifier("BU", "LABO", -1));
    }

    @Test
    void batimentInconnu_declencheException() {
        assertThrows(BatimentInconnuException.class,
                () -> service.verifier("BU", "INCONNU", 10));
    }
}