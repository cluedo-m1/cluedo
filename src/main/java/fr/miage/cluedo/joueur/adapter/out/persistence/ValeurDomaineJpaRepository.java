package fr.miage.cluedo.joueur.adapter.out.persistence;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface ValeurDomaineJpaRepository extends JpaRepository<ValeurDomaineJpa, Long> {

    List<ValeurDomaineJpa> findByVariableIdInOrderByOrdre(Collection<Long> variableIds);
}
