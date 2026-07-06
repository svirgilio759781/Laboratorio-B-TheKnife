/**
 * Stefano Virgilio 759781 VA
 * Rappresenta un ristorante registrato nel sistema TheKnife.
 */
package it.uninsubria.theknife.common.models;

import java.io.Serializable;
import java.util.List;

public class Ristorante implements Serializable {

    // Identificativo per la serializzazione
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String indirizzo;
    private String locazione;
    private String prezzo;
    private String tipoCucina;
    private Double latitudine;
    private Double longitudine;
    private String telefono;
    private String url;
    private String websiteUrl;
    private String premi;
    private int greenStar;
    private String servizi;
    private String descrizione;
    private boolean consegna;
    private boolean prenotazione;
    private int idGestore;

    // Costruttore vuoto
    public Ristorante() {}

    // Costruttore minimo
    public Ristorante(int id, String nome, String indirizzo, String locazione, String prezzo, String tipoCucina, Double latitudine, Double longitudine, String telefono) {
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.locazione = locazione;
        this.prezzo = prezzo;
        this.indirizzo = indirizzo;
        this.tipoCucina = tipoCucina;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.telefono = telefono;
        this.greenStar = 0;
        this.consegna = false;
        this.prenotazione = false;
        this.idGestore = 1;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getLocazione() { return locazione; }
    public void setLocazione(String citta, String nazione) { this.locazione = citta+", "+nazione; }
    public void setLocazione(String locazione) { this.locazione = locazione; }

    public String getPrezzo() { return prezzo; }
    public void setPrezzo(String prezzo) { this.prezzo = prezzo; }

    public String getTipoCucina() { return tipoCucina; }
    public void setTipoCucina(String tipoCucina) { this.tipoCucina = tipoCucina; }

    public Double getLatitudine() { return latitudine; }
    public void setLatitudine(Double latitudine) {
        // Validazione minima: le stelle devono essere tra -90 e 90
        if (latitudine >= 90 && latitudine <= 90) {
            this.latitudine = latitudine;
        }
    }

    public Double getLongitudine() { return longitudine; }
    public void setLongitudine(Double longitudine) {
        // Validazione minima: le stelle devono essere tra -180 e 180
        if (longitudine >= -180 && longitudine <= 180) {
            this.longitudine = longitudine;
        }
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getPremi() { return premi; }
    public void setPremi(String premi) { this.premi = premi; }

    public int getGreenStar() { return greenStar; }
    public void setGreenStar(int greenStar) {
        // Validazione minima: le stelle devono essere 0 o 1
        if (greenStar == 0 || greenStar == 1) {
            this.greenStar = greenStar;
        }
    }

    public String getServizi() { return servizi; }
    public void setServizi(String servizi) { this.servizi = servizi; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public boolean isConsegna() { return consegna; }
    public void setConsegna(boolean consegna) { this.consegna = consegna; }

    public boolean isPrenotazione() { return prenotazione; }
    public void setPrenotazione(boolean prenotazione) { this.prenotazione = prenotazione; }

    public int getGestore() { return idGestore; }
    public void setidGestore(int idGestore) { this.idGestore = idGestore; }

    public double calcolaMediaStelle(List<Recensione> recensioniAssociate) {
        if (recensioniAssociate == null || recensioniAssociate.isEmpty()) {
            return 0.0;
        }
        double somma = 0;
        for (Recensione r : recensioniAssociate) {
            somma += r.getStelle();
        }
        return somma / recensioniAssociate.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("   DETTAGLIO RISTORANTE (ID: ").append(id).append(")\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Nome:             ").append(nome != null ? nome : "N/D").append("\n");
        sb.append("Indirizzo:        ").append(indirizzo != null ? indirizzo : "N/D").append("\n");
        sb.append("Città/Locazione:  ").append(locazione != null ? locazione : "N/D").append("\n");
        sb.append("Tipo Cucina:      ").append(tipoCucina != null ? tipoCucina : "N/D").append("\n");
        sb.append("Fascia Prezzo:    ").append(prezzo != null ? prezzo : "N/D").append("\n");
        sb.append("Telefono:         ").append(telefono != null ? telefono : "N/D").append("\n");
        sb.append("Sito Web:         ").append(websiteUrl != null ? websiteUrl : "N/D").append("\n");
        sb.append("URL Guida:        ").append(url != null ? url : "N/D").append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Descrizione:      ").append(descrizione != null ? descrizione : "Nessuna descrizione").append("\n");
        sb.append("Premi:            ").append(premi != null ? premi : "Nessun premio").append("\n");
        sb.append("Servizi:          ").append(servizi != null ? servizi : "Nessun servizio specificato").append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Coordinate:       [").append(latitudine).append(" , ").append(longitudine).append("]\n");
        sb.append("Green Star:       ").append(greenStar == 1 ? "✅ SÌ (Sostenibile)" : "❌ NO").append("\n");
        sb.append("Consegna:         ").append(consegna ? "✅ Disponibile" : "❌ No").append("\n");
        sb.append("Prenotazione:     ").append(prenotazione ? "✅ Disponibile" : "❌ No").append("\n");
        sb.append("Id Gestore:       ").append(idGestore).append("\n");
        sb.append("--------------------------------------------------");

        return sb.toString();
    }
}