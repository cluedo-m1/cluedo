package fr.miage.cluedo.joueur.application.service;

import fr.miage.cluedo.joueur.application.EnqueteIntrouvableException;
import fr.miage.cluedo.joueur.application.port.in.ConsulterEnqueteUseCase;
import fr.miage.cluedo.joueur.application.port.out.EnqueteRepositoryPort;
import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;

public class ConsultationEnqueteService implements ConsulterEnqueteUseCase {

    private final EnqueteRepositoryPort enquetes;

    public ConsultationEnqueteService(EnqueteRepositoryPort enquetes) {
        this.enquetes = enquetes;
    }

    @Override
    public EnqueteFiltrage consulter(long enqueteId) {
        return enquetes.trouverParId(enqueteId).orElseThrow(() -> new EnqueteIntrouvableException(enqueteId));
    }
}
