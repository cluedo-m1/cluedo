package fr.miage.cluedo.campus.application.port.in;

import java.util.OptionalInt;

/** possible : le verdict ; dureeMinimale : vide si aucun chemin ne relie les deux bâtiments. */
public record ResultatVerification(boolean possible, OptionalInt dureeMinimale) { }