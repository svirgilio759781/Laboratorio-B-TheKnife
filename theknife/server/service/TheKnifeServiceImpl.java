/**
 * Stefano Virgilio 759781 VA
 * Implementazione remota dei metodi di servizio del server (RMI).
 */
package it.uninsubria.theknife.server.service;

import it.uninsubria.theknife.common.interfaces.ITheKnifeService;
import it.uninsubria.theknife.common.models.*;
import it.uninsubria.theknife.server.dao.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TheKnifeServiceImpl extends UnicastRemoteObject implements ITheKnifeService {

    public TheKnifeServiceImpl() throws RemoteException {
        super();
    }

    private final RistoranteDAO ristoranteDAO = new RistoranteDAO();

    // Gestione autenticazione
    @Override
    public Utente login(String username, String password) throws RemoteException {
        System.out.println("Richiesta di login per: " + username);
        return UtenteDAO.verificaLogin(username, password);
    }
    @Override
    public boolean registraUtente(Utente utente) throws RemoteException {
        System.out.println("Richiesta di registrazione per: " + utente.getUsername());
        return UtenteDAO.salvaUtente(utente);
    }

    // Ricerca ristoranti
    @Override
    public List<Ristorante> cercaRistoranti(String nome, String locazione, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, int stelleMinime) throws RemoteException {
        try { return RistoranteDAO.cercaRistoranti(nome, locazione, cucina, lunghezzaCosto, prenotabile, takeaway, stelleMinime); }
        catch (Exception e) { e.printStackTrace(); throw new RemoteException("Errore interno server", e); }
    }
    @Override
    public List<Ristorante> cercaRistoranti(String nome, String locazione, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, boolean mostraSoloPreferiti, int idClienteCorrente, int stelleMinime) throws RemoteException {
        try { return RistoranteDAO.cercaRistoranti(nome, locazione, cucina, lunghezzaCosto, prenotabile, takeaway, mostraSoloPreferiti, idClienteCorrente, stelleMinime); }
        catch (Exception e) { e.printStackTrace(); throw new RemoteException("Errore interno server", e); }
    }
    @Override
    public List<Ristorante> cercaPropriRistoranti(String nome, String locazione, String cucina, int lunghezzaCosto, boolean prenotabile, boolean takeaway, boolean mostraSoloPropri, int idGestoreCorrente, int stelleMinime) throws RemoteException {
        try { return RistoranteDAO.cercaPropriRistoranti(nome, locazione, cucina, lunghezzaCosto, prenotabile, takeaway, mostraSoloPropri, idGestoreCorrente, stelleMinime); }
        catch (Exception e) { e.printStackTrace(); throw new RemoteException("Errore interno server", e); }
    }

    // Preferiti
    @Override
    public boolean isPreferito(int idCliente, int idRistorante) throws RemoteException {
        return PreferitiDAO.isPreferito(idCliente, idRistorante);
    }
    @Override
    public boolean aggiungiAiPreferiti(int idCliente, int idRistorante) throws RemoteException {
        return PreferitiDAO.aggiungi(idCliente, idRistorante);
    }
    @Override
    public boolean rimuoviDaiPreferiti(int idCliente, int idRistorante) throws RemoteException {
        return PreferitiDAO.rimuovi(idCliente, idRistorante);
    }

    // Recensioni
    @Override
    public List<String> getRecensioniRistorante(int idRistorante) throws RemoteException {
        return RecensioneDAO.getRecensioniRistorante(idRistorante);
    }
    @Override
    public boolean aggiungiRecensione(int idCliente, int idRistorante, int stelle, String testo) throws RemoteException {
        return RecensioneDAO.inserisciRecensione(idCliente, idRistorante, stelle, testo);
    }
    @Override
    public boolean rispondiARecensione(int idRecensione, String testoRisposta) throws RemoteException {
        return RispostaRecensioneDAO.inserisciRisposta(idRecensione, testoRisposta);
    }
    @Override
    public boolean aggiornaRecensione(int idRecensione, int stelle, String testo) throws RemoteException {
        return RecensioneDAO.aggiornaRecensione(idRecensione, stelle, testo);
    }
    @Override
    public boolean rimuoviRecensione(int idRecensione) throws RemoteException {
        return RecensioneDAO.rimuoviRecensione(idRecensione);
    }

    // Gestione ristoranti
    @Override
    public boolean rimuoviRistorante(int idRistorante) throws RemoteException {
        return RistoranteDAO.eliminaRistorante(idRistorante);
    }
    @Override
    public boolean aggiornaRistorante(Ristorante ristorante) throws RemoteException {
        return RistoranteDAO.aggiornaRistorante(ristorante);
    }
    @Override
    public boolean inserireRistorante(Ristorante ristorante, int idGestore) throws RemoteException {
        return RistoranteDAO.inserireRistorante(ristorante, idGestore);
    }

    // Prenotazioni
    @Override
    public boolean aggiungiPrenotazione(Prenotazione p) throws RemoteException {
        return PrenotazioneDAO.aggiungiPrenotazione(p);
    }
    @Override
    public List<Prenotazione> getPrenotazioniCliente(int idCliente) throws RemoteException {
        try { return PrenotazioneDAO.getPrenotazioniCliente(idCliente); }
        catch (Exception e) { e.printStackTrace(); throw new RemoteException("Errore interno server", e); }
    }
    @Override
    public List<Prenotazione> getPrenotazioniRistorante(int idRistorante) throws RemoteException {
        try { return PrenotazioneDAO.getPrenotazioniRistorante(idRistorante); }
        catch (Exception e) { e.printStackTrace(); throw new RemoteException("Errore interno server", e); }
    }
    @Override
    public boolean aggiornaStatoPrenotazione(int idPrenotazione, String nuovoStato) throws RemoteException {
        return PrenotazioneDAO.aggiornaStatoPrenotazione(idPrenotazione, nuovoStato);
    }
    @Override
    public boolean modificaPrenotazione(int idPrenotazione, LocalDate data, LocalTime ora, int numeroPersone, String nuovoStato) throws RemoteException {
        return PrenotazioneDAO.modificaPrenotazione(idPrenotazione, data, ora, numeroPersone, nuovoStato);
    }

}