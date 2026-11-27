package fr.miage.cluedo.generation.domain;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Enquete(
        UUID id,
        List<Suspect> suspects,
        List<Lieu> lieux,
        List<Horaire> horaires,
        List<MoyenAcces> moyensAcces,
        List<Indice> indices,
        Solution solution,
        long seed
) {

    public Enquete {
        Objects.requireNonNull(id);
        Objects.requireNonNull(solution);

        suspects = List.copyOf(suspects);
        lieux = List.copyOf(lieux);
        horaires = List.copyOf(horaires);
        moyensAcces = List.copyOf(moyensAcces);
        indices = List.copyOf(indices);
    }
}