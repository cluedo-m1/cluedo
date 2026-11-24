package fr.miage.cluedo.joueur.application.port.out;

import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;
import java.util.Optional;

/** Accès aux enquêtes jouables. Le domaine ne sait pas d'où elles viennent (base, mémoire dans les tests...). */
public interface EnqueteRepositoryPort {

    Optional<EnqueteFiltrage> trouverParId(long id);
}
