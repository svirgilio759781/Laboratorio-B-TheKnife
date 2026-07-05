/**
 * Stefano Virgilio 759781 VA
 * Punto di ingresso dell'applicazione server; inizializza DB e registro RMI.
 */
package it.uninsubria.theknife.server;

import it.uninsubria.theknife.common.interfaces.ITheKnifeService;
import it.uninsubria.theknife.server.service.TheKnifeServiceImpl;

import it.uninsubria.theknife.server.dao.*;
import it.uninsubria.theknife.common.models.Ristorante;

import java.util.List;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerTK {
    public static void main(String[] args) {
        try {
            System.out.println("--- Avvio Server The Knife ---");

            // Verifica connessione al database
            System.out.println("Verifica connessione al database...");
            java.sql.Connection testConn = DBManager.getConnection();

            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✅ Connessione al Database riuscita!");
            }
            // Istanziazione del servizio (motore logico)
            ITheKnifeService service = new TheKnifeServiceImpl();
            // Avvio del Registro RMI sulla porta 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            // Registrazione del servizio
            registry.rebind("TheKnifeService", service);

            System.out.println("🚀 Server The Knife pronto e registrato su RMI!");
            System.out.println("In attesa di connessioni dai client...");

        } catch (java.sql.SQLException e) {
            System.err.println("❌ ERRORE DB: Impossibile connettersi a PostgreSQL.");
            System.err.println("Controlla: URL, Username, Password e che il servizio sia attivo.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ ERRORE RMI: Impossibile avviare il servizio.");
            e.printStackTrace();
        }
    }
}