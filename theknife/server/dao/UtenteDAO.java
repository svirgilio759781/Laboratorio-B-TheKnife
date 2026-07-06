/**
 * Stefano Virgilio 759781 VA
 * Gestisce le operazioni di persistenza per l'entità utente nel database.
 */
package it.uninsubria.theknife.server.dao;

import it.uninsubria.theknife.common.models.Utente;
import it.uninsubria.theknife.common.models.Ruolo;
import it.uninsubria.theknife.server.DBManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {

    // Verifica le credenziali nel DB e restituisce l'oggetto Utente se autenticato
    public static Utente verificaLogin(String username, String password) {
        String sql = "SELECT * FROM utente WHERE username = ? AND password = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utente u = new Utente();
                    u.setId(rs.getInt("id"));
                    u.setNome(rs.getString("nome"));
                    u.setCognome(rs.getString("cognome"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));

                    // Mappa la stringa del DB al valore Enum corrispondente
                    String ruoloDb = rs.getString("ruolo");
                    u.setRuolo(Ruolo.valueOf(ruoloDb));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore login: " + e.getMessage());
        }
        return null;
    }

    // Salva un nuovo utente nel database con i relativi dati anagrafici
    public static boolean salvaUtente(Utente u) {
        String sql = "INSERT INTO Utente (nome, cognome, username, password, ruolo, dataNascita, domicilio, telefono) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            ps.setString(3, u.getUsername());
            ps.setString(4, u.getPassword());

            // Converte l'enum in stringa (es. Cliente) per uniformità con il DB
            String ruoloString = u.getRuolo().toString().toLowerCase();
            ruoloString = ruoloString.substring(0, 1).toUpperCase() + ruoloString.substring(1);
            ps.setString(5, ruoloString);

            ps.setDate(6, java.sql.Date.valueOf(u.getDataNascita()));
            ps.setString(7, u.getDomicilio());
            ps.setString(8, u.getTelefono());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore inserimento utente: " + e.getMessage());
            return false;
        }
    }
}