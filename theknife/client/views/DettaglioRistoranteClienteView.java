/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per mostrare il dettaglio di un ristorante e le sue recensioni (Vista Cliente).
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.client.RemoteServiceConnector;
import it.uninsubria.theknife.common.models.Ristorante;
import it.uninsubria.theknife.common.models.Utente;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

public class DettaglioRistoranteClienteView extends Application {

    private final Ristorante ristorante;
    private final Utente utenteLoggato;
    private ListView<String> recensioniList;
    private TextArea nuovaRecensioneArea;
    private ComboBox<Integer> stelleBox;
    private Label mediaStelleLabel;
    private Button inviaButton;
    private Button eliminaRecensioneButton;
    private DatePicker dataPrenotazionePicker;
    private ComboBox<String> oraPrenotazioneBox;
    private Spinner<Integer> personeSpinner;
    private TextField notePrenotazioneField;
    private Label titoloNuova;
    private final Stage stagePrecedente;

    // Variabili per la gestione dei preferiti
    private Button preferitiButton;
    private boolean isPreferito = false;

    private final String[] orariDisponibili = {
            "12:00", "12:30", "13:00", "13:30", "14:00",
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00"
    };

    public DettaglioRistoranteClienteView(Ristorante ristorante, Utente utenteLoggato, Stage stagePrecedente) {
        this.ristorante = ristorante;
        this.utenteLoggato = utenteLoggato;
        this.stagePrecedente = stagePrecedente;
    }
    // Gestisce le eccezioni di rete mostrando un avviso all'utente nell'interfaccia principale
    private void handleConnectionError(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Errore di connessione al server.", ButtonType.OK);
            alert.showAndWait();
        });
        e.printStackTrace();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Dettaglio: " + ristorante.getNome());

        String bgStyle = "-fx-background-color: #f4f7f6; -fx-font-family: 'Segoe UI', sans-serif;";
        String cardStyle = "-fx-background-color: #ffffff; -fx-padding: 25; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5);";
        String scrollBarStyle = ".scroll-bar:vertical { -fx-pref-width: 0; -fx-opacity: 0; }";

        // --- COLONNA SINISTRA ---
        VBox colonnaSinistra = new VBox(15);
        colonnaSinistra.setPrefWidth(450);
        colonnaSinistra.setPadding(new Insets(25));
        colonnaSinistra.setStyle(cardStyle);

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label nomeLabel = new Label(ristorante.getNome());
        nomeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        preferitiButton = new Button();
        preferitiButton.setOnAction(e -> gestisciPreferito());
        aggiornaGraficaPulsantePreferiti(); // Imposta lo stato iniziale

        headerBox.getChildren().addAll(nomeLabel, preferitiButton);

        mediaStelleLabel = new Label("⭐ Media: Calcolo...");
        mediaStelleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #f1c40f;");

        // Info Box
        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 8;");
        infoBox.getChildren().addAll(
                new Label("📍 " + ristorante.getLocazione()),
                new Label("🍴 " + ristorante.getTipoCucina()),
                new Label("💰 " + ristorante.getPrezzo())
        );

        // EXTRA BOX (Premi, Servizi, URL, Sito)
        VBox extraBox = new VBox(6);
        extraBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");

        Label premiLabel = new Label("🏆 Premi: " + (ristorante.getPremi() != null ? ristorante.getPremi() : "Nessuno")
                + (ristorante.getGreenStar() == 1 ? " | 🌱 Green Star" : ""));
        premiLabel.setWrapText(true);

        Label serviziLabel = new Label("✨ Servizi: " + (ristorante.getServizi() != null ? ristorante.getServizi() : "N/D"));
        serviziLabel.setWrapText(true);

        Label urlLabel = new Label("🔗 Guida: " + (ristorante.getUrl() != null ? ristorante.getUrl() : "N/A"));
        urlLabel.setWrapText(true);

        Label siteLabel = new Label("🌐 Sito: " + (ristorante.getWebsiteUrl() != null ? ristorante.getWebsiteUrl() : "N/A"));
        siteLabel.setWrapText(true);

        extraBox.getChildren().addAll(premiLabel, serviziLabel, urlLabel, siteLabel);

        // Descrizione
        Label descTitle = new Label("Descrizione:");
        descTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label descLabel = new Label(ristorante.getDescrizione() != null ? ristorante.getDescrizione() : "Nessuna descrizione.");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(Double.MAX_VALUE);
        descLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px;");

        ScrollPane descScroll = new ScrollPane(descLabel);
        descScroll.setFitToWidth(true);
        descScroll.getStylesheets().add("data:text/css," + scrollBarStyle);
        descScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(descScroll, Priority.ALWAYS);

        Button indietroBtn = new Button("⬅ Indietro");
        indietroBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20;");
        indietroBtn.setOnAction(e -> { primaryStage.close(); if (stagePrecedente != null) stagePrecedente.show(); });

        colonnaSinistra.getChildren().addAll(headerBox, mediaStelleLabel, infoBox, extraBox, descTitle, descScroll, indietroBtn);

        // COLONNA DESTRA: SEZIONE RECENSIONI ESISTENTI
        VBox colonnaDestra = new VBox(15);
        colonnaDestra.setStyle(cardStyle);
        HBox.setHgrow(colonnaDestra, Priority.ALWAYS);

        Label titoloRecensioni = new Label("Recensioni");
        titoloRecensioni.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        recensioniList = new ListView<>();
        recensioniList.getStylesheets().add("data:text/css," + scrollBarStyle);
        recensioniList.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(recensioniList, Priority.ALWAYS);

        recensioniList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Estrazione del testo
                    String testo = item.contains("|") ? item.substring(item.indexOf("|") + 1) : item;
                    Label rec = new Label(testo);
                    rec.setWrapText(true);
                    rec.setMaxWidth(Double.MAX_VALUE);

                    // Qui definisci lo sfondo grigio per il blocco della recensione
                    rec.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0; -fx-background-radius: 8; -fx-text-fill: #2c3e50;");

                    setGraphic(rec);
                    // La cella della ListView resta trasparente, coloriamo solo il contenuto (il Label)
                    setStyle("-fx-background-color: transparent; -fx-padding: 5;");
                }
            }
        });
        // --- FORM AGGIUNTA/MODIFICA RECENSIONE ---
        titoloNuova = new Label("Lascia una recensione:");
        titoloNuova.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        stelleBox = new ComboBox<>();
        stelleBox.getItems().addAll(1, 2, 3, 4, 5);
        stelleBox.getSelectionModel().select(4);

        nuovaRecensioneArea = new TextArea();
        nuovaRecensioneArea.setPromptText("Scrivi qui il tuo commento...");
        nuovaRecensioneArea.setPrefHeight(80);
        nuovaRecensioneArea.setWrapText(true);

        inviaButton = new Button("Invia Recensione");
        inviaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        inviaButton.setOnAction(e -> gestisciInvioOModifica());

        eliminaRecensioneButton = new Button("🗑️ Elimina Recensione");
        eliminaRecensioneButton.setStyle("-fx-background-color: #d35400; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        eliminaRecensioneButton.setDisable(true);
        eliminaRecensioneButton.setOnAction(e -> eliminaRecensione());

        HBox formHeader = new HBox(10, titoloNuova, new Label("Valutazione:"), stelleBox);
        formHeader.setAlignment(Pos.CENTER_LEFT);

        HBox bottoniLayout = new HBox(10, inviaButton, eliminaRecensioneButton);
        bottoniLayout.setAlignment(Pos.CENTER_LEFT);

        VBox boxPrenotazioneForm = new VBox(8);
        boxPrenotazioneForm.setPadding(new Insets(5, 0, 0, 0));

        if (ristorante.isPrenotazione()) {
            Label titoloPrenotazione = new Label("📅 Prenota un tavolo in questo ristorante:");
            titoloPrenotazione.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2980b9;");

            dataPrenotazionePicker = new DatePicker(LocalDate.now().plusDays(1));
            dataPrenotazionePicker.setPrefWidth(130);

            // Disabilita le date passate
            dataPrenotazionePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });

            oraPrenotazioneBox = new ComboBox<>();
            oraPrenotazioneBox.getItems().addAll(
                    "12:00", "12:30", "13:00", "13:30", "14:00",
                    "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00"
            );
            oraPrenotazioneBox.getSelectionModel().select("20:00");
            oraPrenotazioneBox.setPrefWidth(90);

            // Listener: ogni volta che cambia la data, ricalcola gli orari
            dataPrenotazionePicker.valueProperty().addListener((obs, vecchiaData, nuovaData) -> {
                aggiornaOrariDisponibili(nuovaData);
            });


            // Chiamata iniziale per impostare gli orari di oggi
            aggiornaOrariDisponibili(dataPrenotazionePicker.getValue());

            personeSpinner = new Spinner<>(1, 20, 2);
            personeSpinner.setPrefWidth(75);

            HBox rigaDatiPrenotazione = new HBox(10,
                    new Label("Data:"), dataPrenotazionePicker,
                    new Label("Ora:"), oraPrenotazioneBox,
                    new Label("Persone:"), personeSpinner
            );
            rigaDatiPrenotazione.setAlignment(Pos.CENTER_LEFT);

            notePrenotazioneField = new TextField();
            notePrenotazioneField.setPromptText("Note opzionali per il ristoratore (es. seggiolone, allergie...)");
            HBox.setHgrow(notePrenotazioneField, Priority.ALWAYS);

            Button prenotaButton = new Button("Conferma Prenotazione Tavolo");
            prenotaButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            prenotaButton.setOnAction(e -> eseguiPrenotazione());

            HBox rigaNoteEBottone = new HBox(10, notePrenotazioneField, prenotaButton);
            rigaNoteEBottone.setAlignment(Pos.CENTER_LEFT);

            boxPrenotazioneForm.getChildren().addAll(new Separator(), titoloPrenotazione, rigaDatiPrenotazione, rigaNoteEBottone);
        } else {
            Label avvisoNoPrenotazioni = new Label("🔒 Questo ristorante riceve clienti solo direttamente in sala (Prenotazioni non attive).");
            avvisoNoPrenotazioni.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
            boxPrenotazioneForm.getChildren().addAll(new Separator(), avvisoNoPrenotazioni);
        }

        colonnaDestra.getChildren().addAll(titoloRecensioni, recensioniList,
                new Separator(), formHeader, nuovaRecensioneArea, bottoniLayout, boxPrenotazioneForm);

        // Listener per rendere le card cliccabili e attivare la risposta
        recensioniList.getSelectionModel().selectedItemProperty().addListener((obs, vecchiaRec, nuovaRec) -> {
            if (nuovaRec != null) {
                // Questo metodo abilita i campi e carica il testo nel form
                verificaEAttivaModifica(nuovaRec);
            } else {
                // Se viene deselezionata, resettiamo il form
                disattivaInterfacciaModifica();
            }
        });

        HBox rootLayout = new HBox(25, colonnaSinistra, colonnaDestra);
        rootLayout.setPadding(new Insets(30));
        rootLayout.setStyle(bgStyle);

        caricaRecensioni();
        primaryStage.setOnCloseRequest(e -> { if (stagePrecedente != null) stagePrecedente.show(); });
        primaryStage.setScene(new Scene(rootLayout, 1000, 600));
        primaryStage.show();
    }
    // Invia una nuova prenotazione al server tramite task asincrono
    private void eseguiPrenotazione() {
        LocalDate data = dataPrenotazionePicker.getValue();
        String ora = oraPrenotazioneBox.getValue();
        Integer persone = personeSpinner.getValue();
        String note = notePrenotazioneField.getText().trim();

        if (data == null || data.isBefore(LocalDate.now())) {
            new Alert(Alert.AlertType.WARNING, "Seleziona una data valida!").showAndWait();
            return;
        }

        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                it.uninsubria.theknife.common.models.Prenotazione p = new it.uninsubria.theknife.common.models.Prenotazione();
                p.setIdCliente(utenteLoggato.getId());
                p.setIdRistorante(ristorante.getId());
                p.setData(data);
                p.setOra(java.time.LocalTime.parse(ora));
                p.setNumeroPersone(persone);
                p.setNote(note);
                return RemoteServiceConnector.esegui(s -> s.aggiungiPrenotazione(p));
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                new Alert(Alert.AlertType.INFORMATION, "Prenotazione effettuata!").show();
                notePrenotazioneField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Errore prenotazione.").show();
            }
        });

        task.setOnFailed(e -> handleConnectionError((Exception) task.getException()));
        new Thread(task).start();
    }

    // Estrae il punteggio numerico da una stringa recensione formattata
    private int estraiVotoDaTesto(String testo) {
        if (testo == null || !testo.contains("/5")) return -1;

        try {
            int indexFrazione = testo.indexOf("/5");
            String votoStr = testo.substring(indexFrazione - 1, indexFrazione).trim();
            return Integer.parseInt(votoStr);
        } catch (Exception e) {
            System.err.println("Errore estrazione stelle: " + e.getMessage());
        }
        return -1;
    }

    // Analizza la recensione selezionata per attivare la modalità modifica se dell'utente loggato
    private void verificaEAttivaModifica(String rigaRecensione) {
        if (rigaRecensione == null || rigaRecensione.trim().isEmpty()) return;

        try {
            String primaRiga = rigaRecensione.contains("\n") ? rigaRecensione.split("\n")[0] : rigaRecensione;
            int separatoreId = primaRiga.indexOf("|");
            int duePunti = primaRiga.indexOf(":");

            if (separatoreId != -1 && duePunti != -1 && duePunti > separatoreId) {
                String autoreRecensione = primaRiga.substring(separatoreId + 1, duePunti).trim();
                if (autoreRecensione.startsWith("@")) autoreRecensione = autoreRecensione.substring(1).trim();

                if (autoreRecensione.equalsIgnoreCase(utenteLoggato.getUsername().trim())) {
                    titoloNuova.setText("Modifica la tua recensione precedente:");
                    titoloNuova.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #d35400;");
                    inviaButton.setText("✏ Modifica Recensione");
                    inviaButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                    eliminaRecensioneButton.setDisable(false);

                    int indexTrattino = primaRiga.indexOf(" - ");
                    if (indexTrattino != -1) nuovaRecensioneArea.setText(primaRiga.substring(indexTrattino + 3).trim());

                    for (int i = 5; i >= 1; i--) {
                        if (primaRiga.contains(i + "/5") || primaRiga.contains(String.valueOf(i))) {
                            stelleBox.getSelectionModel().select(Integer.valueOf(i));
                            break;
                        }
                    }
                    return;
                }
            }
        } catch (Exception e) { System.err.println("❌ Errore decodifica riga: " + e.getMessage()); }
        disattivaInterfacciaModifica();
    }

    // Estrae l'ID univoco dalla riga di testo della lista recensioni
    private int estraiIdRecensione(String rigaRecensione) {
        try {
            if (rigaRecensione != null && rigaRecensione.contains("|")) {
                return Integer.parseInt(rigaRecensione.substring(0, rigaRecensione.indexOf("|")).trim());
            }
        } catch (Exception e) { System.err.println("❌ Errore estrazione ID: " + e.getMessage()); }
        return -1;
    }

    // Resetta i campi del form per tornare allo stato di inserimento standard
    private void disattivaInterfacciaModifica() {
        nuovaRecensioneArea.clear();
        stelleBox.getSelectionModel().select(4);
        titoloNuova.setText("Lascia una recensione:");
        titoloNuova.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
        inviaButton.setText("Invia Recensione");
        inviaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        eliminaRecensioneButton.setDisable(true);
    }

    // Resetta il form e rimuove la selezione corrente
    private void resettaFormAzione() {
        disattivaInterfacciaModifica();
        recensioniList.getSelectionModel().clearSelection();
    }

    // Esegue l'inserimento o l'aggiornamento di una recensione sul server
    private void gestisciInvioOModifica() {
        String testo = nuovaRecensioneArea.getText().trim();
        Integer stelle = stelleBox.getValue();
        boolean isModifica = "✏ Modifica Recensione".equals(inviaButton.getText());

        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return RemoteServiceConnector.esegui(s -> {
                    if (isModifica) return s.aggiornaRecensione(estraiIdRecensione(recensioniList.getSelectionModel().getSelectedItem()), stelle, testo);
                    else return s.aggiungiRecensione(utenteLoggato.getId(), ristorante.getId(), stelle, testo);
                });
            }
        };

        task.setOnSucceeded(e -> { resettaFormAzione(); caricaRecensioni(); });
        task.setOnFailed(e -> handleConnectionError((Exception) task.getException()));
        new Thread(task).start();
    }

    // Elimina la recensione selezionata previa conferma dell'utente
    private void eliminaRecensione() {
        String recensioneSelezionata = recensioniList.getSelectionModel().getSelectedItem();
        if (recensioneSelezionata == null) return;

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare questa recensione?", ButtonType.YES, ButtonType.NO);
        conferma.showAndWait().ifPresent(risposta -> {
            if (risposta == ButtonType.YES) {
                try {
                    if (RemoteServiceConnector.getService().rimuoviRecensione(estraiIdRecensione(recensioneSelezionata))) {
                        new Alert(Alert.AlertType.INFORMATION, "Recensione eliminata.").showAndWait();
                        resettaFormAzione();
                        caricaRecensioni();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    // Controlla dal server se il ristorante è tra i preferiti dell'utente
    private void controllaStatoPreferito() {
        try {
            isPreferito = RemoteServiceConnector.getService().isPreferito(utenteLoggato.getId(), ristorante.getId());
            aggiornaGraficaPulsantePreferiti();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Aggiorna l'aspetto visivo del pulsante preferiti
    private void aggiornaGraficaPulsantePreferiti() {
        if (isPreferito) {
            preferitiButton.setText("⭐ Rimuovi dai Preferiti");
            preferitiButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        } else {
            preferitiButton.setText("☆ Aggiungi ai Preferiti");
            preferitiButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black; -fx-cursor: hand; -fx-font-weight: bold;");
        }
    }

    // Inverte lo stato "preferito" del ristorante per l'utente loggato
    private void gestisciPreferito() {
        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return RemoteServiceConnector.esegui(s ->
                        isPreferito ? s.rimuoviDaiPreferiti(utenteLoggato.getId(), ristorante.getId())
                                : s.aggiungiAiPreferiti(utenteLoggato.getId(), ristorante.getId())
                );
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                isPreferito = !isPreferito;
                aggiornaGraficaPulsantePreferiti();
            }
        });

        new Thread(task).start();
    }

    // Carica l'elenco delle recensioni dal server e aggiorna la media calcolata
    private void caricaRecensioni() {
        javafx.concurrent.Task<java.util.List<String>> task = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<String> call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.getRecensioniRistorante(ristorante.getId()));
            }
        };

        task.setOnSucceeded(e -> {
            java.util.List<String> elenco = task.getValue();
            recensioniList.getItems().clear();
            if (elenco != null && !elenco.isEmpty()) {
                recensioniList.getItems().addAll(elenco);
                double totale = 0;
                int conteggio = 0;
                for (String riga : elenco) {
                    int voto = estraiVotoDaTesto(riga);
                    if (voto != -1) { totale += voto; conteggio++; }
                }
                mediaStelleLabel.setText(conteggio > 0 ? String.format("⭐ Media: %.1f / 5 (%d recensioni)", totale / conteggio, conteggio) : "⭐ Media: N/A");
            } else {
                mediaStelleLabel.setText("⭐ Media Recensioni: N/A");
            }
        });

        task.setOnFailed(e -> handleConnectionError((Exception) task.getException()));
        new Thread(task).start();
    }
    private void aggiornaOrariDisponibili(LocalDate dataSelezionata) {
        oraPrenotazioneBox.getItems().clear();

        // Se la data selezionata è oggi, filtriamo gli orari passati
        boolean isOggi = dataSelezionata != null && dataSelezionata.isEqual(LocalDate.now());
        java.time.LocalTime oraAdesso = java.time.LocalTime.now();

        for (String orario : orariDisponibili) {
            java.time.LocalTime timeOrario = java.time.LocalTime.parse(orario);

            // Se è oggi, aggiungiamo solo se l'orario non è ancora passato
            if (!isOggi || timeOrario.isAfter(oraAdesso)) {
                oraPrenotazioneBox.getItems().add(orario);
            }
        }

        // Seleziona automaticamente il primo orario disponibile, se esiste
        if (!oraPrenotazioneBox.getItems().isEmpty()) {
            oraPrenotazioneBox.getSelectionModel().select(0);
        } else {
            oraPrenotazioneBox.setPromptText("Nessuno");
        }
    }
}