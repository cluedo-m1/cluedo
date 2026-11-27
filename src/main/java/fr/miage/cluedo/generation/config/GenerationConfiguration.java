package fr.miage.cluedo.generation.config;

import fr.miage.cluedo.generation.application.port.in.GenererEnqueteUseCase;
import fr.miage.cluedo.generation.application.port.out.EnqueteRepository;
import fr.miage.cluedo.generation.application.service.ServiceGenerationEnquete;
import fr.miage.cluedo.generation.domain.GenerateurAffaire;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenerationConfiguration {

    @Bean
    public GenerateurAffaire generateurAffaire() {
        return new GenerateurAffaire();
    }

    @Bean
    public GenererEnqueteUseCase genererEnqueteUseCase(
            GenerateurAffaire generateur,
            EnqueteRepository repository) {

        return new ServiceGenerationEnquete(
                generateur, repository);
    }
}