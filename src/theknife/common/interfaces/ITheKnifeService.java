package it.uninsubria.theknife.common.interfaces;

import it.uninsubria.theknife.common.models.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Stefano Virgilio 759781 VA
 * Interfaccia remota che definisce i metodi esportati dal Server.
 * Questa interfaccia deve essere nota sia al Client che al Server.
 */
public interface ITheKnifeService extends Remote {

    /* --- Gestione autenticazione utenti --- */
    /** Autentica un utente nel sistema */
    Utente login(String username, String password) throws RemoteException;

    /** Registra un nuovo utente nel sistema */
    boolean registraUtente(Utente utente) throws RemoteException;

    /* --- Metodi per la ricerca e filtraggio dei ristoranti --- */
    /** Cerca ristoranti in base a filtri testuali e opzionali */
    List<Ristorante> cercaRistoranti(String nome, String citta, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, int stelleMinime) throws RemoteException;

    /** Cerca ristoranti per il cliente gestendo anche l'opzione dei preferiti */
    List<Ristorante> cercaRistoranti(String nome, String citta, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, boolean mostraSoloPreferiti, int idClienteCorrente, int stelleMinime) throws RemoteException;

    /** Cerca e filtra i ristoranti gestiti dal gestore corrente */
    List<Ristorante> cercaPropriRistoranti(String nome, String citta, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, boolean mostraSoloProprio, int idGestoreCorrente, int stelleMinime) throws RemoteException;

    /* --- Gestione dei ristoranti preferiti --- */
    /** Verifica se un ristorante è tra i preferiti dell'utente */
    boolean isPreferito(int idUtente, int idRistorante) throws java.rmi.RemoteException;

    /** Aggiunge un ristorante ai preferiti dell'utente */
    boolean aggiungiAiPreferiti(int idUtente, int idRistorante) throws java.rmi.RemoteException;

    /** Rimuove un ristorante dai preferiti dell'utente */
    boolean rimuoviDaiPreferiti(int idUtente, int idRistorante) throws java.rmi.RemoteException;

    /* --- Operazioni su recensioni --- */
    /** Restituisce l'elenco delle recensioni per un ristorante */
    java.util.List<String> getRecensioniRistorante(int idRistorante) throws java.rmi.RemoteException;

    /* --- Aggiunge una nuova recensione a un ristorante --- */
    boolean aggiungiRecensione(int idCliente, int idRistorante, int stelle, String testo) throws java.rmi.RemoteException;

    /** Aggiunge una risposta del gestore a una recensione */
    boolean rispondiARecensione(int idRecensione, String testoRisposta) throws java.rmi.RemoteException;

    /** Aggiorna una recensione esistente */
    boolean aggiornaRecensione(int idRecensione, int stelle, String testo) throws RemoteException;

    /** Rimuove una recensione dal sistema */
    boolean rimuoviRecensione(int idRecensione) throws RemoteException;

    /* --- Gestione anagrafica ristoranti --- */
    /** Rimuove un ristorante dal sistema */
    boolean rimuoviRistorante(int idRistorante) throws java.rmi.RemoteException;

    /** Aggiorna le informazioni di un ristorante esistente */
    boolean aggiornaRistorante(Ristorante ristorante) throws java.rmi.RemoteException;

    /** Inserisce un nuovo ristorante associato a un gestore */
    boolean inserireRistorante(Ristorante ristorante, int idGestore) throws java.rmi.RemoteException;

    /* --- Gestione sistema di prenotazione --- */
    /** Aggiunge una nuova prenotazione nel sistema */
    boolean aggiungiPrenotazione(Prenotazione p) throws RemoteException;

    /** Restituisce l'elenco delle prenotazioni effettuate da un cliente */
    List<Prenotazione> getPrenotazioniCliente(int idCliente) throws RemoteException;

    /** Restituisce l'elenco delle prenotazioni ricevute da un ristorante */
    List<Prenotazione> getPrenotazioniRistorante(int idRistorante) throws RemoteException;

    /** Aggiorna lo stato di una prenotazione specifica */
    boolean aggiornaStatoPrenotazione(int idPrenotazione, String nuovoStato) throws RemoteException;

    /** Modifica data, ora, numero di persone e stato di una prenotazione */
    boolean modificaPrenotazione(int idPrenotazione, LocalDate data, LocalTime ora, int numeroPersone, String nuovoStato) throws RemoteException;
}