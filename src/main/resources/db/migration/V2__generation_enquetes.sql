-- F1 : Generation automatique des enquetes sous contraintes

CREATE TABLE generation_enquete (
    id UUID PRIMARY KEY,
    seed BIGINT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE generation_suspect (
    enquete_id UUID NOT NULL,
    nom VARCHAR(150) NOT NULL,
    position INTEGER NOT NULL,

    PRIMARY KEY (enquete_id, nom),
    UNIQUE (enquete_id, position),

    FOREIGN KEY (enquete_id)
        REFERENCES generation_enquete(id) ON DELETE CASCADE
);

CREATE TABLE generation_suspect_acces (
    enquete_id UUID NOT NULL,
    suspect_nom VARCHAR(150) NOT NULL,
    moyen_acces VARCHAR(30) NOT NULL,

    PRIMARY KEY (enquete_id, suspect_nom, moyen_acces),

    FOREIGN KEY (enquete_id, suspect_nom)
        REFERENCES generation_suspect(enquete_id, nom)
        ON DELETE CASCADE
);

CREATE TABLE generation_suspect_presence (
    enquete_id UUID NOT NULL,
    suspect_nom VARCHAR(150) NOT NULL,
    horaire VARCHAR(30) NOT NULL,

    PRIMARY KEY (enquete_id, suspect_nom, horaire),

    FOREIGN KEY (enquete_id, suspect_nom)
        REFERENCES generation_suspect(enquete_id, nom)
        ON DELETE CASCADE
);

CREATE TABLE generation_lieu (
    enquete_id UUID NOT NULL,
    nom VARCHAR(150) NOT NULL,
    moyen_acces_requis VARCHAR(30) NOT NULL,
    position INTEGER NOT NULL,

    PRIMARY KEY (enquete_id, nom),
    UNIQUE (enquete_id, position),

    FOREIGN KEY (enquete_id)
        REFERENCES generation_enquete(id) ON DELETE CASCADE
);

CREATE TABLE generation_horaire (
    enquete_id UUID NOT NULL,
    horaire VARCHAR(30) NOT NULL,
    position INTEGER NOT NULL,

    PRIMARY KEY (enquete_id, horaire),
    UNIQUE (enquete_id, position),

    FOREIGN KEY (enquete_id)
        REFERENCES generation_enquete(id) ON DELETE CASCADE
);

CREATE TABLE generation_moyen_acces (
    enquete_id UUID NOT NULL,
    moyen_acces VARCHAR(30) NOT NULL,
    position INTEGER NOT NULL,

    PRIMARY KEY (enquete_id, moyen_acces),
    UNIQUE (enquete_id, position),

    FOREIGN KEY (enquete_id)
        REFERENCES generation_enquete(id) ON DELETE CASCADE
);

CREATE TABLE generation_indice (
    enquete_id UUID NOT NULL,
    position INTEGER NOT NULL,
    variable VARCHAR(30) NOT NULL,
    operateur VARCHAR(30) NOT NULL,
    valeur VARCHAR(150) NOT NULL,
    description VARCHAR(500) NOT NULL,

    PRIMARY KEY (enquete_id, position),

    FOREIGN KEY (enquete_id)
        REFERENCES generation_enquete(id) ON DELETE CASCADE
);

CREATE TABLE generation_solution (
    enquete_id UUID PRIMARY KEY,
    suspect_nom VARCHAR(150) NOT NULL,
    lieu_nom VARCHAR(150) NOT NULL,
    horaire VARCHAR(30) NOT NULL,
    moyen_acces VARCHAR(30) NOT NULL,

    FOREIGN KEY (enquete_id)
        REFERENCES generation_enquete(id) ON DELETE CASCADE,

    FOREIGN KEY (enquete_id, suspect_nom)
        REFERENCES generation_suspect(enquete_id, nom),

    FOREIGN KEY (enquete_id, lieu_nom)
        REFERENCES generation_lieu(enquete_id, nom),

    FOREIGN KEY (enquete_id, horaire)
        REFERENCES generation_horaire(enquete_id, horaire),

    FOREIGN KEY (enquete_id, moyen_acces)
        REFERENCES generation_moyen_acces(enquete_id, moyen_acces)
);