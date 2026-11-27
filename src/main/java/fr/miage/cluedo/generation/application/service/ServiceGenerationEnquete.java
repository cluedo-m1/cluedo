package fr.miage.cluedo.generation.application.service;

import fr.miage.cluedo.generation.application.port.in.GenererEnqueteUseCase;
import fr.miage.cluedo.generation.application.port.out.EnqueteRepository;

import fr.miage.cluedo.generation.domain.Enquete;
import fr.miage.cluedo.generation.domain.GenerateurAffaire;
import fr.miage.cluedo.generation.domain.Suspect;
import fr.miage.cluedo.generation.domain.Lieu;
import fr.miage.cluedo.generation.domain.Horaire;
import fr.miage.cluedo.generation.domain.MoyenAcces;

import java.util.List;
import java.util.Objects;

public final class ServiceGenerationEnquete
        implements GenererEnqueteUseCase {

    private final GenerateurAffaire generateur;
    private final EnqueteRepository repository;

    public ServiceGenerationEnquete(
            GenerateurAffaire generateur,
            EnqueteRepository repository) {

        this.generateur = Objects.requireNonNull(generateur);
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Enquete generer(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces,
            long seed) {

        // 1. Generer une enquete valide
        Enquete enquete = generateur.generer(
                suspects,
                lieux,
                horaires,
                moyensAcces,
                seed
        ).orElseThrow(() ->
                new GenerationImpossibleException(
                        "Impossible de generer une enquete a solution unique."
                ));

        // 2. Sauvegarder l'enquete
        repository.sauvegarder(enquete);

        // 3. Retourner l'enquete
        return enquete;
    }
}