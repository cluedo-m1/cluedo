package fr.miage.cluedo.generation.domain;

import java.util.List;

public final class VerificateurUnicite {

    public int compterSolutions(
            List<Suspect> suspects,
            List<Lieu> lieux,
            List<Horaire> horaires,
            List<MoyenAcces> moyensAcces,
            List<Indice> indices) {

        // Seuls les indices DIFFERENT sont pris en charge en v0.1.0.
        for (Indice indice : indices) {
            if (indice.operateur() != Indice.Operateur.DIFFERENT) {
                throw new IllegalArgumentException(
                        "Operateur non pris en charge");
            }
        }

        int compteur = 0;

        for (Suspect suspect : suspects) {
            for (Lieu lieu : lieux) {

                // Verification de l'autorisation d'acces
                if (!Contrainte.peutAcceder(suspect, lieu)) {
                    continue;
                }

                for (Horaire horaire : horaires) {

                    // Verification de la presence du suspect
                    if (!Contrainte.estPresent(suspect, horaire)) {
                        continue;
                    }

                    for (MoyenAcces moyen : moyensAcces) {

                        // Verification du moyen d'acces
                        if (lieu.moyenAccesRequis() != moyen) {
                            continue;
                        }

                        Solution candidate = new Solution(
                                suspect, lieu, horaire, moyen);

                        // Verification des indices
                        if (respecteIndices(candidate, indices)) {
                            compteur++;

                            // Arret anticipe si plusieurs solutions
                            if (compteur >= 2) {
                                return 2;
                            }
                        }
                    }
                }
            }
        }

        return compteur;
    }

    private boolean respecteIndices(
            Solution candidate,
            List<Indice> indices) {

        for (Indice indice : indices) {

            String valeurCandidate = switch (indice.variable()) {
                case SUSPECT -> candidate.coupable().nom();
                case LIEU -> candidate.lieu().nom();
                case HORAIRE -> candidate.horaire().name();
                case MOYEN_ACCES -> candidate.moyenAcces().name();
            };

            // Un indice DIFFERENT interdit une valeur precise
            if (valeurCandidate.equals(indice.valeur())) {
                return false;
            }
        }

        return true;
    }
}