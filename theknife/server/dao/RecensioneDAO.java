/**
 * Stefano Virgilio 759781 VA
 * Gestisce le operazioni di persistenza per l'entità recensione nel database.
 */
package it.uninsubria.theknife.server.dao;

import it.uninsubria.theknife.server.DBManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecensioneDAO {
    // Recupera l'elenco formattato delle recensioni per un ristorante, inclusa eventuale risposta del gestore
    public static List<String> getRecensioniRistorante(int idRistorante) {
        List<String> elenco = new ArrayList<>();

        // Query con LEFT JOIN per prendere i dati da entrambe le tabelle
        String sql = "SELECT r.id, r.stelle, r.testo AS testo_recensione, u.username, risp.testo AS testo_risposta " +
                "FROM recensione r " +
                "INNER JOIN utente u ON r.idcliente = u.id " +
                "LEFT JOIN rispostarecensione risp ON r.id = risp.idrecensione " +
                "WHERE r.idristorante = ? ORDER BY r.id DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idRecensione = rs.getInt("id");
                    int stelle = rs.getInt("stelle");
                    String testo = rs.getString("testo_recensione");
                    String rispostaGestore = rs.getString("testo_risposta");

                    // Formattiamo la stringa base della recensione
                    String rigaRecensione = idRecensione + "|@" + rs.getString("username") + ": ⭐ " + stelle + "/5 - " + testo;
                    // se esiste la risposta, la concateniamo andando a capo con un simbolo grafico (es. ↳)
                    if (rispostaGestore != null && !rispostaGestore.trim().isEmpty()) {
                        rigaRecensione += "\n   ↳ Risposta del gestore: " + rispostaGestore;
                    }

                    elenco.add(rigaRecensione);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recupero delle recensioni con risposte: " + e.getMessage());
            e.printStackTrace();
        }
        return elenco;
    }
    // Inserisce una nuova recensione scritta da un cliente
    public static boolean inserisciRecensione(int idCliente, int idRistorante, int stelle, String testo) {
        String sql = "INSERT INTO recensione (idcliente, idristorante, stelle, testo) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idRistorante);
            ps.setInt(3, stelle);
            ps.setString(4, testo);

            return ps.executeUpdate() > 0; // Ritorna true se la riga viene inserita con successo
        } catch (SQLException e) {
            System.err.println("Errore nel salvataggio della recensione: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // Aggiorna il contenuto e il punteggio di una recensione esistente
    public static boolean aggiornaRecensione(int idRecensione, int stelle, String testo) {
        String sql = "UPDATE recensione SET stelle = ?, testo = ? WHERE id = ?"; // Controlla i nomi delle tue tabelle/colonne

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stelle);
            ps.setString(2, testo);
            ps.setInt(3, idRecensione);

            int righeColpite = ps.executeUpdate();
            return righeColpite > 0; // Ritorna true se la recensione è stata effettivamente aggiornata

        } catch (SQLException e) {
            System.err.println("Errore DAO durante l'aggiornamento della recensione:");
            e.printStackTrace();
            return false;
        }
    }
    // Rimuove una recensione dal database tramite il suo identificativo
    public static boolean rimuoviRecensione(int idRecensione) {
        String sql = "DELETE FROM recensione WHERE id = ?"; // Controlla il nome della tua tabella

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRecensione);

            int righeColpite = ps.executeUpdate();
            return righeColpite > 0; // Ritorna true se la recensione è stata eliminata con successo

        } catch (SQLException e) {
            System.err.println("Errore DAO durante la rimozione della recensione:");
            e.printStackTrace();
            return false;
        }
    }
}
