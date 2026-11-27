package fr.miage.cluedo.campus.domain.algo;

import fr.miage.cluedo.campus.domain.model.Batiment;
import fr.miage.cluedo.campus.domain.model.Connexion;
import fr.miage.cluedo.campus.domain.model.Graphe;

import java.util.*;

public class Dijkstra {

    private record Etape(Batiment batiment, int distance) { }

    public static OptionalInt dureeMinimale(
            Graphe graphe,
            Batiment depart,
            Batiment arrivee) {

        Map<Batiment, Integer> distances = new HashMap<>();
        Set<Batiment> traites = new HashSet<>();

        PriorityQueue<Etape> file = new PriorityQueue<>(
                Comparator.comparingInt(Etape::distance)
        );

        distances.put(depart, 0);
        file.add(new Etape(depart, 0));

        while (!file.isEmpty()) {

            Etape etape = file.poll();
            Batiment courant = etape.batiment();
            int distanceCourante = etape.distance();

            if (!traites.add(courant)) {
                continue;
            }

            if (courant.equals(arrivee)) {
                return OptionalInt.of(distanceCourante);
            }

            for (Connexion connexion : graphe.voisinsDe(courant)) {

                Batiment voisin = connexion.arrivee();

                int nouvelleDistance =
                        distanceCourante + connexion.duree();

                if (nouvelleDistance < distances.getOrDefault(
                        voisin, Integer.MAX_VALUE)) {

                    distances.put(voisin, nouvelleDistance);
                    file.add(new Etape(voisin, nouvelleDistance));
                }
            }
        }

        return OptionalInt.empty();
    }
}
