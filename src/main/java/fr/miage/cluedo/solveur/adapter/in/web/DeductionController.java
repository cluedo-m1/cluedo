package fr.miage.cluedo.solveur.adapter.in.web;

import fr.miage.cluedo.solveur.application.port.in.ResoudreEnqueteUseCase;
import fr.miage.cluedo.solveur.domain.model.Exclusion;
import fr.miage.cluedo.solveur.domain.model.Implication;
import fr.miage.cluedo.solveur.domain.model.Indice;
import fr.miage.cluedo.solveur.domain.model.Resultat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deduction")
public class DeductionController {

    private final ResoudreEnqueteUseCase useCase;

    public DeductionController(ResoudreEnqueteUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/resoudre")
    public ResolutionResponse resoudre(@RequestBody EnqueteRequest requete) {
        List<Indice> indices = new ArrayList<>();
        for (IndiceDto dto : requete.indices()) {
            if ("EXCLUSION".equals(dto.type())) {
                indices.add(new Exclusion(dto.variable(), dto.valeur()));
            } else if ("IMPLICATION".equals(dto.type())) {
                indices.add(new Implication(dto.variableX(), dto.valeurX(),
                        dto.variableY(), dto.valeursAutoriseesY()));
            }
        }

        Resultat resultat = useCase.resoudre(requete.variables(), indices);

        return new ResolutionResponse(resultat.nombreSolutions(), resultat.domaines());
    }
}