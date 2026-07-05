/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per mostrare il dettaglio di un ristorante, le sue recensioni e gestirlo (Vista Gestore).
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DettaglioRistoranteGestoreView extends Application {

    private final Ristorante ristorante;
    private final Utente utenteLoggato;
    private ListView<String> recensioniList;
    private Label mediaStelleLabel;
    private TextArea rispostaArea;
    private Button inviaRispostaButton;
    private Button eliminaRispostaButton;
    private Label titoloRispostaLabel;
    private final Runnable onRistoranteEliminato;
    private final Stage stagePrecedente;

    public DettaglioRistoranteGestoreView(Ristorante ristorante, Utente utenteLoggato, Runnable onRistoranteEliminato, Stage stagePrecedente) {
        this.ristorante = ristorante;
        this.utenteLoggato = utenteLoggato;
        this.onRistoranteEliminato = onRistoranteEliminato;
        this.stagePrecedente = stagePrecedente;
    }
    // Gestisce le eccezioni di rete mostrando un avviso all'utente nell'interfaccia principale
    private void handleConnectionError(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Errore di connessione al server. Si prega di riprovare.", ButtonType.OK);
            alert.showAndWait();
        });
        e.printStackTrace();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife [GESTIONE] - Dettaglio: " + ristorante.getNome());

        String cardStyle = "-fx-background-color: #ffffff; -fx-padding: 25; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5);";
        String scrollBarStyle = ".scroll-bar:vertical { -fx-pref-width: 0; -fx-opacity: 0; }";

        // --- COLONNA SINISTRA ---
        VBox colonnaSinistra = new VBox(15);
        colonnaSinistra.setPrefWidth(450);
        colonnaSinistra.setPadding(new Insets(25));
        colonnaSinistra.setStyle(cardStyle);

        Label nomeLabel = new Label(ristorante.getNome());
        nomeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // --- BOTTONE INSERIMENTO ---
        HBox modRistoranteLayout = getHBox(primaryStage);

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

        // --- BOTTONE PRENOTAZIONI ---
        Button prenotazioneButton = new Button("📅 Le Mie Prenotazioni");
        prenotazioneButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 4px; -fx-cursor: hand;");
        prenotazioneButton.setOnAction(e -> {
            Stage prenotazioneStage = new Stage();

            VisualizzaPrenotazioniRistoranteView visualizzaPrenotazioneView = new VisualizzaPrenotazioniRistoranteView(ristorante, primaryStage);
            try {
                primaryStage.hide();
                visualizzaPrenotazioneView.start(prenotazioneStage);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button indietroBtn = new Button("⬅ Indietro");
        indietroBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20;");
        indietroBtn.setOnAction(e -> { primaryStage.close(); if (stagePrecedente != null) stagePrecedente.show(); });

        HBox bottoniLayout = new HBox(10, prenotazioneButton, indietroBtn);
        bottoniLayout.setAlignment(Pos.CENTER_LEFT);

        colonnaSinistra.getChildren().addAll(nomeLabel, modRistoranteLayout, mediaStelleLabel, infoBox, extraBox, descTitle, descScroll, bottoniLayout);

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
        // --- FORM RISPOSTA GESTORE ---
        titoloRispostaLabel = new Label("Seleziona una recensione per poter rispondere");
        titoloRispostaLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        titoloRispostaLabel.setWrapText(true);

        rispostaArea = new TextArea();
        rispostaArea.setPromptText("Scrivi qui la tua risposta ufficiale alla recensione selezionata...");
        rispostaArea.setPrefHeight(80);
        rispostaArea.setWrapText(true);
        rispostaArea.setDisable(true);

        inviaRispostaButton = new Button("Invia Risposta Ufficiale");
        inviaRispostaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        inviaRispostaButton.setDisable(true);
        inviaRispostaButton.setOnAction(e -> inviaRisposta());

        eliminaRispostaButton = new Button("🗑️ Elimina Risposta");
        eliminaRispostaButton.setStyle("-fx-background-color: #d35400; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        eliminaRispostaButton.setDisable(true);
        eliminaRispostaButton.setOnAction(e -> eliminaRisposta());

        HBox bottoniRispostaLayout = new HBox(10, inviaRispostaButton, eliminaRispostaButton);
        bottoniRispostaLayout.setAlignment(Pos.CENTER_LEFT);

        colonnaDestra.getChildren().addAll(titoloRecensioni, recensioniList,
                new Separator(), titoloRispostaLabel, rispostaArea, bottoniRispostaLayout);

        // CONTENITORE GENERALE (HBOX)
        HBox rootLayout = new HBox(15);
        rootLayout.setPadding(new Insets(15));
        rootLayout.getChildren().addAll(colonnaSinistra, new Separator(javafx.geometry.Orientation.VERTICAL), colonnaDestra);

        caricaRecensioni();
        primaryStage.setOnCloseRequest(e -> { if (stagePrecedente != null) stagePrecedente.show(); });
        primaryStage.setScene(new Scene(new StackPane(), 1000, 600)); // Esempio layout
        primaryStage.show();

        // Listener per rendere le card cliccabili e attivare la risposta
        recensioniList.getSelectionModel().selectedItemProperty().addListener((obs, vecchiaRec, nuovaRec) -> {
            if (nuovaRec != null) {
                // Questo metodo abilita i campi e carica il testo nel form
                attivaFormRisposta(nuovaRec);
            } else {
                // Se viene deselezionata, resettiamo il form
                resettaFormAzione();
            }
        });

        Scene scene = new Scene(rootLayout, 900, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    // Crea il layout con i pulsanti per le operazioni amministrative sul ristorante
    private HBox getHBox(Stage primaryStage) {
        Button modificaButton = new Button("✏ Modifica Ristorante");
        modificaButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        modificaButton.setOnAction(e -> gestisciModifica(primaryStage));

        Button eliminaButton = new Button("🗑️ Elimina Ristorante");
        eliminaButton.setStyle("-fx-background-color: #d35400; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        eliminaButton.setOnAction(e -> gestisciEliminazione(primaryStage));

        HBox modRistoranteLayout = new HBox(10, modificaButton, eliminaButton);
        modRistoranteLayout.setAlignment(Pos.CENTER_LEFT);
        return modRistoranteLayout;
    }

    // Estrae il punteggio numerico (1-5) dal formato stringa della recensione
    private int estraiVotoDaTesto(String testo) {
        if (testo == null || !testo.contains("/5")) return -1;
        try {
            int indexFrazione = testo.indexOf("/5");
            return Integer.parseInt(testo.substring(indexFrazione - 1, indexFrazione).trim());
        } catch (Exception e) {
            System.err.println("Errore estrazione stelle: " + e.getMessage());
        }
        return -1;
    }

    // Configura il form di risposta in base allo stato (nuova risposta o modifica esistente)
    private void attivaFormRisposta(String recensioneSelezionata) {
        rispostaArea.setDisable(false);
        if (recensioneSelezionata.contains("Risposta del gestore:") || recensioneSelezionata.contains("Risposta del proprietario:")) {
            titoloRispostaLabel.setText("Modifica la risposta ufficiale precedente:");
            titoloRispostaLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #e67e22;");
            inviaRispostaButton.setText("Modifica Risposta");
            inviaRispostaButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            inviaRispostaButton.setDisable(false);
            eliminaRispostaButton.setDisable(false);

            int indexRisposta = recensioneSelezionata.indexOf("Risposta del ");
            String vecchiaRisposta = recensioneSelezionata.substring(recensioneSelezionata.indexOf(":", indexRisposta) + 1).trim();
            rispostaArea.setText(vecchiaRisposta);
        } else {
            titoloRispostaLabel.setText("Rispondi alla recensione selezionata:");
            titoloRispostaLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            inviaRispostaButton.setText("Invia Risposta Ufficiale");
            inviaRispostaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            inviaRispostaButton.setDisable(false);
            eliminaRispostaButton.setDisable(true);
            rispostaArea.clear();
        }
    }

    // Invia al server la risposta del gestore come task asincrono
    private void inviaRisposta() {
        String testoRisposta = rispostaArea.getText().trim();
        int idRecensioneReale = estraiIdRecensione(recensioniList.getSelectionModel().getSelectedItem());

        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.rispondiARecensione(idRecensioneReale, testoRisposta));
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                new Alert(Alert.AlertType.INFORMATION, "Risposta inviata!").show();
                resettaFormAzione();
                caricaRecensioni();
            }
        });
        task.setOnFailed(e -> handleConnectionError((Exception) task.getException()));
        new Thread(task).start();
    }

    // Apre la vista per la modifica dei dati del ristorante
    private void gestisciModifica(Stage currentStage) {
        Stage modificaStage = new Stage();
        currentStage.hide();
        ModificaRistoranteView modificaView = new ModificaRistoranteView(utenteLoggato, ristorante, () -> {
            currentStage.close();
            if (onRistoranteEliminato != null) onRistoranteEliminato.run();
            if (stagePrecedente != null) stagePrecedente.show();
        }, currentStage);

        modificaStage.setOnHidden(e -> {
            if (stagePrecedente != null && !stagePrecedente.isShowing() && !currentStage.isShowing()) currentStage.show();
        });

        try { modificaView.start(modificaStage); } catch (Exception e) { e.printStackTrace(); }
    }

    // Gestisce l'eliminazione definitiva del ristorante dal sistema tramite task
    private void gestisciEliminazione(Stage currentStage) {
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare il ristorante? Azione irreversibile.", ButtonType.YES, ButtonType.NO);
        conferma.showAndWait().ifPresent(risposta -> {
            if (risposta == ButtonType.YES) {
                javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return RemoteServiceConnector.esegui(s -> s.rimuoviRistorante(ristorante.getId()));
                    }
                };
                task.setOnSucceeded(e -> {
                    if (task.getValue()) {
                        new Alert(Alert.AlertType.INFORMATION, "Ristorante eliminato.").showAndWait();
                        if (onRistoranteEliminato != null) onRistoranteEliminato.run();
                        currentStage.close();
                        if (stagePrecedente != null) stagePrecedente.show();
                    } else new Alert(Alert.AlertType.ERROR, "Errore eliminazione.").showAndWait();
                });
                task.setOnFailed(e -> handleConnectionError((Exception) task.getException()));
                new Thread(task).start();
            }
        });
    }

    // Elimina la risposta ufficiale inviata dal gestore
    private void eliminaRisposta() {
        String recensioneSelezionata = recensioniList.getSelectionModel().getSelectedItem();
        if (recensioneSelezionata == null) return;

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare la risposta ufficiale?", ButtonType.YES, ButtonType.NO);
        conferma.showAndWait().ifPresent(risposta -> {
            if (risposta == ButtonType.YES) {
                try {
                    if (RemoteServiceConnector.getService().rispondiARecensione(estraiIdRecensione(recensioneSelezionata), "")) {
                        new Alert(Alert.AlertType.INFORMATION, "Risposta eliminata.").showAndWait();
                        resettaFormAzione();
                        caricaRecensioni();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    // Analizza la stringa della lista per recuperare l'ID numerico della recensione
    private int estraiIdRecensione(String rigaRecensione) {
        try {
            if (rigaRecensione != null && rigaRecensione.contains("|")) {
                return Integer.parseInt(rigaRecensione.substring(0, rigaRecensione.indexOf("|")).trim());
            }
        } catch (Exception e) { System.err.println("Errore ID: " + e.getMessage()); }
        return -1;
    }

    // Ripristina lo stato iniziale del form di risposta
    private void resettaFormAzione() {
        rispostaArea.clear();
        rispostaArea.setDisable(true);
        inviaRispostaButton.setDisable(true);
        inviaRispostaButton.setText("Invia Risposta Ufficiale");
        eliminaRispostaButton.setDisable(true);
        titoloRispostaLabel.setText("Seleziona una recensione per poter rispondere");
        titoloRispostaLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        recensioniList.getSelectionModel().clearSelection();
    }

    // Recupera dal server le recensioni del ristorante e ricalcola la media
    private void caricaRecensioni() {
        javafx.concurrent.Task<java.util.List<String>> task = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<String> call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.getRecensioniRistorante(ristorante.getId()));
            }
        };
        task.setOnSucceeded(e -> {
            var elenco = task.getValue();
            recensioniList.getItems().clear();
            if (elenco != null && !elenco.isEmpty()) {
                recensioniList.getItems().addAll(elenco);
                double totale = 0;
                for (String s : elenco) {
                    int voto = estraiVotoDaTesto(s);
                    if (voto != -1) totale += voto;
                }
                mediaStelleLabel.setText(String.format("⭐ Media: %.1f / 5", totale / elenco.size()));
            } else mediaStelleLabel.setText("⭐ Media Recensioni: N/A");
        });
        task.setOnFailed(e -> handleConnectionError((Exception) task.getException()));
        new Thread(task).start();
    }
}