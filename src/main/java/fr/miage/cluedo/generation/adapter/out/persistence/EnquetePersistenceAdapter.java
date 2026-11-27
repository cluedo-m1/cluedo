package fr.miage.cluedo.generation.adapter.out.persistence;

import fr.miage.cluedo.generation.application.port.out.EnqueteRepository;
import fr.miage.cluedo.generation.domain.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class EnquetePersistenceAdapter implements EnqueteRepository {

    private final EnqueteJpaRepository jpa;
    private final JdbcTemplate jdbc;

    public EnquetePersistenceAdapter(
            EnqueteJpaRepository jpa,
            JdbcTemplate jdbc) {
        this.jpa = jpa;
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void sauvegarder(Enquete enquete) {

        UUID id = enquete.id();

        jpa.saveAndFlush(new EnqueteEntity(id, enquete.seed()));

        for (int i = 0; i < enquete.suspects().size(); i++) {
            Suspect suspect = enquete.suspects().get(i);

            jdbc.update("""
                INSERT INTO generation_suspect
                (enquete_id, nom, position)
                VALUES (?, ?, ?)
                """, id, suspect.nom(), i);

            for (MoyenAcces acces : suspect.moyensAcces()) {
                jdbc.update("""
                    INSERT INTO generation_suspect_acces
                    (enquete_id, suspect_nom, moyen_acces)
                    VALUES (?, ?, ?)
                    """, id, suspect.nom(), acces.name());
            }

            for (Horaire horaire : suspect.horairesPresence()) {
                jdbc.update("""
                    INSERT INTO generation_suspect_presence
                    (enquete_id, suspect_nom, horaire)
                    VALUES (?, ?, ?)
                    """, id, suspect.nom(), horaire.name());
            }
        }

        for (int i = 0; i < enquete.lieux().size(); i++) {
            Lieu lieu = enquete.lieux().get(i);

            jdbc.update("""
                INSERT INTO generation_lieu
                (enquete_id, nom, moyen_acces_requis, position)
                VALUES (?, ?, ?, ?)
                """, id, lieu.nom(),
                    lieu.moyenAccesRequis().name(), i);
        }

        for (int i = 0; i < enquete.horaires().size(); i++) {
            jdbc.update("""
                INSERT INTO generation_horaire
                (enquete_id, horaire, position)
                VALUES (?, ?, ?)
                """, id, enquete.horaires().get(i).name(), i);
        }

        for (int i = 0; i < enquete.moyensAcces().size(); i++) {
            jdbc.update("""
                INSERT INTO generation_moyen_acces
                (enquete_id, moyen_acces, position)
                VALUES (?, ?, ?)
                """, id, enquete.moyensAcces().get(i).name(), i);
        }

        for (int i = 0; i < enquete.indices().size(); i++) {
            Indice indice = enquete.indices().get(i);

            jdbc.update("""
                INSERT INTO generation_indice
                (enquete_id, position, variable, operateur,
                 valeur, description)
                VALUES (?, ?, ?, ?, ?, ?)
                """, id, i, indice.variable().name(),
                    indice.operateur().name(),
                    indice.valeur(), indice.description());
        }

        Solution solution = enquete.solution();

        jdbc.update("""
            INSERT INTO generation_solution
            (enquete_id, suspect_nom, lieu_nom, horaire, moyen_acces)
            VALUES (?, ?, ?, ?, ?)
            """, id, solution.coupable().nom(),
                solution.lieu().nom(),
                solution.horaire().name(),
                solution.moyenAcces().name());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enquete> trouverParId(UUID id) {

        return jpa.findById(id).map(entity -> {

            List<Suspect> suspects = jdbc.query("""
                SELECT nom FROM generation_suspect
                WHERE enquete_id = ? ORDER BY position
                """, (rs, n) -> {
                    String nom = rs.getString("nom");

                    Set<MoyenAcces> acces = new HashSet<>(
                        jdbc.query("""
                            SELECT moyen_acces
                            FROM generation_suspect_acces
                            WHERE enquete_id = ? AND suspect_nom = ?
                            """, (r, i) -> MoyenAcces.valueOf(
                                r.getString("moyen_acces")), id, nom));

                    Set<Horaire> presences = new HashSet<>(
                        jdbc.query("""
                            SELECT horaire
                            FROM generation_suspect_presence
                            WHERE enquete_id = ? AND suspect_nom = ?
                            """, (r, i) -> Horaire.valueOf(
                                r.getString("horaire")), id, nom));

                    return new Suspect(nom, acces, presences);
                }, id);

            List<Lieu> lieux = jdbc.query("""
                SELECT nom, moyen_acces_requis
                FROM generation_lieu
                WHERE enquete_id = ? ORDER BY position
                """, (rs, n) -> new Lieu(
                    rs.getString("nom"),
                    MoyenAcces.valueOf(
                        rs.getString("moyen_acces_requis"))), id);

            List<Horaire> horaires = jdbc.query("""
                SELECT horaire FROM generation_horaire
                WHERE enquete_id = ? ORDER BY position
                """, (rs, n) -> Horaire.valueOf(
                    rs.getString("horaire")), id);

            List<MoyenAcces> moyens = jdbc.query("""
                SELECT moyen_acces FROM generation_moyen_acces
                WHERE enquete_id = ? ORDER BY position
                """, (rs, n) -> MoyenAcces.valueOf(
                    rs.getString("moyen_acces")), id);

            List<Indice> indices = jdbc.query("""
                SELECT variable, operateur, valeur, description
                FROM generation_indice
                WHERE enquete_id = ? ORDER BY position
                """, (rs, n) -> new Indice(
                    Indice.Variable.valueOf(rs.getString("variable")),
                    Indice.Operateur.valueOf(rs.getString("operateur")),
                    rs.getString("valeur"),
                    rs.getString("description")), id);

            Solution solution = jdbc.queryForObject("""
                SELECT suspect_nom, lieu_nom, horaire, moyen_acces
                FROM generation_solution
                WHERE enquete_id = ?
                """, (rs, n) -> {

                    String suspectNom = rs.getString("suspect_nom");
                    String lieuNom = rs.getString("lieu_nom");

                    Suspect coupable = suspects.stream()
                        .filter(s -> s.nom().equals(suspectNom))
                        .findFirst().orElseThrow();

                    Lieu lieu = lieux.stream()
                        .filter(l -> l.nom().equals(lieuNom))
                        .findFirst().orElseThrow();

                    return new Solution(
                        coupable,
                        lieu,
                        Horaire.valueOf(rs.getString("horaire")),
                        MoyenAcces.valueOf(rs.getString("moyen_acces")));
                }, id);

            return new Enquete(
                id, suspects, lieux, horaires, moyens,
                indices, solution, entity.getSeed());
        });
    }
}