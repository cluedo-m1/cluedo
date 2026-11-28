package fr.miage.cluedo.campus.adapter.in.web;

import fr.miage.cluedo.campus.application.port.in.ResultatVerification;
import fr.miage.cluedo.campus.application.port.in.VerificationTrajetUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/campus")
class VerificationTrajetController {

    private final VerificationTrajetUseCase verification;

    VerificationTrajetController(VerificationTrajetUseCase verification) {
        this.verification = verification;
    }

    /** Exemple : GET /api/v1/campus/trajets/verification?depart=BU&arrivee=LABO&duree=5 */
    @GetMapping("/trajets/verification")
    VerificationTrajetReponse verifier(@RequestParam String depart,
                                       @RequestParam String arrivee,
                                       @RequestParam int duree) {
        ResultatVerification resultat = verification.verifier(depart, arrivee, duree);
        return VerificationTrajetReponse.depuis(depart, arrivee, duree, resultat);
    }
}