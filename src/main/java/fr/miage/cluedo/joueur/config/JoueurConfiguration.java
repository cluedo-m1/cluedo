package fr.miage.cluedo.joueur.config;

import fr.miage.cluedo.joueur.application.port.in.ConsulterEnqueteUseCase;
import fr.miage.cluedo.joueur.application.port.in.FiltrerHypothesesUseCase;
import fr.miage.cluedo.joueur.application.port.out.EnqueteRepositoryPort;
import fr.miage.cluedo.joueur.application.service.ConsultationEnqueteService;
import fr.miage.cluedo.joueur.application.service.FiltrageHypothesesService;
import fr.miage.cluedo.joueur.domain.SolveurFiltrageBacktracking;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Assemblage du module « joueur » : le domaine et l'application n'ont aucune annotation Spring. */
@Configuration
class JoueurConfiguration {

    @Bean
    SolveurFiltrageBacktracking solveurFiltrageBacktracking() {
        return new SolveurFiltrageBacktracking();
    }

    @Bean
    FiltrerHypothesesUseCase filtrerHypothesesUseCase(
            EnqueteRepositoryPort enquetes, SolveurFiltrageBacktracking solveur) {
        return new FiltrageHypothesesService(enquetes, solveur);
    }

    @Bean
    ConsulterEnqueteUseCase consulterEnqueteUseCase(EnqueteRepositoryPort enquetes) {
        return new ConsultationEnqueteService(enquetes);
    }
}
