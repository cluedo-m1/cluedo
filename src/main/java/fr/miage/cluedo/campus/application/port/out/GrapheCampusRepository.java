package fr.miage.cluedo.campus.application.port.out;

import fr.miage.cluedo.campus.domain.model.Batiment;
import fr.miage.cluedo.campus.domain.model.Connexion;

import java.util.List;
import java.util.Optional;

/** Port de sortie : lecture du campus (bâtiments et connexions). */
public interface GrapheCampusRepository {

    /** Le bâtiment de ce code, ou un Optional vide s'il n'existe pas. */
    Optional<Batiment> batiment(String code);

    List<Connexion> connexions();
}