/**
 * Stefano Virgilio 759781 VA
 * Gestisce la persistenza della relazione prenotazione tra utenti e ristoranti.
 */
package it.uninsubria.theknife.server.dao;

import it.uninsubria.theknife.common.models.Prenotazione;
import it.uninsubria.theknife.common.models.StatoPrenotazione;
import it.uninsubria.theknife.server.DBManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAO {
    // Aggiorna lo stato di una prenotazione esistente
    public static boolean aggiornaStatoPrenotazione(int idPrenotazione, String nuovoStato) {
        String sql = "UPDATE prenotazione SET stato = ? WHERE id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuovoStato);
            ps.setInt(2, idPrenotazione);

            int righeColpite = ps.executeUpdate();
            return righeColpite > 0;

        } catch (SQLException e) {
            System.err.println("❌ Errore SQL durante l'aggiornamento dello stato della prenotazione:");
            e.printStackTrace();
            return false;
        }
    }
    // Recupera lo storico prenotazioni di uno specifico cliente
    public static List<Prenotazione> getPrenotazioniCliente(int idCliente) {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = "SELECT id, idcliente, idristorante, data, ora, numeropersone, note, stato FROM prenotazione WHERE idcliente = ? ORDER BY data ASC, ora ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prenotazione p = new Prenotazione();
                    p.setId(rs.getInt("id"));
                    p.setIdCliente(rs.getInt("idcliente"));
                    p.setIdRistorante(rs.getInt("idristorante"));
                    p.setData(rs.getDate("data").toLocalDate());
                    p.setOra(rs.getTime("ora").toLocalTime());
                    p.setNumeroPersone(rs.getInt("numeropersone"));
                    p.setNote(rs.getString("note"));

                    String statoDB = rs.getString("stato");

                    if (statoDB != null) {
                        // Rimuove spazi e converte tutto in maiuscolo per confrontarlo in modo sicuro con l'Enum standard
                        String statoNormalizzato = statoDB.trim();

                        switch (statoNormalizzato) {
                            case "Cancellato" -> p.setStato(StatoPrenotazione.Cancellato);
                            case "In_attesa" -> p.setStato(StatoPrenotazione.In_attesa);
                            case "Confermato" -> p.setStato(StatoPrenotazione.Confermato);
                        }
                    }
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    // Recupera tutte le prenotazioni relative a un determinato ristorante
    public static List<Prenotazione> getPrenotazioniRistorante(int idRistorante) {
        List<Prenotazione> lista = new ArrayList<>();
        String sql = "SELECT id, idcliente, idristorante, data, ora, numeropersone, note, stato FROM prenotazione WHERE idristorante = ? ORDER BY data ASC, ora ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prenotazione p = new Prenotazione();
                    p.setId(rs.getInt("id"));
                    p.setIdCliente(rs.getInt("idcliente"));
                    p.setIdRistorante(rs.getInt("idristorante"));
                    p.setData(rs.getDate("data").toLocalDate());
                    p.setOra(rs.getTime("ora").toLocalTime());
                    p.setNumeroPersone(rs.getInt("numeropersone"));
                    p.setNote(rs.getString("note"));

                    String statoDB = rs.getString("stato");

                    if (statoDB != null) {
                        // Rimuove spazi e converte tutto in maiuscolo per confrontarlo in modo sicuro con l'Enum standard
                        String statoNormalizzato = statoDB.trim();

                        switch (statoNormalizzato) {
                            case "Cancellato" -> p.setStato(StatoPrenotazione.Cancellato);
                            case "In_attesa" -> p.setStato(StatoPrenotazione.In_attesa);
                            case "Confermato" -> p.setStato(StatoPrenotazione.Confermato);
                        }
                    }
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    // Inserisce una nuova prenotazione impostando lo stato iniziale su 'In_attesa'
    public static boolean aggiungiPrenotazione(Prenotazione p) {
        String sql = "INSERT INTO prenotazione (idcliente, idristorante, data, ora, numeropersone, note, stato) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdRistorante());

            if (p.getData() != null) {
                ps.setDate(3, java.sql.Date.valueOf(p.getData()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            if (p.getOra() != null) {
                ps.setTime(4, java.sql.Time.valueOf(p.getOra()));
            } else {
                ps.setNull(4, java.sql.Types.TIME);
            }

            ps.setInt(5, p.getNumeroPersone());

            // Gestione sicura delle note opzionali
            if (p.getNote() == null || p.getNote().trim().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, p.getNote().trim());
            }
            ps.setString(7, String.valueOf(StatoPrenotazione.In_attesa));

            int righeColpite = ps.executeUpdate();
            return righeColpite > 0; // Ritorna true se l'operazione nel DB ha avuto successo

        } catch (SQLException e) {
            System.err.println("❌ Errore SQL durante l'inserimento della prenotazione:");
            e.printStackTrace();
            return false;
        }
    }

    // Modifica una prenotazione impostando lo stato su 'In_attesa'
    public static boolean modificaPrenotazione(int id, LocalDate data, LocalTime ora, int persone, String stato) {
        String sql = "UPDATE prenotazione SET data = ?, ora = ?, numeropersone = ?, stato = ? WHERE id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(data));
            ps.setTime(2, java.sql.Time.valueOf(ora));
            ps.setInt(3, persone);
            ps.setString(4, stato);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}