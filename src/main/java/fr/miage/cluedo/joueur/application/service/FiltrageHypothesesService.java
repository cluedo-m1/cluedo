package fr.miage.cluedo.joueur.application.service;

import fr.miage.cluedo.joueur.application.EnqueteIntrouvableException;
import fr.miage.cluedo.joueur.application.port.in.FiltrerHypothesesUseCase;
import fr.miage.cluedo.joueur.application.port.out.EnqueteRepositoryPort;
import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;
import fr.miage.cluedo.joueur.domain.HypotheseJoueur;
import fr.miage.cluedo.joueur.domain.ResultatFiltrage;
import fr.miage.cluedo.joueur.domain.SolveurFiltrageBacktracking;
import java.util.Set;

/** Orchestration : charger l'enquête, retenir les indices activés, déléguer le calcul au solveur. */
public class FiltrageHypothesesService implements FiltrerHypothesesUseCase {

    private final EnqueteRepositoryPort enquetes;
    private final SolveurFiltrageBacktracking solveur;

    public FiltrageHypothesesService(EnqueteRepositoryPort enquetes, SolveurFiltrageBacktracking solveur) {
        this.enquetes = enquetes;
        this.solveur = solveur;
    }

    @Override
    public ResultatFiltrage filtrer(long enqueteId, Set<Long> indicesActifsIds, HypotheseJoueur hypothese) {
        EnqueteFiltrage enquete = enquetes.trouverParId(enqueteId)
                .orElseThrow(() -> new EnqueteIntrouvableException(enqueteId));
        return solveur.filtrer(enquete, enquete.contraintesActives(indicesActifsIds), hypothese);
    }
}
