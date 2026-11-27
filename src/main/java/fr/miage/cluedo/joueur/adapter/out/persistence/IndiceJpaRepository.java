package fr.miage.cluedo.joueur.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface IndiceJpaRepository extends JpaRepository<IndiceJpa, Long> {

    List<IndiceJpa> findByEnqueteIdOrderById(long enqueteId);
}
