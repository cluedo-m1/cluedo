package fr.miage.cluedo.joueur.adapter.in.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:joueur_web_it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
class JoueurControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void a1_get_enquete_1_retourne_variables_valeurs_et_indices_sans_clause() throws Exception {
        mockMvc.perform(get("/api/v1/joueur/enquetes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titre", is("Fuite du sujet d'examen de POO")))
                .andExpect(jsonPath("$.variables", hasSize(3)))
                .andExpect(jsonPath("$.variables[0].code", is("SUSPECT")))
                .andExpect(jsonPath("$.variables[1].code", is("LIEU")))
                .andExpect(jsonPath("$.variables[2].code", is("OBJET")))
                .andExpect(jsonPath("$.variables[0].valeurs", hasSize(6)))
                .andExpect(jsonPath("$.variables[1].valeurs", hasSize(6)))
                .andExpect(jsonPath("$.variables[2].valeurs", hasSize(6)))
                .andExpect(jsonPath("$.indices", hasSize(5)))
                .andExpect(jsonPath("$.indices[*]", everyItem(not(hasKey("clause")))))
                .andExpect(jsonPath("$.indices[*]", everyItem(not(hasKey("contrainte")))))
                .andExpect(jsonPath("$.indices[*]", everyItem(not(hasKey("litteraux")))));
    }

    @Test
    void a2_get_enquete_inconnue_retourne_404_problem_json() throws Exception {
        mockMvc.perform(get("/api/v1/joueur/enquetes/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.title").isNotEmpty());
    }

    @Test
    void a3_get_id_non_numerique_retourne_400() throws Exception {
        mockMvc.perform(get("/api/v1/joueur/enquetes/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void a4_post_sans_contrainte_retourne_216_scenarios_et_36_par_valeur() throws Exception {
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [],
                                  "hypothese": {}
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScenariosCompatibles", is(216)))
                .andExpect(jsonPath("$.comptageParVariable", hasSize(3)))
                .andExpect(jsonPath("$.comptageParVariable[0].valeurs", hasSize(6)))
                .andExpect(jsonPath("$.comptageParVariable[0].valeurs[*].scenarios", everyItem(is(36))))
                .andExpect(jsonPath("$.comptageParVariable[1].valeurs[*].scenarios", everyItem(is(36))))
                .andExpect(jsonPath("$.comptageParVariable[2].valeurs[*].scenarios", everyItem(is(36))));
    }

    @Test
    void a5_post_avec_les_5_indices_retourne_18_scenarios() throws Exception {
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [1, 2, 3, 4, 5],
                                  "hypothese": {}
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScenariosCompatibles", is(18)))
                .andExpect(jsonPath("$.noeudsVisites", is(180)));
    }

    @Test
    void a6_post_avec_hypothese_reduit_espace_a_36_scenarios() throws Exception {
        MvcResult getResult = mockMvc.perform(get("/api/v1/joueur/enquetes/1"))
                .andExpect(status().isOk())
                .andReturn();
        String premierSuspect = JsonPath.read(
                getResult.getResponse().getContentAsString(),
                "$.variables[0].valeurs[0]");

        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [],
                                  "hypothese": {
                                    "SUSPECT": "%s"
                                  }
                                }
                                """.formatted(premierSuspect)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScenariosCompatibles", is(36)))
                .andExpect(jsonPath("$.comptageParVariable[0].valeurs[0].scenarios", is(36)))
                .andExpect(jsonPath("$.comptageParVariable[0].valeurs[1].scenarios", is(0)))
                .andExpect(jsonPath("$.comptageParVariable[1].valeurs[*].scenarios", everyItem(is(6))));
    }

    @Test
    void a7_post_sans_champ_hypothese_donne_meme_resultat_que_vide() throws Exception {
        MvcResult sansChamp = mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [1, 2]
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult avecVide = mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [1, 2],
                                  "hypothese": {}
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(sansChamp.getResponse().getContentAsString())
                .isEqualTo(avecVide.getResponse().getContentAsString());
    }

    @Test
    void a8_post_enquete_inconnue_retourne_404() throws Exception {
        mockMvc.perform(post("/api/v1/joueur/enquetes/999/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": []
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void a9_post_indice_inconnu_retourne_400_requete_invalide() throws Exception {
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [999]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title", is("Requête invalide")));
    }

    @Test
    void a10_post_variable_inconnue_valeur_hors_domaine_ou_vide_retourne_400() throws Exception {
        // Variable inconnue
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [],
                                  "hypothese": { "METEO": "PLUIE" }
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Valeur hors domaine
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [],
                                  "hypothese": { "SUSPECT": "INCONNU" }
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Valeur vide
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": [],
                                  "hypothese": { "SUSPECT": "" }
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void a11_corps_sans_indices_json_mal_forme_ou_id_invalide_retourne_400_problem_json() throws Exception {
        // Sans indicesActifsIds
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

        // JSON mal formé
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ json invalide"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

        // ID non numérique dans l'URL
        mockMvc.perform(post("/api/v1/joueur/enquetes/abc/filtrages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicesActifsIds": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void a12_post_sans_content_type_retourne_415() throws Exception {
        mockMvc.perform(post("/api/v1/joueur/enquetes/1/filtrages")
                        .content("""
                                {
                                  "indicesActifsIds": []
                                }
                                """))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @Transactional
    void a13_indice_corrompu_en_base_retourne_500_sans_detail_technique() throws Exception {
        jdbc.update(
                "INSERT INTO joueur_indice (id, enquete_id, libelle) VALUES (?, ?, ?)",
                999L, 1L, "Indice sans littéral");

        mockMvc.perform(get("/api/v1/joueur/enquetes/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail", not(containsString("IllegalStateException"))))
                .andExpect(jsonPath("$.detail", not(containsString("fr.miage"))));
    }

    @Test
    void a14_openapi_expose_les_deux_endpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths", hasKey("/api/v1/joueur/enquetes/{id}")))
                .andExpect(jsonPath("$.paths", hasKey("/api/v1/joueur/enquetes/{id}/filtrages")));
    }
}