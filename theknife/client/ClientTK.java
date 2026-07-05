/**
 * Stefano Virgilio 759781 VA
 * Classe principale del Client.
 * Si limita ad avviare l'applicazione; la connessione è gestita in modo
 * lazy e resiliente dal RemoteServiceConnector.
 */
package it.uninsubria.theknife.client;

import it.uninsubria.theknife.client.views.HomeView;
import javafx.application.Application;

public class ClientTK {

    public static void main(String[] args) {
        System.out.println("--- Avvio Client The Knife ---");

        System.out.println("Avvio interfaccia grafica...");
        Application.launch(HomeView.class, args);
    }
}