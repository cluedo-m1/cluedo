package fr.miage.cluedo.generation.adapter.in.web;

import fr.miage.cluedo.generation.domain.*;

import java.util.List;
import java.util.UUID;

public record EnqueteResponse(
        UUID id,
        String titre,
        String description,
        List<Suspect> suspects,
        List<Lieu> lieux,
        List<Horaire> horaires,
        List<MoyenAcces> moyensAcces,
        List<Indice> indices) {

    public static EnqueteResponse depuis(Enquete enquete) {
        return new EnqueteResponse(
                enquete.id(),
                "Fuite d'un sujet d'examen",
                "Un sujet d'examen a disparu sur le campus. "
                    + "Identifiez le responsable, le lieu, "
                    + "l'heure et le moyen d'acces.",
                enquete.suspects(),
                enquete.lieux(),
                enquete.horaires(),
                enquete.moyensAcces(),
                enquete.indices());
    }
}