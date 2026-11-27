package fr.miage.cluedo.generation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class GenerateurAffaire {

    public Optional<Enquete> generer(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces,
            long seed) {

        // 1. Generer une solution secrete
        Optional<Solution> resultat =
                new GenerateurEnquete(seed).generer(
                        suspects, lieux, horaires, moyensAcces);

        if (resultat.isEmpty()) {
            return Optional.empty();
        }

        Solution solution = resultat.get();

        // 2. Generer les indices compatibles
        List<Indice> indices = new GenerateurIndices().generer(
                solution, suspects, lieux, horaires, moyensAcces);

        // 3. Verifier l'unicite de la solution
        int nombreSolutions =
                new VerificateurUnicite().compterSolutions(
                        suspects, lieux, horaires,
                        moyensAcces, indices);

        if (nombreSolutions != 1) {
            return Optional.empty();
        }

        // 4. Construire l'enquete complete
        Enquete enquete = new Enquete(
                UUID.randomUUID(),
                suspects,
                lieux,
                horaires,
                moyensAcces,
                indices,
                solution,
                seed);

        return Optional.of(enquete);
    }
}