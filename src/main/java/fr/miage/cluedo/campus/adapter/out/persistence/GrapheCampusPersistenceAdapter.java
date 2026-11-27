package fr.miage.cluedo.campus.adapter.out.persistence;

import fr.miage.cluedo.campus.application.port.out.GrapheCampusRepository;
import fr.miage.cluedo.campus.domain.model.Batiment;
import fr.miage.cluedo.campus.domain.model.Connexion;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Adaptateur : implémente le port GrapheCampusRepository avec la base de données. */
@Component
class GrapheCampusPersistenceAdapter implements GrapheCampusRepository {

    private final BatimentJpaRepository batiments;
    private final ConnexionJpaRepository connexions;

    GrapheCampusPersistenceAdapter(BatimentJpaRepository batiments, ConnexionJpaRepository connexions) {
        this.batiments = batiments;
        this.connexions = connexions;
    }

    @Override
    public Optional<Batiment> batiment(String code) {
        return batiments.findById(code).map(GrapheCampusPersistenceAdapter::versDomaine);
    }

    @Override
    public List<Connexion> connexions() {
        // On charge tous les bâtiments une seule fois, rangés par code
        Map<String, Batiment> parCode = batiments.findAll().stream()
                .map(GrapheCampusPersistenceAdapter::versDomaine)
                .collect(Collectors.toMap(Batiment::code, Function.identity()));

        return connexions.findAll().stream()
                .map(c -> new Connexion(parCode.get(c.getDepart()), parCode.get(c.getArrivee()), c.getDuree()))
                .toList();
    }

    private static Batiment versDomaine(BatimentJpaEntity e) {
        return new Batiment(e.getCode(), e.getNom());
    }
}