package fr.miage.cluedo.joueur.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface VariableJpaRepository extends JpaRepository<VariableJpa, Long> {

    List<VariableJpa> findByEnqueteIdOrderByOrdre(long enqueteId);
}
