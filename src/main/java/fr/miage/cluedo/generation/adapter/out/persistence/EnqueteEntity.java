package fr.miage.cluedo.generation.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "generation_enquete")
public class EnqueteEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private long seed;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    protected EnqueteEntity() {
    }

    public EnqueteEntity(UUID id, long seed) {
        this.id = id;
        this.seed = seed;
        this.dateCreation = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public long getSeed() {
        return seed;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
}