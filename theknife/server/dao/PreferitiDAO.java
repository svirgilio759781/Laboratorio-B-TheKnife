/**
 * Stefano Virgilio 759781 VA
 * Gestisce la persistenza della relazione preferito tra utenti e ristoranti.
 */
package it.uninsubria.theknife.server.dao;

import it.uninsubria.theknife.server.DBManager;
import java.sql.*;

public class PreferitiDAO {

    // Verifica se esiste già un record di preferenza per la coppia utente-ristorante
    public static boolean isPreferito(int idCliente, int idRistorante) {
        String sql = "SELECT 1 FROM preferito WHERE idcliente = ? AND idristorante = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idRistorante);

            try (ResultSet rs = ps.executeQuery()) {
                // Ritorna true se il risultato non è vuoto
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Crea un nuovo legame di preferenza nel database
    public static boolean aggiungi(int idCliente, int idRistorante) {
        String sql = "INSERT INTO preferito (idcliente, idristorante) VALUES (?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idRistorante);

            // Verifica che l'inserimento sia avvenuto correttamente
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Rimuove il legame di preferenza dal database
    public static boolean rimuovi(int idCliente, int idRistorante) {
        String sql = "DELETE FROM preferito WHERE idcliente = ? AND idristorante = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idRistorante);

            // Verifica che la riga sia stata effettivamente eliminata
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}