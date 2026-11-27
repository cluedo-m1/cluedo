package fr.miage.cluedo.joueur.domain;

/**
 * Condition atomique sur une variable : « variable = valeur » (positif) ou « variable != valeur » (négatif).
 */
public record Litteral(VariableId variable, ValeurId valeur, boolean positif) {

    public Litteral {
        if (variable == null || valeur == null) {
            throw new EnqueteInvalideException("Un littéral exige une variable et une valeur");
        }
    }

    /** @param valeurAssignee la valeur prise par {@link #variable()} dans le scénario évalué */
    public boolean estVrai(ValeurId valeurAssignee) {
        boolean egale = valeur.equals(valeurAssignee);
        return positif ? egale : !egale;
    }
}
