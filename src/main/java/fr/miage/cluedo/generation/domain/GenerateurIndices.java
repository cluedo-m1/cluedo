package fr.miage.cluedo.generation.domain;

import java.util.ArrayList;
import java.util.List;

public final class GenerateurIndices {

    public List<Indice> generer(
            Solution solution,
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces) {

        List<Indice> indices = new ArrayList<>();

        // Un indice DIFFERENT pour chaque suspect qui n'est PAS le coupable
        for (Suspect s : suspects) {
            if (!s.equals(solution.coupable())) {
                indices.add(new Indice(
                        Indice.Variable.SUSPECT,
                        Indice.Operateur.DIFFERENT,
                        s.nom(),
                        "Le coupable n'est pas " + s.nom() + "."));
            }
        }

        // À TOI : fais les mêmes boucles pour les lieux, les horaires
        // et les moyens d'accès (utilise .nom() pour un lieu, .name() pour un enum).

                for (Lieu l : lieux) {
            if (!l.equals(solution.lieu())) {
                indices.add(new Indice(
                        Indice.Variable.LIEU,
                        Indice.Operateur.DIFFERENT,
                        l.nom(),
                        "Le lieu n'est pas " + l.nom() + "."));
            }
        }

        for (Horaire h : horaires) {
            if (!h.equals(solution.horaire())) {
                indices.add(new Indice(
                        Indice.Variable.HORAIRE,
                        Indice.Operateur.DIFFERENT,
                        h.name(),
                        "L'heure n'est pas " + h.name() + "."));
            }
        }

        for (MoyenAcces m : moyensAcces) {
            if (!m.equals(solution.moyenAcces())) {
                indices.add(new Indice(
                        Indice.Variable.MOYEN_ACCES,
                        Indice.Operateur.DIFFERENT,
                        m.name(),
                        "Le moyen d'acces n'est pas " + m.name() + "."));
            }
        }

        return indices;
    }
}