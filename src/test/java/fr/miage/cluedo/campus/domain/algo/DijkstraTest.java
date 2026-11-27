package fr.miage.cluedo.campus.domain.algo;

import fr.miage.cluedo.campus.domain.model.Batiment;
import fr.miage.cluedo.campus.domain.model.Connexion;
import fr.miage.cluedo.campus.domain.model.Graphe;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DijkstraTest {

    // Le mini-campus de la fiche
    private final Batiment bu    = new Batiment("BU", "Bibliothèque");
    private final Batiment resto = new Batiment("RESTO", "Restaurant");
    private final Batiment amphi = new Batiment("AMPHI", "Amphithéâtre");
    private final Batiment labo  = new Batiment("LABO", "Laboratoire");

    /** Ajoute une connexion dans les deux sens (le campus n'a pas de sens unique). */
    private static void relier(List<Connexion> liste, Batiment a, Batiment b, int duree) {
        liste.add(new Connexion(a, b, duree));
        liste.add(new Connexion(b, a, duree));
    }

    private Graphe campusDeLaFiche() {
        List<Connexion> c = new ArrayList<>();
        relier(c, bu, amphi, 4);
        relier(c, bu, resto, 2);
        relier(c, resto, amphi, 1);
        relier(c, amphi, labo, 3);
        relier(c, resto, labo, 7);
        return new Graphe(c);
    }

    @Test
    void exempleDeLaFiche_BUversLabo_vaut6() {
        OptionalInt resultat = Dijkstra.dureeMinimale(campusDeLaFiche(), bu, labo);

        assertEquals(OptionalInt.of(6), resultat);
    }

    @Test
    void memeLieu_vaut0() {
        OptionalInt resultat = Dijkstra.dureeMinimale(campusDeLaFiche(), bu, bu);

        assertEquals(OptionalInt.of(0), resultat);
    }

    @Test
    void aucunChemin_renvoieVide() {
        Batiment gymnase = new Batiment("GYMNASE", "Gymnase");

        OptionalInt resultat = Dijkstra.dureeMinimale(campusDeLaFiche(), bu, gymnase);

        assertEquals(OptionalInt.empty(), resultat);
    }
}