package fr.miage.cluedo.joueur.domain;

import java.util.HashSet;
import java.util.List;

/** Une dimension de l'enquête avec son domaine fini de valeurs candidates, dans un ordre fixe. */
public record VariableEnquete(VariableId id, List<ValeurId> domaine) {

    public VariableEnquete {
        if (id == null) {
            throw new EnqueteInvalideException("Une variable exige un identifiant");
        }
        if (domaine == null || domaine.isEmpty()) {
            throw new EnqueteInvalideException("Le domaine de la variable " + id.code() + " ne peut pas être vide");
        }
        domaine = List.copyOf(domaine);
        if (new HashSet<>(domaine).size() != domaine.size()) {
            throw new EnqueteInvalideException("Le domaine de la variable " + id.code() + " contient un doublon");
        }
    }

    public boolean contient(ValeurId valeur) {
        return domaine.contains(valeur);
    }
}
