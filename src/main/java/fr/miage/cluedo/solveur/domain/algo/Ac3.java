package fr.miage.cluedo.solveur.domain.algo;

import java.util.ArrayList;

import fr.miage.cluedo.solveur.domain.model.Contrainte;

public final class Ac3 {

    private Ac3() { }

    /** Retire du domaine de X les valeurs sans support dans Y. @return true si le domaine de X a changé. */
    public static boolean revise(Contrainte c) {
        boolean domaineModifie = false;

        for (String vx : new ArrayList<>(c.getX().getDomaine())) {
            boolean aUnSup = false;
            for(String vy : new ArrayList<>(c.getY().getDomaine())){
                if(c.autorise(vx, vy) == true){
                    aUnSup = true;
                }
            }
            if(aUnSup == false){
                c.getX().retirer(vx);
                domaineModifie = true;
            }
        }

        return domaineModifie;
    }
}