package fr.miage.cluedo.generation.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GenerateurEnqueteTest {

    private static final List<Suspect> SUSPECTS = List.of(
            new Suspect("Alice", Set.of(MoyenAcces.CLE), Set.of(Horaire.values())),
            new Suspect("Thomas", Set.of(MoyenAcces.BADGE), Set.of(Horaire.values())),
            new Suspect("Emma", Set.of(MoyenAcces.CLE, MoyenAcces.BADGE), Set.of(Horaire.values())),
            new Suspect("Lucas", Set.of(), Set.of(Horaire.values())));
    private static final List<Lieu> LIEUX = List.of(
            new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE),
            new Lieu("Secrétariat", MoyenAcces.CLE),
            new Lieu("Salle informatique", MoyenAcces.BADGE));
    private static final List<Horaire> HORAIRES = List.of(Horaire.values());
    private static final List<MoyenAcces> MOYENS = List.of(MoyenAcces.values());

    @Test
    void doitTrouverUneSolutionValide() {
        Suspect lucas = new Suspect("Lucas", Set.of(), Set.of(Horaire.NEUF_HEURES));
        Lieu bibliotheque = new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE);

        Optional<Solution> resultat = new GenerateurEnquete().generer(
                List.of(lucas),
                List.of(bibliotheque),
                List.of(Horaire.NEUF_HEURES),
                List.of(MoyenAcces.ACCES_LIBRE));

        assertTrue(resultat.isPresent());
        assertEquals(lucas, resultat.get().coupable());
        assertEquals(bibliotheque, resultat.get().lieu());
        assertEquals(Horaire.NEUF_HEURES, resultat.get().horaire());
        assertEquals(MoyenAcces.ACCES_LIBRE, resultat.get().moyenAcces());
    }

    @Test
    void doitRefuserUnAccesNonAutorise() {
        Suspect lucas = new Suspect("Lucas", Set.of(), Set.of(Horaire.NEUF_HEURES));
        Lieu salleInformatique = new Lieu("Salle informatique", MoyenAcces.BADGE);

        Optional<Solution> resultat = new GenerateurEnquete().generer(
                List.of(lucas),
                List.of(salleInformatique),
                List.of(Horaire.NEUF_HEURES),
                List.of(MoyenAcces.BADGE));

        assertTrue(resultat.isEmpty());
    }

    @Test
    void doitEffectuerUnRetourArriere() {
        Suspect lucas = new Suspect("Lucas", Set.of(), Set.of(Horaire.NEUF_HEURES));
        Suspect emma = new Suspect(
                "Emma",
                Set.of(MoyenAcces.CLE, MoyenAcces.BADGE),
                Set.of(Horaire.NEUF_HEURES));
        Lieu salleInformatique = new Lieu("Salle informatique", MoyenAcces.BADGE);

        Optional<Solution> resultat = new GenerateurEnquete().generer(
                List.of(lucas, emma),
                List.of(salleInformatique),
                List.of(Horaire.NEUF_HEURES),
                List.of(MoyenAcces.BADGE));

        assertTrue(resultat.isPresent());
        assertEquals(emma, resultat.get().coupable());
        assertEquals(salleInformatique, resultat.get().lieu());
    }

    @Test
    void doitRefuserUnSuspectAbsent() {
        Suspect lucas = new Suspect("Lucas", Set.of(), Set.of(Horaire.NEUF_HEURES));
        Lieu bibliotheque = new Lieu("Bibliothèque", MoyenAcces.ACCES_LIBRE);

        Optional<Solution> resultat = new GenerateurEnquete().generer(
                List.of(lucas),
                List.of(bibliotheque),
                List.of(Horaire.DIX_HEURES),
                List.of(MoyenAcces.ACCES_LIBRE));

        assertTrue(resultat.isEmpty());
    }

    @Test
    void doitGererUnDomaineVide() {
        Suspect lucas = new Suspect("Lucas", Set.of(), Set.of(Horaire.NEUF_HEURES));

        Optional<Solution> resultat = new GenerateurEnquete().generer(
                List.of(lucas),
                List.of(),
                List.of(Horaire.NEUF_HEURES),
                List.of(MoyenAcces.ACCES_LIBRE));

        assertTrue(resultat.isEmpty());
    }

    @Test
    void memeSeedDoitDonnerMemeSolution() {
        Optional<Solution> a = new GenerateurEnquete(42L).generer(SUSPECTS, LIEUX, HORAIRES, MOYENS);
        Optional<Solution> b = new GenerateurEnquete(42L).generer(SUSPECTS, LIEUX, HORAIRES, MOYENS);

        assertTrue(a.isPresent());
        assertEquals(a, b);
    }

    @Test
    void desSeedsDifferentesDoiventDonnerDesSolutionsVariees() {
        Set<Solution> solutions = new HashSet<>();
        for (long seed = 0; seed < 50; seed++) {
            new GenerateurEnquete(seed).generer(SUSPECTS, LIEUX, HORAIRES, MOYENS)
                    .ifPresent(solutions::add);
        }
        assertTrue(solutions.size() > 1);
    }

    @Test
    void leDomaineDoitResterInchange() {
        List<Suspect> copieAvant = List.copyOf(SUSPECTS);
        new GenerateurEnquete(7L).generer(SUSPECTS, LIEUX, HORAIRES, MOYENS);
        assertEquals(copieAvant, SUSPECTS);
    }
}