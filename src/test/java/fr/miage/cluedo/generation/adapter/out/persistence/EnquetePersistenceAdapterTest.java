package fr.miage.cluedo.generation.adapter.out.persistence;

import fr.miage.cluedo.generation.domain.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EnquetePersistenceAdapterTest {

    @Autowired
    private EnquetePersistenceAdapter repository;

    private Enquete creerEnquete() {
        return new GenerateurAffaire().generer(
                List.of(
                    new Suspect("Alice",
                        Set.of(MoyenAcces.CLE),
                        Set.of(Horaire.values())),
                    new Suspect("Lucas",
                        Set.of(),
                        Set.of(Horaire.values()))
                ),
                List.of(
                    new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE),
                    new Lieu("Secrétariat", MoyenAcces.CLE)
                ),
                List.of(Horaire.values()),
                List.of(MoyenAcces.values()),
                42L
        ).orElseThrow();
    }

    @Test
    void doitSauvegarderEtRetrouverUneEnqueteComplete() {
        Enquete enquete = creerEnquete();

        repository.sauvegarder(enquete);

        Enquete retrouvee = repository
                .trouverParId(enquete.id())
                .orElseThrow();

        assertEquals(enquete, retrouvee);
    }

    @Test
    void doitRetournerVidePourUnIdentifiantInconnu() {
        assertTrue(
            repository.trouverParId(UUID.randomUUID()).isEmpty()
        );
    }

    @Test
    void doitSauvegarderPlusieursEnquetesIndependantes() {
        Enquete premiere = creerEnquete();
        Enquete seconde = creerEnquete();

        repository.sauvegarder(premiere);
        repository.sauvegarder(seconde);

        assertNotEquals(premiere.id(), seconde.id());

        assertEquals(premiere,
            repository.trouverParId(premiere.id()).orElseThrow());

        assertEquals(seconde,
            repository.trouverParId(seconde.id()).orElseThrow());
    }
}