package fr.miage.cluedo.solveur.domain.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** Une variable du problème (ex. « Coupable ») et l'ensemble de ses valeurs encore possibles. */
public class Variable {

    private final String nom;
    private final Set<String> domaine;

    public Variable(String nom, Set<String> valeurs) {
        this.nom = Objects.requireNonNull(nom);
        // copie défensive : on ne garde pas l'ensemble de l'appelant (voir point 2 ci-dessous)
        this.domaine = new LinkedHashSet<>(Objects.requireNonNull(valeurs));
    }

    /** @return true si la valeur était présente et a été retirée, false si le domaine n'a pas changé. */
    public boolean retirer(String valeur) {
        return domaine.remove(valeur);
    }

    public boolean estVide() {
        return domaine.isEmpty();
    }

    public boolean contient(String valeur) {
        return domaine.contains(valeur);
    }

    public int taille() {
        return domaine.size();
    }

    /** Vue en lecture seule : personne ne modifie le domaine sans passer par {@link #retirer}. */
    public Set<String> getDomaine() {
        return Collections.unmodifiableSet(domaine);
    }

    public String getNom() {
        return nom;
    }

    /** Copie indépendante, utile pour le retour arrière (on essaie sur une copie, on jette si ça échoue). */
    public Variable copie() {
        return new Variable(nom, domaine);
    }
}