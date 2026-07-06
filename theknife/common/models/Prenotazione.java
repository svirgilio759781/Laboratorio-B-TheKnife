/**
 * Stefano Virgilio 759781 VA
 * Rappresenta una prenotazione effettuata da un utente presso un ristorante.
 */
package it.uninsubria.theknife.common.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Prenotazione implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int idCliente;
    private int idRistorante;
    private LocalDate data;
    private LocalTime ora;
    private int numeroPersone;
    private String note; // Eventuali allergie o richieste speciali
    private StatoPrenotazione stato;

    // Costruttore vuoto
    public Prenotazione() {
        this.stato = StatoPrenotazione.In_attesa;
    }

    // Costruttore minimo
    public Prenotazione(int id, int idCliente, int idRistorante, LocalDate data, LocalTime ora, int numeroPersone) {
        this.id = id;
        this.idCliente = idCliente;
        this.idRistorante = idRistorante;
        this.data = data;
        this.ora = ora;
        this.numeroPersone = numeroPersone;
        this.stato = StatoPrenotazione.In_attesa;
    }
    // Costruttore minimo
    public Prenotazione(int id, int idCliente, int idRistorante, LocalDate data, LocalTime ora, int numeroPersone, String note) {
        this.id = id;
        this.idCliente = idCliente;
        this.idRistorante = idRistorante;
        this.data = data;
        this.ora = ora;
        this.numeroPersone = numeroPersone;
        this.note = note;
        this.stato = StatoPrenotazione.In_attesa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdRistorante() { return idRistorante; }
    public void setIdRistorante(int idRistorante) { this.idRistorante = idRistorante; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getOra() { return ora; }
    public void setOra(LocalTime ora) { this.ora = ora; }

    public int getNumeroPersone() { return numeroPersone; }
    public void setNumeroPersone(int numeroPersone) { this.numeroPersone = numeroPersone; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public StatoPrenotazione getStato() { return stato; }
    public void setStato(StatoPrenotazione stato) { this.stato = stato; }

    public void ConfermaPrenotazione() {
        if(stato == StatoPrenotazione.In_attesa){
            this.stato = StatoPrenotazione.Confermato;
        }
    }
    public void RifiutaPrenotazione() {
        if(stato == StatoPrenotazione.In_attesa){
            this.stato = StatoPrenotazione.Cancellato;
        }
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "id=" + id +
                ", data=" + data +
                ", ora=" + ora +
                ", persone=" + numeroPersone +
                ", note=" + note + (note.length() > 20 ? note.substring(0, 20) + "..." : note) +
                ", stato='" + stato + '\'' +
                '}';
    }
}
