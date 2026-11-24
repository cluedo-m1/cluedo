package fr.miage.cluedo.solveur.domain.model;
import java.util.Objects;
/*Elle représente un indice du jeu. Un indice relie deux variables et 
dit quelles combinaisons de valeurs sont permises. C'est un petit « contrôleur » 
qui répond oui ou non à : « est-ce que ce couple de valeurs est compatible ? » */
import java.util.function.BiPredicate;

public class Contrainte {
    private final Variable x;
    private final Variable y;
    private final BiPredicate<String,String>regle;
    private final String description;

    public Contrainte(Variable x, Variable y,BiPredicate<String,String> regle, String description){
        this.x = Objects.requireNonNull(x);
        this.y = Objects.requireNonNull(y);
        this.regle = Objects.requireNonNull(regle);
        this.description = Objects.requireNonNull(description);
    }
    
    public boolean autorise(String valeurX, String valeurY) {
        return regle.test(valeurX, valeurY);
    }
    public Contrainte inverse(){
        return new Contrainte(
            y, x, 
            (valeurX, valeurY) ->autorise(valeurY, valeurX), 
            description);
    }
    public Variable getX(){
        return x;
    }
    public Variable getY(){
        return y;
    }
    @Override
    public String toString() {
        return description;
    }
}
