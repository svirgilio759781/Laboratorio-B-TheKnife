/**
 * Stefano Virgilio 759781 VA
 * Gestisce le operazioni di persistenza per l'entità rispostarecensione nel database.
 */
package it.uninsubria.theknife.server.dao;

import it.uninsubria.theknife.server.DBManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RispostaRecensioneDAO {

    // Inserisce o aggiorna la risposta del gestore per una specifica recensione
    public static boolean inserisciRisposta(int idRecensione, String testoRisposta) {
        // Query di inserimento basata su tabella separata
        // Usiamo ON CONFLICT per intercettare la violazione del vincolo unique sulla colonna idrecensione.
        String sql = "INSERT INTO rispostarecensione (idrecensione, testo) VALUES (?, ?) " +
                "ON CONFLICT (idrecensione) " +
                "DO UPDATE SET testo = EXCLUDED.testo";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRecensione);
            ps.setString(2, testoRisposta);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Errore in RispostaRecensioneDAO durante l'inserimento:");
            e.printStackTrace();
            return false;
        }
    }
}