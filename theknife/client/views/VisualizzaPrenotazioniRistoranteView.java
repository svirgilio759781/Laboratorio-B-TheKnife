/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per i ristoratori: visualizza e gestisce le prenotazioni del proprio ristorante.
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.client.RemoteServiceConnector;
import it.uninsubria.theknife.common.models.Prenotazione;
import it.uninsubria.theknife.common.models.StatoPrenotazione;
import it.uninsubria.theknife.common.models.Ristorante;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class VisualizzaPrenotazioniRistoranteView extends Application {

    private final Ristorante ristoranteCorrente;
    private final Stage stagePrecedente;
    private ListView<Prenotazione> prenotazioniList;
    private DatePicker dataMinPicker;
    private DatePicker dataMaxPicker;
    private Button confermaBtn;
    private Button rifiutaBtn;
    private Label infoDettaglioLabel;

    public VisualizzaPrenotazioniRistoranteView(Ristorante ristoranteCorrente, Stage stagePrecedente) {
        this.ristoranteCorrente = ristoranteCorrente;
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Gestione Prenotazioni (" + ristoranteCorrente.getNome() + ")");

        // --- BARRA SUPERIORE: FILTRI VELOCI ---
        HBox barraSuperiore = new HBox(15);
        barraSuperiore.setPadding(new Insets(15));
        barraSuperiore.setAlignment(Pos.CENTER_LEFT);
        barraSuperiore.setStyle("-fx-background-color: #34495e;");

        Label titololoBarra = new Label("🏪 " + ristoranteCorrente.getNome().toUpperCase());
        titololoBarra.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label filtroLabel = new Label("Filtra per giorno:");
        filtroLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label daLabel = new Label("Dal:");
        daLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        dataMinPicker = new DatePicker(LocalDate.now()); // Default oggi
        dataMinPicker.setPrefWidth(120);
        dataMinPicker.valueProperty().addListener((obs, vecchio, nuovo) -> caricaPrenotazioniRistorante());

        Label aLabel = new Label("Al:");
        aLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        dataMaxPicker = new DatePicker(LocalDate.now().plusWeeks(1)); // Default tra una settimana
        dataMaxPicker.setPrefWidth(120);
        dataMaxPicker.valueProperty().addListener((obs, vecchio, nuovo) -> caricaPrenotazioniRistorante());

        Button resetFiltroBtn = new Button("Mostra Tutto");
        resetFiltroBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-cursor: hand;");
        resetFiltroBtn.setOnAction(e -> {
            // Resettiamo entrambi gli estremi dell'intervallo
            dataMinPicker.setValue(null);
            dataMaxPicker.setValue(null);

            // Ricarichiamo la lista (che ora non avendo filtri, mostrerà tutto)
            caricaPrenotazioniRistorante();
        });

        // Spingiamo il bottone indietro tutto a destra
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button indietroBtn = new Button("⬅ Chiudi");
        indietroBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        indietroBtn.setOnAction(e -> {
            primaryStage.close();
            if (stagePrecedente != null) stagePrecedente.show();
        });

        barraSuperiore.getChildren().addAll(titololoBarra, new Separator(javafx.geometry.Orientation.VERTICAL), daLabel, dataMinPicker, aLabel, dataMaxPicker, resetFiltroBtn, spacer, indietroBtn);

        // --- PANNELLO LATERALE DESTRO: AZIONI DI APPROVAZIONE/RIFIUTO ---
        VBox pannelloAzioni = new VBox(15);
        pannelloAzioni.setPadding(new Insets(15));
        pannelloAzioni.setPrefWidth(300);
        pannelloAzioni.setStyle("-fx-background-color: #f8f9fa;");

        Label titoloAzioni = new Label("⚙️ Gestione Stato");
        titoloAzioni.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        infoDettaglioLabel = new Label("Seleziona una richiesta dalla lista per validarla.");
        infoDettaglioLabel.setWrapText(true);
        infoDettaglioLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");

        confermaBtn = new Button("✅ Approva / Conferma");
        confermaBtn.setMaxWidth(Double.MAX_VALUE);
        confermaBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        confermaBtn.setDisable(true);
        confermaBtn.setOnAction(e -> cambiaStatoSelezionata(StatoPrenotazione.Confermato));

        rifiutaBtn = new Button("❌ Rifiuta / Cancella");
        rifiutaBtn.setMaxWidth(Double.MAX_VALUE);
        rifiutaBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        rifiutaBtn.setDisable(true);
        rifiutaBtn.setOnAction(e -> cambiaStatoSelezionata(StatoPrenotazione.Cancellato));

        pannelloAzioni.getChildren().addAll(titoloAzioni, infoDettaglioLabel, confermaBtn, rifiutaBtn);

        // --- LISTA PRENOTAZIONI ---
        prenotazioniList = new ListView<>();
        prenotazioniList.setPlaceholder(new Label("Nessuna prenotazione trovata per i criteri selezionati."));
        HBox.setHgrow(prenotazioniList, Priority.ALWAYS);

        // Rendering grafico personalizzato delle celle per il ristoratore
        prenotazioniList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Prenotazione p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    String note = (p.getNote() != null && !p.getNote().trim().isEmpty()) ? " | 📝 Note: " + p.getNote() : "";

                    // Mostriamo ID Utente (se nel modello passi il nome utente, usa p.getNomeUtente())
                    setText(String.format("👤 Cliente ID: %d\n📅 Giorno: %s  |  ⏰ Ora: %s  |  👥 Coperti: %d\n📌 STATO ATTUALE: [%s]%s",
                            p.getIdCliente(),
                            p.getData().toString(),
                            p.getOra().toString(),
                            p.getNumeroPersone(),
                            p.getStato().name(),
                            note
                    ));

                    // Colorazione minimale in base allo stato attuale per colpo d'occhio veloce
                    if (p.getStato().name().equalsIgnoreCase("CONFERMATO")) {
                        setStyle("-fx-border-color: #2ecc71; -fx-border-width: 0 0 0 5px; -fx-padding: 8px;");
                    } else if (p.getStato().name().equalsIgnoreCase("CANCELLATO")) {
                        setStyle("-fx-border-color: #e74c3c; -fx-border-width: 0 0 0 5px; -fx-padding: 8px;");
                    } else {
                        setStyle("-fx-border-color: #f1c40f; -fx-border-width: 0 0 0 5px; -fx-padding: 8px;"); // In attesa
                    }
                }
            }
        });

        // Intercettiamo il click per sbloccare le azioni di approvazione
        prenotazioniList.getSelectionModel().selectedItemProperty().addListener((obs, vecchio, selezionata) -> {
            if (selezionata != null) {
                infoDettaglioLabel.setText("Modifica lo stato per la prenotazione del giorno " + selezionata.getData() +
                        " alle ore " + selezionata.getOra() + " (" + selezionata.getNumeroPersone() + " persone).");

                String stato = selezionata.getStato().name().toUpperCase();
                // Gestione dei bottoni in base allo stato in cui si trova la prenotazione
                confermaBtn.setDisable(stato.contains("CONFERMATO") || stato.contains("CANCELLATO"));
                rifiutaBtn.setDisable(stato.contains("CANCELLATO"));
            } else {
                infoDettaglioLabel.setText("Seleziona una richiesta dalla lista per validarla.");
                confermaBtn.setDisable(true);
                rifiutaBtn.setDisable(true);
            }
        });

        // Layout di allineamento inferiore
        HBox corpoCentrale = new HBox(prenotazioniList, pannelloAzioni);
        VBox.setVgrow(corpoCentrale, Priority.ALWAYS);

        VBox rootLayout = new VBox(barraSuperiore, corpoCentrale);

        // Caricamento asincrono iniziale
        caricaPrenotazioniRistorante();

        primaryStage.setOnCloseRequest(e -> {
            if (stagePrecedente != null) stagePrecedente.show();
        });

        Scene scene = new Scene(rootLayout, 900, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void caricaPrenotazioniRistorante() {
        // Usiamo una variabile locale per il task per catturare correttamente lo stato attuale dei DatePicker
        LocalDate min = dataMinPicker.getValue();
        LocalDate max = dataMaxPicker.getValue();

        javafx.concurrent.Task<List<Prenotazione>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<Prenotazione> call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.getPrenotazioniRistorante(ristoranteCorrente.getId()));
            }
        };

        task.setOnSucceeded(e -> {
            prenotazioniList.getItems().clear();
            List<Prenotazione> elenco = task.getValue();
            if (elenco != null) {
                for (Prenotazione p : elenco) {
                    if (p.getData() == null) continue;
                    boolean dopoMin = (min == null || !p.getData().isBefore(min));
                    boolean primaMax = (max == null || !p.getData().isAfter(max));
                    if (dopoMin && primaMax) {
                        prenotazioniList.getItems().add(p);
                    }
                }
            }
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore nel caricamento delle prenotazioni.").showAndWait();
        });

        new Thread(task).start();
    }

    private void cambiaStatoSelezionata(StatoPrenotazione nuovoStato) {
        Prenotazione selezionata = prenotazioniList.getSelectionModel().getSelectedItem();
        if (selezionata == null) return;

        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.aggiornaStatoPrenotazione(selezionata.getId(), nuovoStato.name()));
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                selezionata.setStato(nuovoStato);
                prenotazioniList.refresh();
                // Forza il refresh della UI di dettaglio
                prenotazioniList.getSelectionModel().clearSelection();
                prenotazioniList.getSelectionModel().select(selezionata);
                new Alert(Alert.AlertType.INFORMATION, "Stato aggiornato a " + nuovoStato.name()).showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Errore durante l'aggiornamento.").showAndWait();
            }
        });

        task.setOnFailed(e -> new Alert(Alert.AlertType.ERROR, "Errore di connessione RMI.").showAndWait());
        new Thread(task).start();
    }
}