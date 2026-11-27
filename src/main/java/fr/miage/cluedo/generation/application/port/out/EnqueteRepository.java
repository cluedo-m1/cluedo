package fr.miage.cluedo.generation.application.port.out;

import fr.miage.cluedo.generation.domain.Enquete;

import java.util.Optional;
import java.util.UUID;

public interface EnqueteRepository {

    void sauvegarder(Enquete enquete);

    Optional<Enquete> trouverParId(UUID id);
}