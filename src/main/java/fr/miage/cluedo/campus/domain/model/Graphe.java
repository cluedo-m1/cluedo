package fr.miage.cluedo.campus.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Graphe {

    private final Map<Batiment, List<Connexion>> voisins = new HashMap<>();

    public Graphe(List<Connexion> connexions) {
        for (Connexion c : connexions) {
            voisins.computeIfAbsent(c.depart(), k -> new ArrayList<>()).add(c);
        }
    }

    public List<Connexion> voisinsDe(Batiment b) {
        return voisins.getOrDefault(b, List.of());
    }
}
