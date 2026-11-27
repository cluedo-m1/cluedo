package fr.miage.cluedo.generation.adapter.in.web;

import fr.miage.cluedo.generation.application.port.in.GenererEnqueteUseCase;
import fr.miage.cluedo.generation.application.port.out.EnqueteRepository;
import fr.miage.cluedo.generation.domain.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/v1/enquetes")
public class GenerationController {

    private final GenererEnqueteUseCase service;
    private final EnqueteRepository repository;

    public GenerationController(
            GenererEnqueteUseCase service,
            EnqueteRepository repository) {

        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<EnqueteResponse> generer(
            @RequestBody GenerationRequest request) {

        long seed = request.seed() != null
                ? request.seed()
                : ThreadLocalRandom.current().nextLong();

        List<Suspect> suspects = List.of(
                new Suspect("Alice",
                        Set.of(MoyenAcces.CLE),
                        Set.of(Horaire.NEUF_HEURES,
                               Horaire.DIX_HEURES)),

                new Suspect("Thomas",
                        Set.of(MoyenAcces.BADGE),
                        Set.of(Horaire.DIX_HEURES,
                               Horaire.ONZE_HEURES)),

                new Suspect("Emma",
                        Set.of(MoyenAcces.CLE,
                               MoyenAcces.BADGE),
                        Set.of(Horaire.values())),

                new Suspect("Lucas",
                        Set.of(),
                        Set.of(Horaire.NEUF_HEURES,
                               Horaire.ONZE_HEURES))
        );

        List<Lieu> lieux = List.of(
                new Lieu("Bibliothèque",
                        MoyenAcces.ACCES_LIBRE),
                new Lieu("Secrétariat",
                        MoyenAcces.CLE),
                new Lieu("Salle informatique",
                        MoyenAcces.BADGE)
        );

        Enquete enquete = service.generer(
                suspects,
                lieux,
                List.of(Horaire.values()),
                List.of(MoyenAcces.values()),
                seed);

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/enquetes/" + enquete.id()))
                .body(EnqueteResponse.depuis(enquete));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnqueteResponse> consulter(
            @PathVariable UUID id) {

        return repository.trouverParId(id)
                .map(EnqueteResponse::depuis)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }
}