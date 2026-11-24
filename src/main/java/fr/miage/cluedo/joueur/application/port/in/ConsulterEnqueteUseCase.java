package fr.miage.cluedo.joueur.application.port.in;

import fr.miage.cluedo.joueur.application.EnqueteIntrouvableException;
import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;

/** Cas d'utilisation : afficher une enquête jouable (variables, domaines, indices disponibles). */
public interface ConsulterEnqueteUseCase {

    /** @throws EnqueteIntrouvableException si l'enquête n'existe pas */
    EnqueteFiltrage consulter(long enqueteId);
}
