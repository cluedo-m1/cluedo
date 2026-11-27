package fr.miage.cluedo.generation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EnqueteJpaRepository
        extends JpaRepository<EnqueteEntity, UUID> {
}