package fr.miage.cluedo.joueur.adapter.in.web;

import fr.miage.cluedo.joueur.application.port.in.ConsulterEnqueteUseCase;
import fr.miage.cluedo.joueur.application.port.in.FiltrerHypothesesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API du module « joueur » : consulter une enquête et filtrer les scénarios compatibles. */
@RestController
@RequestMapping("/api/v1/joueur/enquetes")
public class JoueurController {

    private final ConsulterEnqueteUseCase consulterEnquete;
    private final FiltrerHypothesesUseCase filtrerHypotheses;

    public JoueurController(ConsulterEnqueteUseCase consulterEnquete, FiltrerHypothesesUseCase filtrerHypotheses) {
        this.consulterEnquete = consulterEnquete;
        this.filtrerHypotheses = filtrerHypotheses;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une enquête : variables, domaines de valeurs et indices disponibles")
    public EnqueteDto consulter(@PathVariable long id) {
        return EnqueteDto.depuis(consulterEnquete.consulter(id));
    }

    /**
     * Pas de persistance : le calcul est refait à chaque appel, donc 200 (et non 201). L'URL désigne la
     * ressource « filtrages » de l'enquête.
     */
    @PostMapping("/{id}/filtrages")
    @Operation(summary = "Dénombrer les scénarios compatibles avec les indices actifs et l'hypothèse du joueur")
    public FiltrageReponse filtrer(@PathVariable long id, @Valid @RequestBody FiltrageRequete requete) {
        return FiltrageReponse.depuis(
                filtrerHypotheses.filtrer(id, requete.indicesActifsIds(), requete.versHypothese()));
    }
}
