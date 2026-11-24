package fr.miage.cluedo.solveur.domain.algo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import fr.miage.cluedo.solveur.domain.model.Contrainte;

public final class Ac3 {

    private Ac3() { }

    /** Retire du domaine de X les valeurs sans support dans Y. @return true si le domaine de X a changé. */
    public static boolean revise(Contrainte c) {
        boolean domaineModifie = false;

        for (String vx : new ArrayList<>(c.getX().getDomaine())) {
            boolean aUnSup = false;
            for(String vy : new ArrayList<>(c.getY().getDomaine())){
                if(c.autorise(vx, vy)){
                    aUnSup = true;
                }
            }
            if(!aUnSup){
                c.getX().retirer(vx);
                domaineModifie = true;
            }
        }
        
        return domaineModifie;
    }
    /** Rend le problème cohérent d'arc. @return false si un domaine devient vide (enquête incohérente). */
    public static boolean ac3(List<Contrainte> contraintes) {
        Deque<Contrainte> file = new ArrayDeque<>();
        for (Contrainte c : contraintes) {
            file.add(c);
            file.add(c.inverse());
        }
        List<Contrainte> tousLesArcs = new ArrayList<>(file);

        while (!file.isEmpty()) {
            Contrainte arc = file.poll();
            if(revise(arc)){
                if(arc.getX().estVide()){
                    return false;
                }
                for(Contrainte d : tousLesArcs){
                    if(d.getY() == arc.getX()){
                        file.add(d);
                    }
                }
            }
        }
        return true;
    }
}