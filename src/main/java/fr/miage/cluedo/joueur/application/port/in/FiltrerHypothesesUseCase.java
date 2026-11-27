package fr.miage.cluedo.joueur.application.port.in;

import fr.miage.cluedo.joueur.application.EnqueteIntrouvableException;
import fr.miage.cluedo.joueur.domain.EnqueteInvalideException;
import fr.miage.cluedo.joueur.domain.HypotheseJoueur;
import fr.miage.cluedo.joueur.domain.ResultatFiltrage;
import java.util.Set;

/** Cas d'utilisation : dénombrer les scénarios encore compatibles avec les indices actifs et l'hypothèse. */
public interface FiltrerHypothesesUseCase {

    /**
     * @param enqueteId       l'enquête jouée
     * @param indicesActifsIds les indices que le joueur a activés
     * @param hypothese       l'hypothèse partielle ou complète du joueur
     * @throws EnqueteIntrouvableException si l'enquête n'existe pas
     * @throws EnqueteInvalideException    si un indice n'appartient pas à l'enquête ou si l'hypothèse est incohérente
     */
    ResultatFiltrage filtrer(long enqueteId, Set<Long> indicesActifsIds, HypotheseJoueur hypothese);
}
