package fr.miage.cluedo.joueur.adapter.out.persistence;

import fr.miage.cluedo.joueur.application.port.out.EnqueteRepositoryPort;
import fr.miage.cluedo.joueur.domain.ContrainteLogique;
import fr.miage.cluedo.joueur.domain.EnqueteFiltrage;
import fr.miage.cluedo.joueur.domain.EnqueteInvalideException;
import fr.miage.cluedo.joueur.domain.IndiceEnquete;
import fr.miage.cluedo.joueur.domain.Litteral;
import fr.miage.cluedo.joueur.domain.ValeurId;
import fr.miage.cluedo.joueur.domain.VariableEnquete;
import fr.miage.cluedo.joueur.domain.VariableId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptateur de sortie : reconstruit l'enquête du domaine à partir des tables joueur_*.
 * Cinq requêtes au total (pas de N+1), quel que soit le nombre de variables ou d'indices.
 */
@Component
class EnquetePersistenceAdapter implements EnqueteRepositoryPort {

    private final EnqueteJpaRepository enquetes;
    private final VariableJpaRepository variables;
    private final ValeurDomaineJpaRepository valeurs;
    private final IndiceJpaRepository indices;
    private final LitteralJpaRepository litteraux;

    EnquetePersistenceAdapter(
            EnqueteJpaRepository enquetes,
            VariableJpaRepository variables,
            ValeurDomaineJpaRepository valeurs,
            IndiceJpaRepository indices,
            LitteralJpaRepository litteraux) {
        this.enquetes = enquetes;
        this.variables = variables;
        this.valeurs = valeurs;
        this.indices = indices;
        this.litteraux = litteraux;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnqueteFiltrage> trouverParId(long id) {
        return enquetes.findById(id).map(enquete -> chargerEnVerifiant(enquete, id));
    }

    // Une enquête incohérente en base n'est pas une requête invalide (400) : c'est une erreur serveur (500).
    private EnqueteFiltrage chargerEnVerifiant(EnqueteJpa enquete, long id) {
        try {
            return charger(enquete);
        } catch (EnqueteInvalideException exception) {
            throw new IllegalStateException("Données incohérentes pour l'enquête " + id, exception);
        }
    }

    private EnqueteFiltrage charger(EnqueteJpa enquete) {
        List<VariableJpa> lignesVariables = variables.findByEnqueteIdOrderByOrdre(enquete.getId());
        List<Long> variableIds = lignesVariables.stream().map(VariableJpa::getId).toList();

        Map<Long, ValeurId> valeurParId = new HashMap<>();
        Map<Long, List<ValeurId>> domaineParVariable = new HashMap<>();
        for (ValeurDomaineJpa ligne : valeurs.findByVariableIdInOrderByOrdre(variableIds)) {
            ValeurId valeur = new ValeurId(ligne.getCode());
            valeurParId.put(ligne.getId(), valeur);
            domaineParVariable.computeIfAbsent(ligne.getVariableId(), cle -> new ArrayList<>()).add(valeur);
        }

        Map<Long, VariableId> variableParId = new HashMap<>();
        List<VariableEnquete> variablesDomaine = new ArrayList<>();
        for (VariableJpa ligne : lignesVariables) {
            VariableId variable = new VariableId(ligne.getCode());
            variableParId.put(ligne.getId(), variable);
            variablesDomaine.add(new VariableEnquete(variable, domaineParVariable.getOrDefault(ligne.getId(), List.of())));
        }

        return new EnqueteFiltrage(
                enquete.getId(),
                enquete.getTitre(),
                variablesDomaine,
                chargerIndices(enquete.getId(), variableParId, valeurParId));
    }

    private List<IndiceEnquete> chargerIndices(
            long enqueteId, Map<Long, VariableId> variableParId, Map<Long, ValeurId> valeurParId) {
        List<IndiceJpa> lignesIndices = indices.findByEnqueteIdOrderById(enqueteId);
        if (lignesIndices.isEmpty()) {
            return List.of();
        }

        Map<Long, List<Litteral>> litterauxParIndice = new HashMap<>();
        List<Long> indiceIds = lignesIndices.stream().map(IndiceJpa::getId).toList();
        for (LitteralJpa ligne : litteraux.findByIndiceIdInOrderById(indiceIds)) {
            Litteral litteral = new Litteral(
                    variableParId.get(ligne.getVariableId()), valeurParId.get(ligne.getValeurId()), ligne.isPositif());
            litterauxParIndice.computeIfAbsent(ligne.getIndiceId(), cle -> new ArrayList<>()).add(litteral);
        }

        return lignesIndices.stream()
                .map(ligne -> new IndiceEnquete(
                        ligne.getId(),
                        ligne.getLibelle(),
                        new ContrainteLogique(litterauxParIndice.getOrDefault(ligne.getId(), List.of()))))
                .toList();
    }
}
