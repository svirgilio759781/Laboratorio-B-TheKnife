/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per visualizzare e gestire lo storico delle proprie prenotazioni (da oggi in poi).
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.client.RemoteServiceConnector;
import it.uninsubria.theknife.common.models.Prenotazione;
import it.uninsubria.theknife.common.models.StatoPrenotazione;
import it.uninsubria.theknife.common.models.Utente;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class VisualizzaPrenotazioniClienteView extends Application {

    private final Utente utenteLoggato;
    private final Stage stagePrecedente;
    private ListView<Prenotazione> prenotazioniList;
    private Button annullaPrenotazioneBtn;
    private Label riepilogoLabel;
    private DatePicker editDataPicker;
    private ComboBox<String> editOraBox;
    private Spinner<Integer> editPersoneSpinner;
    private Button salvaModificaBtn;
    private TitledPane sezioneModifica;

    public VisualizzaPrenotazioniClienteView(Utente utenteLoggato, Stage stagePrecedente) {
        this.utenteLoggato = utenteLoggato;
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Le Mie Prenotazioni");

        // --- BARRA SUPERIORE ---
        HBox barraSuperiore = new HBox(15);
        barraSuperiore.setPadding(new Insets(15));
        barraSuperiore.setAlignment(Pos.CENTER_LEFT);
        barraSuperiore.setStyle("-fx-background-color: #34495e;");

        Label titololoBarra = new Label("📅 LE MIE PRENOTAZIONI");
        titololoBarra.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button indietroBtn = new Button("⬅ Chiudi");
        indietroBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        indietroBtn.setOnAction(e -> { primaryStage.close(); if(stagePrecedente != null) stagePrecedente.show(); });

        barraSuperiore.getChildren().addAll(titololoBarra, spacer, indietroBtn);

        // --- PANNELLO AZIONI (DESTRA) ---
        VBox pannelloAzioni = new VBox(15);
        pannelloAzioni.setPadding(new Insets(15));
        pannelloAzioni.setPrefWidth(300);
        pannelloAzioni.setStyle("-fx-background-color: #f8f9fa;");

        Label titoloAzioni = new Label("⚙️ Gestione");
        titoloAzioni.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        riepilogoLabel = new Label("Seleziona una prenotazione per vedere i dettagli.");
        riepilogoLabel.setWrapText(true);
        riepilogoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");

        annullaPrenotazioneBtn = new Button("❌ Annulla Prenotazione");
        annullaPrenotazioneBtn.setMaxWidth(Double.MAX_VALUE);
        annullaPrenotazioneBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        annullaPrenotazioneBtn.setDisable(true);
        annullaPrenotazioneBtn.setOnAction(e -> gestisciAnnullamento());

        pannelloAzioni.getChildren().addAll(titoloAzioni, riepilogoLabel, annullaPrenotazioneBtn);

        editDataPicker = new DatePicker(LocalDate.now());
        // Blocco date passate
        editDataPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        editOraBox = new ComboBox<>();
        editOraBox.setPrefWidth(90);

        // Listener: ogni volta che cambia la data, ricalcola gli orari
        editDataPicker.valueProperty().addListener((obs, vecchiaData, nuovaData) -> {
            aggiornaOrariDisponibili(nuovaData);
        });
        editPersoneSpinner = new Spinner<>(1, 20, 2);
        salvaModificaBtn = new Button("💾 Salva Modifiche");
        salvaModificaBtn.setDisable(true);
        salvaModificaBtn.setOnAction(e -> gestisciModifica());

        VBox boxModifica = new VBox(10, new Label("Nuova Data/Ora:"), editDataPicker, editOraBox, editPersoneSpinner, salvaModificaBtn);
        sezioneModifica = new TitledPane("Modifica Prenotazione", boxModifica);
        sezioneModifica.setExpanded(false); // Inizialmente chiusa

        pannelloAzioni.getChildren().add(sezioneModifica);

        // --- LISTA PRENOTAZIONI ---
        prenotazioniList = new ListView<>();
        prenotazioniList.setPlaceholder(new Label("Nessuna prenotazione attiva."));
        HBox.setHgrow(prenotazioniList, Priority.ALWAYS);

        prenotazioniList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Prenotazione p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null); setStyle("");
                } else {
                    String note = (p.getNote() != null && !p.getNote().isEmpty()) ? " | 📝 Note: " + p.getNote() : "";
                    setText(String.format("🏪 Ristorante ID: %d\n📅 Data: %s  |  ⏰ Ora: %s  |  👥 Persone: %d\n📌 STATO: [%s]%s",
                            p.getIdRistorante(), p.getData(), p.getOra(), p.getNumeroPersone(), p.getStato().name(), note));

                    // Colori stato
                    String color = p.getStato().name().equalsIgnoreCase("CONFERMATO") ? "#2ecc71" :
                            p.getStato().name().equalsIgnoreCase("CANCELLATO") ? "#e74c3c" : "#f1c40f";
                    setStyle("-fx-border-color: " + color + "; -fx-border-width: 0 0 0 5px; -fx-padding: 8px;");
                }
            }
        });

        prenotazioniList.getSelectionModel().selectedItemProperty().addListener((obs, v, n) -> {
            if (n != null) {
                riepilogoLabel.setText("Modifica prenotazione del " + n.getData());
                annullaPrenotazioneBtn.setDisable(n.getStato().name().equalsIgnoreCase("CANCELLATO"));

                editDataPicker.setValue(n.getData());

                // Aggiorna gli orari in base alla data selezionata
                aggiornaOrariDisponibili(n.getData());

                // Seleziona l'orario originale
                editOraBox.getSelectionModel().select(n.getOra().toString().substring(0, 5));

                editPersoneSpinner.getValueFactory().setValue(n.getNumeroPersone());
                salvaModificaBtn.setDisable(false);
                sezioneModifica.setExpanded(true);
            }
        });

        // LAYOUT FINALE
        HBox corpoCentrale = new HBox(prenotazioniList, pannelloAzioni);
        VBox rootLayout = new VBox(barraSuperiore, corpoCentrale);
        VBox.setVgrow(corpoCentrale, Priority.ALWAYS);

        caricaPrenotazioniAttive();
        primaryStage.setScene(new Scene(rootLayout, 850, 500));
        primaryStage.show();
    }
    // Carica le prenotazioni future dell'utente corrente
    private void caricaPrenotazioniAttive() {
        javafx.concurrent.Task<List<Prenotazione>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<Prenotazione> call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.getPrenotazioniCliente(utenteLoggato.getId()));
            }
        };

        task.setOnSucceeded(e -> {
            prenotazioniList.getItems().clear();
            List<Prenotazione> elenco = task.getValue();
            if (elenco != null) {
                LocalDate oggi = LocalDate.now();
                // Filtra solo le prenotazioni odierne o future
                for (Prenotazione p : elenco) {
                    if (p.getData() != null && (p.getData().isEqual(oggi) || p.getData().isAfter(oggi))) {
                        prenotazioniList.getItems().add(p);
                    }
                }
            }
        });

        task.setOnFailed(e -> System.err.println("Errore caricamento prenotazioni: " + task.getException().getMessage()));
        new Thread(task).start();
    }
    // Gestisce l'annullamento della prenotazione selezionata previa conferma
    private void gestisciAnnullamento() {
        Prenotazione selezionata = prenotazioniList.getSelectionModel().getSelectedItem();
        if (selezionata == null) return;

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION, "Annullare la prenotazione?", ButtonType.YES, ButtonType.NO);
        conferma.showAndWait().ifPresent(risposta -> {
            if (risposta == ButtonType.YES) {
                // Task asincrono per aggiornare lo stato nel database via RMI
                javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return RemoteServiceConnector.esegui(s ->
                                s.aggiornaStatoPrenotazione(selezionata.getId(), StatoPrenotazione.Cancellato.name()));
                    }
                };

                task.setOnSucceeded(e -> {
                    if (task.getValue()) {
                        new Alert(Alert.AlertType.INFORMATION, "Prenotazione annullata.").showAndWait();
                        caricaPrenotazioniAttive(); // Ricarica lista
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Errore nell'annullamento.").showAndWait();
                    }
                });

                task.setOnFailed(e -> new Alert(Alert.AlertType.ERROR, "Errore di connessione.").showAndWait());
                new Thread(task).start();
            }
        });
    }

    private void gestisciModifica() {
        Prenotazione p = prenotazioniList.getSelectionModel().getSelectedItem();
        if (p == null) return;

        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return RemoteServiceConnector.esegui(s ->
                        s.modificaPrenotazione(p.getId(), editDataPicker.getValue(),
                                java.time.LocalTime.parse(editOraBox.getValue()),
                                editPersoneSpinner.getValue(),
                                StatoPrenotazione.In_attesa.name()));
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                new Alert(Alert.AlertType.INFORMATION, "Modifica inviata! Stato impostato in attesa.").showAndWait();
                caricaPrenotazioniAttive();
            }
        });
        new Thread(task).start();
    }
    private void aggiornaOrariDisponibili(LocalDate dataSelezionata) {
        editOraBox.getItems().clear();
        final String[] TUTTI_GLI_ORARI = {"12:00", "12:30", "13:00", "13:30", "14:00", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00"};

        java.time.LocalTime oraAdesso = java.time.LocalTime.now();
        boolean isOggi = dataSelezionata != null && dataSelezionata.isEqual(LocalDate.now());

        for (String orario : TUTTI_GLI_ORARI) {
            java.time.LocalTime timeOrario = java.time.LocalTime.parse(orario);

            // Se è oggi, mostriamo solo orari successivi a quello attuale
            if (!isOggi || timeOrario.isAfter(oraAdesso)) {
                editOraBox.getItems().add(orario);
            }
        }

        // Seleziona il primo disponibile o resetta
        if (!editOraBox.getItems().isEmpty()) {
            editOraBox.getSelectionModel().select(0);
        } else {
            editOraBox.setPromptText("Nessun orario");
        }
    }
}