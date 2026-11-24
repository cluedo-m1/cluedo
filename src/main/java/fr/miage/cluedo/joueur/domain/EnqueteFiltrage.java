package fr.miage.cluedo.joueur.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enquête jouable : variables ordonnées (l'ordre est celui du parcours du retour arrière) et indices.
 * Tous les littéraux des indices sont vérifiés à la construction.
 */
public record EnqueteFiltrage(long id, String titre, List<VariableEnquete> variables, List<IndiceEnquete> indices) {

    public EnqueteFiltrage {
        if (titre == null || titre.isBlank()) {
            throw new EnqueteInvalideException("Une enquête exige un titre");
        }
        if (variables == null || variables.isEmpty()) {
            throw new EnqueteInvalideException("Une enquête exige au moins une variable");
        }
        variables = List.copyOf(variables);
        indices = indices == null ? List.of() : List.copyOf(indices);

        Set<VariableId> vues = new HashSet<>();
        for (VariableEnquete variable : variables) {
            if (!vues.add(variable.id())) {
                throw new EnqueteInvalideException("Variable déclarée deux fois : " + variable.id().code());
            }
        }
        for (IndiceEnquete indice : indices) {
            verifierCoherence(variables, indice.contrainte());
        }
    }

    /**
     * Clauses des indices que le joueur a activés.
     *
     * @throws EnqueteInvalideException si un id ne désigne pas un indice de CETTE enquête : cela révèle un bug
     *                                  du client, le taire fausserait silencieusement le dénombrement
     */
    public List<ContrainteLogique> contraintesActives(Set<Long> indicesActifsIds) {
        if (indicesActifsIds == null) {
            throw new EnqueteInvalideException("La liste des indices actifs est obligatoire");
        }
        Map<Long, ContrainteLogique> contrainteParIndice = new HashMap<>();
        for (IndiceEnquete indice : indices) {
            contrainteParIndice.put(indice.id(), indice.contrainte());
        }
        List<ContrainteLogique> actives = new ArrayList<>(indicesActifsIds.size());
        for (Long indiceId : indicesActifsIds) {
            ContrainteLogique contrainte = contrainteParIndice.get(indiceId);
            if (contrainte == null) {
                throw new EnqueteInvalideException("L'indice " + indiceId + " n'appartient pas à l'enquête " + id);
            }
            actives.add(contrainte);
        }
        return actives;
    }

    /** Lève {@link EnqueteInvalideException} si la clause cite une variable inconnue ou une valeur hors domaine. */
    public void verifierCoherence(ContrainteLogique clause) {
        verifierCoherence(variables, clause);
    }

    private static void verifierCoherence(List<VariableEnquete> variables, ContrainteLogique clause) {
        Map<VariableId, VariableEnquete> parId =
                variables.stream().collect(Collectors.toMap(VariableEnquete::id, Function.identity()));
        for (Litteral litteral : clause.litteraux()) {
            VariableEnquete variable = parId.get(litteral.variable());
            if (variable == null) {
                throw new EnqueteInvalideException("Variable inconnue : " + litteral.variable().code());
            }
            if (!variable.contient(litteral.valeur())) {
                throw new EnqueteInvalideException("Valeur « " + litteral.valeur().code()
                        + " » hors du domaine de la variable " + litteral.variable().code());
            }
        }
    }
}
