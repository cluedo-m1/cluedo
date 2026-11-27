package fr.miage.cluedo.campus.config;

import fr.miage.cluedo.campus.application.port.in.VerificationTrajetUseCase;
import fr.miage.cluedo.campus.application.port.out.GrapheCampusRepository;
import fr.miage.cluedo.campus.application.service.VerificationTrajetService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Assemblage : c'est ici, et seulement ici, que le service est branché sur l'adaptateur. */
@Configuration
class CampusConfiguration {

    @Bean
    VerificationTrajetUseCase verificationTrajetUseCase(GrapheCampusRepository campus) {
        return new VerificationTrajetService(campus);
    }
}