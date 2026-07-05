/**
 * Stefano Virgilio 759781 VA
 * Gestisce il pool di connessioni al database PostgreSQL tramite HikariCP.
 */
package it.uninsubria.theknife.server;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBManager {
    // DataSource per il pool di connessioni
    private static final HikariDataSource dataSource;

    // Blocco statico per inizializzare il pool una sola volta
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/TheKnifeVirgilio");
        config.setUsername("postgres");
        config.setPassword("Gino2015!");

        // Ottimizzazioni per il pool
        config.setMaximumPoolSize(10); // Numero massimo di connessioni simultanee
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");

        dataSource = new HikariDataSource(config);
    }

    // Costruttore privato (Singleton pattern)
    private DBManager() {}

    // Restituisce una connessione dal pool. HikariCP gestisce internamente la thread-safety.
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Chiude il pool alla chiusura dell'applicazione.
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}