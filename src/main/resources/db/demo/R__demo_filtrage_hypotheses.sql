-- Jeu de démonstration de F4 (module « joueur »), chargé avec le profil « demo ».
-- Enquête « Fuite du sujet d'examen de POO » : 6 suspects x 6 lieux x 6 objets = 216 scénarios, 5 indices.
-- Migration répétable et idempotente : chaque ligne n'est insérée que si son id est absent.
-- Compatible PostgreSQL et H2 (mode PostgreSQL).

INSERT INTO joueur_enquete (id, titre)
SELECT v.id, v.titre
FROM (VALUES (1, 'Fuite du sujet d''examen de POO')) AS v(id, titre)
WHERE NOT EXISTS (SELECT 1 FROM joueur_enquete e WHERE e.id = v.id);

INSERT INTO joueur_variable (id, enquete_id, code, ordre)
SELECT v.id, v.enquete_id, v.code, v.ordre
FROM (VALUES
    (1, 1, 'SUSPECT', 0),
    (2, 1, 'LIEU',    1),
    (3, 1, 'OBJET',   2)
) AS v(id, enquete_id, code, ordre)
WHERE NOT EXISTS (SELECT 1 FROM joueur_variable x WHERE x.id = v.id);

INSERT INTO joueur_valeur_domaine (id, variable_id, code, ordre)
SELECT v.id, v.variable_id, v.code, v.ordre
FROM (VALUES
    (11, 1, 'Prof. Leblanc',        0),
    (12, 1, 'Dr. Moreau',           1),
    (13, 1, 'M. Garnier',           2),
    (14, 1, 'Mme Duval',            3),
    (15, 1, 'Pr. Lambert',          4),
    (16, 1, 'Dr. Benali',           5),
    (21, 2, 'Amphi A',              0),
    (22, 2, 'Salle serveur',        1),
    (23, 2, 'Secrétariat',          2),
    (24, 2, 'Salle des profs',      3),
    (25, 2, 'Bibliothèque',         4),
    (26, 2, 'Reprographie',         5),
    (31, 3, 'Clé USB',              0),
    (32, 3, 'Imprimante',           1),
    (33, 3, 'Badge d''accès',       2),
    (34, 3, 'Ordinateur portable',  3),
    (35, 3, 'Dossier papier',       4),
    (36, 3, 'Serveur de fichiers',  5)
) AS v(id, variable_id, code, ordre)
WHERE NOT EXISTS (SELECT 1 FROM joueur_valeur_domaine x WHERE x.id = v.id);

INSERT INTO joueur_indice (id, enquete_id, libelle)
SELECT v.id, v.enquete_id, v.libelle
FROM (VALUES
    (1, 1, 'Le suspect n''est pas Prof. Leblanc'),
    (2, 1, 'Le lieu n''est pas l''Amphi A'),
    (3, 1, 'Soit l''incident a eu lieu en Salle serveur, soit l''objet est la Clé USB'),
    (4, 1, 'Si le suspect est Dr. Moreau, alors le lieu n''est pas le Secrétariat'),
    (5, 1, 'Soit le suspect est Mme Duval, soit le lieu est la Salle des profs, soit l''objet est le Badge d''accès')
) AS v(id, enquete_id, libelle)
WHERE NOT EXISTS (SELECT 1 FROM joueur_indice x WHERE x.id = v.id);

-- Littéraux : (id, indice, variable, valeur, positif)
INSERT INTO joueur_litteral (id, indice_id, variable_id, valeur_id, positif)
SELECT v.id, v.indice_id, v.variable_id, v.valeur_id, v.positif
FROM (VALUES
    (1, 1, 1, 11, FALSE),  -- indice 1 : SUSPECT != Prof. Leblanc
    (2, 2, 2, 21, FALSE),  -- indice 2 : LIEU != Amphi A
    (3, 3, 2, 22, TRUE),   -- indice 3 : LIEU = Salle serveur ...
    (4, 3, 3, 31, TRUE),   --            ... OU OBJET = Clé USB
    (5, 4, 1, 12, FALSE),  -- indice 4 : SUSPECT != Dr. Moreau ...
    (6, 4, 2, 23, FALSE),  --            ... OU LIEU != Secrétariat (implication)
    (7, 5, 1, 14, TRUE),   -- indice 5 : SUSPECT = Mme Duval ...
    (8, 5, 2, 24, TRUE),   --            ... OU LIEU = Salle des profs
    (9, 5, 3, 33, TRUE)    --            ... OU OBJET = Badge d'accès
) AS v(id, indice_id, variable_id, valeur_id, positif)
WHERE NOT EXISTS (SELECT 1 FROM joueur_litteral x WHERE x.id = v.id);
