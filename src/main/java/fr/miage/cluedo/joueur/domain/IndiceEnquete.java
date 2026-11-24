package fr.miage.cluedo.joueur.domain;

/** Un indice recueilli par le joueur, traduit en clause logique. Le joueur peut l'activer ou le désactiver. */
public record IndiceEnquete(long id, String libelle, ContrainteLogique contrainte) {

    public IndiceEnquete {
        if (libelle == null || libelle.isBlank()) {
            throw new EnqueteInvalideException("Un indice exige un libellé");
        }
        if (contrainte == null) {
            throw new EnqueteInvalideException("Un indice exige une contrainte");
        }
    }
}
