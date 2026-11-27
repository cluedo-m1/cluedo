package fr.miage.cluedo.campus.application.service;

import fr.miage.cluedo.campus.application.port.in.ResultatVerification;
import fr.miage.cluedo.campus.application.port.in.VerificationTrajetUseCase;
import fr.miage.cluedo.campus.application.port.out.GrapheCampusRepository;
import fr.miage.cluedo.campus.domain.algo.Dijkstra;
import fr.miage.cluedo.campus.domain.exception.BatimentInconnuException;
import fr.miage.cluedo.campus.domain.model.Batiment;
import fr.miage.cluedo.campus.domain.model.Graphe;

import java.util.OptionalInt;

public class VerificationTrajetService implements VerificationTrajetUseCase {

    private final GrapheCampusRepository campus;

    public VerificationTrajetService(GrapheCampusRepository campus) {
        this.campus = campus;
    }

    @Override
    public ResultatVerification verifier(String codeDepart, String codeArrivee, int tempsEcoule) {

        if (tempsEcoule < 0) {
            throw new IllegalArgumentException("Le temps écoulé ne peut pas être négatif");
        }

        Batiment depart = campus.batiment(codeDepart)
                .orElseThrow(() -> new BatimentInconnuException(codeDepart));

        Batiment arrivee = campus.batiment(codeArrivee)
                .orElseThrow(() -> new BatimentInconnuException(codeArrivee));

        Graphe graphe = new Graphe(campus.connexions());

        OptionalInt dureeMinimale =
                Dijkstra.dureeMinimale(graphe, depart, arrivee);

        boolean possible = dureeMinimale.isPresent()
                && dureeMinimale.getAsInt() <= tempsEcoule;

        return new ResultatVerification(possible, dureeMinimale);
    }
}