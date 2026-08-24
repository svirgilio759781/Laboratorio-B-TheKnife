package it.uninsubria.theknife.client;

import it.uninsubria.theknife.common.interfaces.ITheKnifeService;
import java.rmi.Naming;

/**
 * Stefano Virgilio 759781 VA
 * Gestore centralizzato della connessione RMI.
 * Implementa un pattern di connessione "Lazy" e "Resiliente":
 * - Lazy: Il lookup del servizio avviene solo alla prima effettiva richiesta.
 * - Resiliente: Il metodo 'esegui' gestisce automaticamente la perdita di connessione, tentando il ripristino (re-lookup) in caso di RemoteException.
 */
public class RemoteServiceConnector {

    private static ITheKnifeService service;
    private static final String URL = "rmi://localhost:1099/TheKnifeService";

    /** Interfaccia per definire azioni da eseguire sul servizio remoto */
    @FunctionalInterface
    public interface Action<T> {
        T run(ITheKnifeService s) throws Exception;
    }

    /** Ottiene l'istanza del servizio con pattern Singleton lazy */
    public static synchronized ITheKnifeService getService() throws Exception {
        if (service == null) {
            service = (ITheKnifeService) Naming.lookup(URL);
        }
        return service;
    }

    /** Esegue un'azione remota gestendo il ripristino in caso di caduta connessione */
    public static <T> T esegui(Action<T> action) throws Exception {
        try {
            return action.run(getService());
        } catch (Exception e) {
            System.out.println("Server non pronto o connessione caduta, riprovo tra 2 secondi...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            service = null; // Forza il nuovo lookup alla prossima chiamata
            return esegui(action); // Chiamata ricorsiva
        }
    }
}