/**
 * Stefano Virgilio 759781 VA
 * Rappresenta la risposta a una recensione.
 */
package it.uninsubria.theknife.common.models;

import java.io.Serializable;

public class RispostaRecensione implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String testo;
    private int idRecensione;

    // Costruttore vuoto
    public RispostaRecensione() {}

    // Costruttore completo
    public RispostaRecensione(int id, String testo, int idRecensione) {
        this.id = id;
        this.testo = testo;
        this.idRecensione = idRecensione;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public int getidRecensione() { return idRecensione; }
    public void setidRecensione(int idRecensione) { this.idRecensione = idRecensione; }

    @Override
    public String toString() {
        return "Risposta alla Recensione{" +
                ", testo='" + (testo.length() > 20 ? testo.substring(0, 20) + "..." : testo) + '\'' +
                '}';
    }
}