/**
 * Stefano Virgilio 759781 VA
 * Interfaccia remota che definisce i metodi esportati dal Server.
 * Questa interfaccia deve essere nota sia al Client che al Server.
 */
package it.uninsubria.theknife.common.interfaces;

import it.uninsubria.theknife.common.models.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ITheKnifeService extends Remote {

    // Gestione autenticazione utenti
    Utente login(String username, String password) throws RemoteException;
    boolean registraUtente(Utente utente) throws RemoteException;

    // Metodi per la ricerca e filtraggio dei ristoranti
    List<Ristorante> cercaRistoranti(String nome, String citta, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, int stelleMinime) throws RemoteException;
    List<Ristorante> cercaRistoranti(String nome, String citta, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, boolean mostraSoloPreferiti, int idClienteCorrente, int stelleMinime) throws RemoteException;
    List<Ristorante> cercaPropriRistoranti(String nome, String citta, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, boolean mostraSoloProprio, int idGestoreCorrente, int stelleMinime) throws RemoteException;

    // Gestione dei ristoranti preferiti
    boolean isPreferito(int idUtente, int idRistorante) throws java.rmi.RemoteException;
    boolean aggiungiAiPreferiti(int idUtente, int idRistorante) throws java.rmi.RemoteException;
    boolean rimuoviDaiPreferiti(int idUtente, int idRistorante) throws java.rmi.RemoteException;

    // Operazioni su recensioni
    java.util.List<String> getRecensioniRistorante(int idRistorante) throws java.rmi.RemoteException;
    boolean aggiungiRecensione(int idCliente, int idRistorante, int stelle, String testo) throws java.rmi.RemoteException;
    boolean rispondiARecensione(int idRecensione, String testoRisposta) throws java.rmi.RemoteException;
    boolean aggiornaRecensione(int idRecensione, int stelle, String testo) throws RemoteException;
    boolean rimuoviRecensione(int idRecensione) throws RemoteException;

    // Gestione anagrafica ristoranti
    boolean rimuoviRistorante(int idRistorante) throws java.rmi.RemoteException;
    boolean aggiornaRistorante(Ristorante ristorante) throws java.rmi.RemoteException;
    boolean inserireRistorante(Ristorante ristorante, int idGestore) throws java.rmi.RemoteException;

    // Gestione sistema di prenotazione
    boolean aggiungiPrenotazione(Prenotazione p) throws RemoteException;
    List<Prenotazione> getPrenotazioniCliente(int idCliente) throws RemoteException;
    List<Prenotazione> getPrenotazioniRistorante(int idRistorante) throws RemoteException;
    boolean aggiornaStatoPrenotazione(int idPrenotazione, String nuovoStato) throws RemoteException;
    boolean modificaPrenotazione(int idPrenotazione, LocalDate data, LocalTime ora, int numeroPersone, String nuovoStato) throws RemoteException;
}