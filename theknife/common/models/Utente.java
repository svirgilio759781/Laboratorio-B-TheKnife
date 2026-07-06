/**
 * Stefano Virgilio 759781 VA
 * Rappresenta un utente registrato nel sistema TheKnife.
 */
package it.uninsubria.theknife.common.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Utente implements Serializable {

    // Identificativo per la serializzazione
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private LocalDate dataNascita;
    private String domicilio;
    private String telefono;
    private Ruolo ruolo;
    private List<Ristorante> preferiti;

    // Costruttore vuoto
    public Utente() {
        this.preferiti = new ArrayList<>();
    }
    // Costruttore minimo
    public Utente(int id,String username, String password, Ruolo ruolo) {
        this(); // Inizializza la lista chiamando il costruttore vuoto
        this.id = id;
        this.username = username;
        this.password = password;
        this.ruolo = ruolo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public java.time.LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(java.time.LocalDate dataNascita) { this.dataNascita = dataNascita; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }

    public List<Ristorante> getPreferiti() { return preferiti; }
    public void setPreferiti(List<Ristorante> preferiti) { this.preferiti = preferiti; }

    public void aggiungiPreferito(Ristorante ristorante) {
        if (!this.preferiti.contains(ristorante)) {
            this.preferiti.add(ristorante);
        }
    }
    public void rimuoviPreferito(Ristorante ristorante) {
        this.preferiti.remove(ristorante);
    }

    public boolean autentica(String password) {
        return this.password.equals(password);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Profilo Utente ---\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Username: ").append(username).append("\n");
        sb.append("Nome Completo: ").append(nome != null ? nome : "").append(" ").append(cognome != null ? cognome : "").append("\n");
        sb.append("Ruolo: ").append(ruolo).append("\n");
        sb.append("Indirizzo: ").append(domicilio != null ? domicilio : "N/D").append("\n");
        sb.append("Ristoranti Preferiti: ").append(preferiti.size()).append("\n");
        sb.append("----------------------");
        return sb.toString();
    }
    public boolean gestisceRistorante(Ristorante r) {
        if (r == null) return false;
        return this.getId() == r.getGestore();
    }
}