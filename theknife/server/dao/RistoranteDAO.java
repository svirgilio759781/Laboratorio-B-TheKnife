/**
 * Stefano Virgilio 759781 VA
 * Gestisce le operazioni di persistenza per l'entità ristorante nel database.
 */
package it.uninsubria.theknife.server.dao;

import it.uninsubria.theknife.common.models.Ristorante;
import it.uninsubria.theknife.server.DBManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RistoranteDAO {
    // Ricerca ristoranti con filtri dinamici, inclusa media recensioni
    public static List<Ristorante> cercaRistoranti(String nome, String locazione, String cucina, int lunghezzaCosto, boolean prenotazione, boolean consegnaDomicilio, int stelleMinime) {
        List<Ristorante> lista = new ArrayList<>();

        // Base della query dinamica modificata con la LEFT JOIN per calcolare la media
        StringBuilder sql = new StringBuilder("SELECT r.* FROM Ristorante r LEFT JOIN recensione rec ON r.id = rec.idristorante WHERE 1=1");

        // Aggiungiamo i filtri solo se sono stati compilati/scelti (usando l'alias 'r.')
        if (nome != null && !nome.isEmpty()) sql.append(" AND LOWER(r.nome) LIKE ?");
        if (locazione != null && !locazione.isEmpty()) sql.append(" AND LOWER(TRIM(r.locazione)) LIKE ?");
        if (cucina != null && !cucina.isEmpty()) sql.append(" AND LOWER(TRIM(r.tipologiacucina)) LIKE ?");
        if (lunghezzaCosto > 0) sql.append(" AND LENGTH(r.prezzo) = ?");
        if (prenotazione) sql.append(" AND r.prenotazioneonline = TRUE");
        if (consegnaDomicilio) sql.append(" AND r.consegnadomicilio = TRUE");

        // Raggruppamento obbligatorio per aggregare la media delle recensioni
        sql.append(" GROUP BY r.id");

        // Clausola HAVING per filtrare sulla media delle stelle calcolata
        if (stelleMinime > 0) {
            sql.append(" HAVING AVG(rec.stelle) >= ?");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Valorizziamo i punti di domanda nell'ordine esatto in cui sono stati aggiunti
            if (nome != null && !nome.isEmpty()) {
                ps.setString(paramIndex++, "%" + nome.toLowerCase() + "%");
            }
            if (locazione != null && !locazione.isEmpty()) {
                ps.setString(paramIndex++, "%" + locazione.toLowerCase() + "%");
            }
            if (cucina != null && !cucina.isEmpty()) {
                ps.setString(paramIndex++, "%" + cucina.toLowerCase() + "%");
            }
            if (lunghezzaCosto > 0) {
                ps.setInt(paramIndex++, lunghezzaCosto);
            }

            if (stelleMinime > 0) {
                ps.setInt(paramIndex++, stelleMinime);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ristorante r = new Ristorante(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("indirizzo"),
                            rs.getString("locazione"),
                            rs.getString("prezzo"),
                            rs.getString("tipologiacucina"),
                            rs.getDouble("latitudine"),
                            rs.getDouble("longitudine"),
                            rs.getString("telefono")
                    );
                    r.setUrl(rs.getString("url"));
                    r.setWebsiteUrl(rs.getString("websiteurl"));
                    r.setPremi(rs.getString("premi"));
                    r.setGreenStar(rs.getInt("greenstar"));
                    r.setServizi(rs.getString("servizi"));
                    r.setDescrizione(rs.getString("descrizione"));
                    r.setConsegna(rs.getBoolean("consegnadomicilio"));
                    r.setPrenotazione(rs.getBoolean("prenotazioneonline"));
                    r.setidGestore(rs.getInt("idgestore"));

                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    // Ricerca personalizzata per cliente (include filtro preferiti)
    public static List<Ristorante> cercaRistoranti(String nome, String locazione, String cucina, int lunghezzaCosto, boolean prenotazione, boolean consegnaDomicilio, boolean mostraSoloPreferiti, int idClienteCorrente, int stelleMinime) {
        List<Ristorante> lista = new ArrayList<>();

        // Query base modificata inserendo anche la LEFT JOIN per le recensioni
        StringBuilder sql = new StringBuilder("SELECT r.* FROM ristorante r LEFT JOIN recensione rec ON r.id = rec.idristorante");

        if (mostraSoloPreferiti && idClienteCorrente > 0) {
            sql.append(" INNER JOIN preferito p ON r.id = p.idristorante WHERE p.idcliente = ?");
        } else {
            sql.append(" WHERE 1=1");
        }

        if (nome != null && !nome.isEmpty()) sql.append(" AND LOWER(r.nome) LIKE ?");
        if (locazione != null && !locazione.isEmpty()) sql.append(" AND LOWER(r.locazione) LIKE ?");
        if (cucina != null && !cucina.isEmpty()) sql.append(" AND LOWER(r.tipologiacucina) LIKE ?");
        if (lunghezzaCosto > 0) sql.append(" AND LENGTH(r.prezzo) = ?");
        if (prenotazione) sql.append(" AND r.prenotazioneonline = TRUE");
        if (consegnaDomicilio) sql.append(" AND r.consegnadomicilio = TRUE");

        // Raggruppamento necessario per aggregare AVG
        sql.append(" GROUP BY r.id");

        // Filtro finale sulle stelle
        if (stelleMinime > 0) {
            sql.append(" HAVING AVG(rec.stelle) >= ?");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (mostraSoloPreferiti && idClienteCorrente > 0) {
                ps.setInt(paramIndex++, idClienteCorrente);
            }

            if (nome != null && !nome.isEmpty()) {
                ps.setString(paramIndex++, "%" + nome.toLowerCase() + "%");
            }
            if (locazione != null && !locazione.isEmpty()) {
                ps.setString(paramIndex++, "%" + locazione.toLowerCase() + "%");
            }
            if (cucina != null && !cucina.isEmpty()) {
                ps.setString(paramIndex++, "%" + cucina.toLowerCase() + "%");
            }
            if (lunghezzaCosto > 0) {
                ps.setInt(paramIndex++, lunghezzaCosto);
            }

            if (stelleMinime > 0) {
                ps.setInt(paramIndex++, stelleMinime);
            }

            System.out.println("SQL GENERATO CLIENTE: " + sql.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ristorante r = new Ristorante(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("indirizzo"),
                            rs.getString("locazione"),
                            rs.getString("prezzo"),
                            rs.getString("tipologiacucina"),
                            rs.getDouble("latitudine"),
                            rs.getDouble("longitudine"),
                            rs.getString("telefono")
                    );
                    r.setUrl(rs.getString("url"));
                    r.setWebsiteUrl(rs.getString("websiteurl"));
                    r.setPremi(rs.getString("premi"));
                    r.setServizi(rs.getString("servizi"));
                    r.setDescrizione(rs.getString("descrizione"));
                    r.setGreenStar(rs.getInt("greenstar"));
                    r.setConsegna(rs.getBoolean("consegnadomicilio"));
                    r.setPrenotazione(rs.getBoolean("prenotazioneonline"));
                    r.setidGestore(rs.getInt("idgestore"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore durante il filtraggio dei preferiti:");
            e.printStackTrace();
        }
        return lista;
    }
    // Ricerca specifica per i ristoranti gestiti dall'utente corrente
    public static List<Ristorante> cercaPropriRistoranti(String nome, String locazione, String cucina, int lunghezzaCosto, boolean prenotazione, boolean consegnaDomicilio, boolean mostraSoloPropri, int idGestoreCorrente, int stelleMinime) {
        List<Ristorante> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT r.* FROM ristorante r LEFT JOIN recensione rec ON r.id = rec.idristorante");

        if (mostraSoloPropri && idGestoreCorrente > 0) {
            sql.append(" WHERE r.idgestore = ?");
        } else {
            sql.append(" WHERE 1=1");
        }

        if (nome != null && !nome.isEmpty()) sql.append(" AND LOWER(r.nome) LIKE ?");
        if (locazione != null && !locazione.isEmpty()) sql.append(" AND LOWER(r.locazione) LIKE ?");
        if (cucina != null && !cucina.isEmpty()) sql.append(" AND LOWER(r.tipologiacucina) LIKE ?");
        if (lunghezzaCosto > 0) sql.append(" AND LENGTH(r.prezzo) = ?");
        if (prenotazione) sql.append(" AND r.prenotazioneonline = TRUE");
        if (consegnaDomicilio) sql.append(" AND r.consegnadomicilio = TRUE");

        sql.append(" GROUP BY r.id");

        if (stelleMinime > 0) {
            sql.append(" HAVING COALESCE(AVG(rec.stelle), 0) >= ?");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (mostraSoloPropri && idGestoreCorrente > 0) ps.setInt(paramIndex++, idGestoreCorrente);
            if (nome != null && !nome.isEmpty()) ps.setString(paramIndex++, "%" + nome.toLowerCase() + "%");
            if (locazione != null && !locazione.isEmpty()) ps.setString(paramIndex++, "%" + locazione.toLowerCase() + "%");
            if (cucina != null && !cucina.isEmpty()) ps.setString(paramIndex++, "%" + cucina.toLowerCase() + "%");
            if (lunghezzaCosto > 0) ps.setInt(paramIndex++, lunghezzaCosto);
            if (stelleMinime > 0) ps.setInt(paramIndex++, stelleMinime);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ristorante r = new Ristorante(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("indirizzo"),
                            rs.getString("locazione"),
                            rs.getString("prezzo"),
                            rs.getString("tipologiacucina"),
                            rs.getDouble("latitudine"),
                            rs.getDouble("longitudine"),
                            rs.getString("telefono")
                    );
                    r.setUrl(rs.getString("url"));
                    r.setWebsiteUrl(rs.getString("websiteurl"));
                    r.setPremi(rs.getString("premi"));
                    r.setServizi(rs.getString("servizi"));
                    r.setDescrizione(rs.getString("descrizione"));
                    r.setGreenStar(rs.getInt("greenstar"));
                    r.setConsegna(rs.getBoolean("consegnadomicilio"));
                    r.setPrenotazione(rs.getBoolean("prenotazioneonline"));
                    r.setidGestore(rs.getInt("idgestore"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // Elimina un ristorante (il DB gestisce il cascata su recensioni e preferiti)
    public static boolean eliminaRistorante(int idRistorante) {
        String sql = "DELETE FROM ristorante WHERE id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRistorante);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Aggiorna i dati di un ristorante esistente
    public static boolean aggiornaRistorante(Ristorante r) {
        String sql = "UPDATE ristorante SET nome=?, indirizzo=?, locazione=?, prezzo=?, tipologiacucina=?, " +
                "latitudine=?, longitudine=?, telefono=?, url=?, websiteurl=?, premi=?, greenstar=?, " +
                "servizi=?, descrizione=?, consegnadomicilio=?, prenotazioneonline=? WHERE id=?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Campi obbligatori di testo
            ps.setString(1, r.getNome());
            ps.setString(2, r.getIndirizzo());
            ps.setString(3, r.getLocazione());
            ps.setString(4, r.getPrezzo());
            ps.setString(5, r.getTipoCucina());

            // Coordinate (Double - NOT NULL con CHECK)
            ps.setDouble(6, r.getLatitudine());
            ps.setDouble(7, r.getLongitudine());

            // Campi opzionali / testuali aggiuntivi
            ps.setString(8, r.getTelefono());
            ps.setString(9, r.getUrl());
            ps.setString(10, r.getWebsiteUrl());
            ps.setString(11, r.getPremi());

            // greenStar (int: 0 o 1)
            ps.setInt(12, r.getGreenStar());

            ps.setString(13, r.getServizi());
            ps.setString(14, r.getDescrizione());

            // Flags Booleani (consegna e prenotazione)
            ps.setBoolean(15, r.isConsegna());
            ps.setBoolean(16, r.isPrenotazione());

            // Chiave Esterna del Gestore
            ps.setInt(17, r.getId()); // Condizione WHERE per aggiornare solo il ristorante corretto

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Errore in RistoranteDAO durante l'aggiornamento:");
            e.printStackTrace();
            return false;
        }
    }

    // Inserisce un nuovo ristorante associato a un gestore
    public static boolean inserireRistorante(Ristorante r, int idGestore) {
        // Stringa SQL mappata sul DDL della tabella Ristorante (escludiamo 'id' autogenerato)
        String sql = "INSERT INTO ristorante (nome, indirizzo, locazione, prezzo, tipologiacucina, " +
                "latitudine, longitudine, telefono, url, websiteurl, premi, greenstar, " +
                "servizi, descrizione, consegnadomicilio, prenotazioneonline, idgestore) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Campi obbligatori di testo
            ps.setString(1, r.getNome());
            ps.setString(2, r.getIndirizzo());
            ps.setString(3, r.getLocazione());
            ps.setString(4, r.getPrezzo());
            ps.setString(5, r.getTipoCucina());

            // Coordinate (Double - NOT NULL con CHECK)
            ps.setDouble(6, r.getLatitudine());
            ps.setDouble(7, r.getLongitudine());

            // Campi opzionali / testuali aggiuntivi
            ps.setString(8, r.getTelefono());
            ps.setString(9, r.getUrl());
            ps.setString(10, r.getWebsiteUrl());
            ps.setString(11, r.getPremi());

            // greenStar (int: 0 o 1)
            ps.setInt(12, r.getGreenStar());

            ps.setString(13, r.getServizi());
            ps.setString(14, r.getDescrizione());

            // Flags Booleani (consegna e prenotazione)
            ps.setBoolean(15, r.isConsegna());
            ps.setBoolean(16, r.isPrenotazione());

            // Chiave Esterna del Gestore
            ps.setInt(17, idGestore);

            // Esegue la query sul database
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Errore critico in RistoranteDAO durante l'inserimento completo:");
            e.printStackTrace();
            return false;
        }
    }
}