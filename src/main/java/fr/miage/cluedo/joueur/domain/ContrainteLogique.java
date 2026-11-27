package fr.miage.cluedo.joueur.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clause : disjonction (OU) de littéraux. Elle est satisfaite dès qu'un de ses littéraux est vrai.
 * Une exclusion, une disjonction, une implication et une hypothèse du joueur s'écrivent toutes ainsi.
 */
public record ContrainteLogique(List<Litteral> litteraux) {

    public ContrainteLogique {
        if (litteraux == null || litteraux.isEmpty()) {
            throw new EnqueteInvalideException("Une clause doit contenir au moins un littéral");
        }
        litteraux = List.copyOf(litteraux);
    }

    public Set<VariableId> variables() {
        return litteraux.stream().map(Litteral::variable).collect(Collectors.toUnmodifiableSet());
    }
}
