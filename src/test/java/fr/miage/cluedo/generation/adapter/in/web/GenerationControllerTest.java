package fr.miage.cluedo.generation.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@SpringBootTest
class GenerationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc() {
        return MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void doitGenererUneEnquete() throws Exception {

        mvc().perform(post("/api/v1/enquetes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seed\":42}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.suspects.length()").value(4))
                .andExpect(jsonPath("$.lieux.length()").value(3))
                .andExpect(jsonPath("$.indices").isNotEmpty());
    }

    @Test
    void neDoitPasExposerLaSolution() throws Exception {

        mvc().perform(post("/api/v1/enquetes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.solution").doesNotExist())
                .andExpect(jsonPath("$.seed").doesNotExist());
    }

    @Test
    void doitRetrouverUneEnqueteGeneree() throws Exception {

        String location = mvc()
                .perform(post("/api/v1/enquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"seed\":42}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mvc().perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.indices").isNotEmpty())
                .andExpect(jsonPath("$.solution").doesNotExist());
    }

    @Test
    void doitRetourner404PourUneEnqueteInconnue()
            throws Exception {

        mvc().perform(get("/api/v1/enquetes/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void doitRefuserUneSeedInvalide() throws Exception {

        mvc().perform(post("/api/v1/enquetes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seed\":\"abc\"}"))
                .andExpect(status().isBadRequest());
    }
}