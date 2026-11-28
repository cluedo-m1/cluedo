package fr.miage.cluedo.campus.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Test de bout en bout : vraie appli, vraie migration V4, vraies requêtes HTTP. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // les données insérées sont annulées après chaque test
class VerificationTrajetApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    private void relier(String a, String b, int duree) {
        jdbc.update("INSERT INTO connexion (depart, arrivee, duree) VALUES (?, ?, ?)", a, b, duree);
        jdbc.update("INSERT INTO connexion (depart, arrivee, duree) VALUES (?, ?, ?)", b, a, duree);
    }

    @BeforeEach
    void campusDeLaFiche() {
        jdbc.update("INSERT INTO batiment (code, nom) VALUES "
                + "('BU', 'Bibliothèque'), ('RESTO', 'Restaurant'), "
                + "('AMPHI', 'Amphithéâtre'), ('LABO', 'Laboratoire')");
        relier("BU", "AMPHI", 4);
        relier("BU", "RESTO", 2);
        relier("RESTO", "AMPHI", 1);
        relier("AMPHI", "LABO", 3);
        relier("RESTO", "LABO", 7);
    }

    @Test
    void trajetImpossible_repond200_avecLaDureeMinimale() throws Exception {
        mockMvc.perform(get("/api/v1/campus/trajets/verification")
                        .param("depart", "BU").param("arrivee", "LABO").param("duree", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.possible").value(false))
                .andExpect(jsonPath("$.dureeMinimale").value(6));
    }

    @Test
    void batimentInconnu_repond404() throws Exception {
        mockMvc.perform(get("/api/v1/campus/trajets/verification")
                        .param("depart", "BU")
                        .param("arrivee", "XYZ")
                        .param("duree", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void dureeNegative_repond400() throws Exception {
        mockMvc.perform(get("/api/v1/campus/trajets/verification")
                        .param("depart", "BU")
                        .param("arrivee", "LABO")
                        .param("duree", "-1"))
                .andExpect(status().isBadRequest());
    }
}