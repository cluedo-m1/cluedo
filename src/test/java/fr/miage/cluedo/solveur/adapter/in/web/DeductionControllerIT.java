package fr.miage.cluedo.solveur.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class DeductionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void resoutLEnqueteDeLaMatinee() throws Exception {
        String json = """
                {
                  "variables": {
                    "Coupable": ["Alice", "Bob", "Chloé"],
                    "Lieu": ["salle", "imprimante", "serveur"]
                  },
                  "indices": [
                    {"type": "EXCLUSION", "variable": "Lieu", "valeur": "salle"},
                    {"type": "IMPLICATION", "variableX": "Coupable", "valeurX": "Alice",
                     "variableY": "Lieu", "valeursAutoriseesY": ["salle"]},
                    {"type": "IMPLICATION", "variableX": "Coupable", "valeurX": "Chloé",
                     "variableY": "Lieu", "valeursAutoriseesY": ["salle"]}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/deduction/resoudre")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombreSolutions").value(2))
        .andExpect(jsonPath("$.domaines.Coupable[0]").value("Bob"));
    }
    @Test 
    void envoie400QuandLaVariableEstInconnue() throws Exception {
        String json = """
                {
                  "variables": {
                    "Coupable": ["Alice", "Bob", "Chloé"],
                    "Lieu": ["salle", "imprimante", "serveur"]
                  },
                  "indices": [
                    {"type": "EXCLUSION", "variable": "Lieux", "valeur": "salle"},
                    {"type": "IMPLICATION", "variableX": "Coupable", "valeurX": "Alice",
                     "variableY": "Lieu", "valeursAutoriseesY": ["salle"]},
                    {"type": "IMPLICATION", "variableX": "Coupable", "valeurX": "Chloé",
                     "variableY": "Lieu", "valeursAutoriseesY": ["salle"]}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/deduction/resoudre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("Lieux")));
    }

}