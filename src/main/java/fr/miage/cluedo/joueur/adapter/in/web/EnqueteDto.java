package fr.miage.cluedo.joueur.adapter.in.web;

import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;
import fr.miage.cluedo.joueur.domain.ValeurId;
import java.util.List;

/**
 * Enquête telle que le front l'affiche. Les indices n'exposent que leur énoncé : la clause logique
 * reste côté back.
 */
public record EnqueteDto(long id, String titre, List<VariableDto> variables, List<IndiceDto> indices) {

    public record VariableDto(String code, List<String> valeurs) {
    }

    public record IndiceDto(long id, String libelle) {
    }

    static EnqueteDto depuis(EnqueteFiltrage enquete) {
        List<VariableDto> variables = enquete.variables().stream()
                .map(variable -> new VariableDto(
                        variable.id().code(), variable.domaine().stream().map(ValeurId::code).toList()))
                .toList();
        List<IndiceDto> indices = enquete.indices().stream()
                .map(indice -> new IndiceDto(indice.id(), indice.libelle()))
                .toList();
        return new EnqueteDto(enquete.id(), enquete.titre(), variables, indices);
    }
}
