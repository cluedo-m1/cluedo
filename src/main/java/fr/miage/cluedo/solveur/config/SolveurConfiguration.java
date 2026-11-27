package fr.miage.cluedo.solveur.config;

import fr.miage.cluedo.solveur.application.port.in.ResoudreEnqueteUseCase;
import fr.miage.cluedo.solveur.application.service.ResoudreEnqueteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolveurConfiguration {
    @Bean
    public ResoudreEnqueteUseCase resoudreEnqueteUseCase() {
        return new ResoudreEnqueteService();
    }
}
