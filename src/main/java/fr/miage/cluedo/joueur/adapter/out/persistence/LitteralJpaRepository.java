package fr.miage.cluedo.joueur.adapter.out.persistence;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface LitteralJpaRepository extends JpaRepository<LitteralJpa, Long> {

    List<LitteralJpa> findByIndiceIdInOrderById(Collection<Long> indiceIds);
}
