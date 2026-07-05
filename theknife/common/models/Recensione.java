/**
 * Stefano Virgilio 759781 VA
 * Rappresenta una recensione lasciata da un cliente a un ristorante.
 */
package it.uninsubria.theknife.common.models;

import java.io.Serializable;

public class Recensione implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String testo;
    private int stelle;
    private int idCliente;
    private int idRistorante;

    // Costruttore vuoto
    public Recensione() {}

    // Costruttore completo
    public Recensione(int id, String testo, int stelle, int idCliente, int idRistorante) {
        this.id = id;
        this.testo = testo;
        this.stelle = stelle;
        this.idCliente = idCliente;
        this.idRistorante = idRistorante;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public int getStelle() { return stelle; }
    public void setStelle(int stelle) {
        // Validazione minima: le stelle devono essere tra 1 e 5
        if (stelle >= 1 && stelle <= 5) {
            this.stelle = stelle;
        }
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdRistorante() { return idRistorante; }
    public void setIdRistorante(int idRistorante) { this.idRistorante = idRistorante; }

    @Override
    public String toString() {
        return "Recensione{" +
                "stelle=" + stelle +
                ", testo='" + (testo.length() > 20 ? testo.substring(0, 20) + "..." : testo) + '\'' +
                '}';
    }
}