package fr.miage.cluedo.generation.application.port.in;

import fr.miage.cluedo.generation.domain.Enquete;
import fr.miage.cluedo.generation.domain.Suspect;
import fr.miage.cluedo.generation.domain.Lieu;
import fr.miage.cluedo.generation.domain.Horaire;
import fr.miage.cluedo.generation.domain.MoyenAcces;

import java.util.List;

public interface GenererEnqueteUseCase {

    Enquete generer(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces,
            long seed);
}