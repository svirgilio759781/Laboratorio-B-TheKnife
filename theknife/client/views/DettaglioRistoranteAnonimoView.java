/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per mostrare il dettaglio di un ristorante e le sue recensioni.
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.client.RemoteServiceConnector;
import it.uninsubria.theknife.common.models.Ristorante;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DettaglioRistoranteAnonimoView extends Application {

    private final Ristorante ristorante;
    private ListView<String> recensioniList;
    private Label mediaStelleLabel;
    private final Stage stagePrecedente;

    public DettaglioRistoranteAnonimoView(Ristorante ristorante, Stage stagePrecedente) {
        this.ristorante = ristorante;
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
        colonnaSinistra.setPrefWidth(430);
        colonnaSinistra.setPadding(new Insets(25));
        colonnaSinistra.setStyle(cardStyle);

        Label nomeLabel = new Label(ristorante.getNome());
        nomeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

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

        colonnaSinistra.getChildren().addAll(nomeLabel, mediaStelleLabel, infoBox, extraBox, descTitle, descScroll, indietroBtn);

        // --- COLONNA DESTRA (RECENSIONI) ---
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

        colonnaDestra.getChildren().addAll(titoloRecensioni, recensioniList);

        HBox rootLayout = new HBox(25, colonnaSinistra, colonnaDestra);
        rootLayout.setPadding(new Insets(30));
        rootLayout.setStyle(bgStyle);

        caricaRecensioni();
        primaryStage.setOnCloseRequest(e -> { if (stagePrecedente != null) stagePrecedente.show(); });
        primaryStage.setScene(new Scene(rootLayout, 1000, 600));
        primaryStage.show();
    }

    // Carica l'elenco delle recensioni dal server e aggiorna la media calcolata
    private void caricaRecensioni() {
        // Usiamo un Task per non bloccare la UI di JavaFX durante il recupero RMI
        javafx.concurrent.Task<java.util.List<String>> task = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<String> call() throws Exception {
                // Sfruttiamo il wrapper di riconnessione automatica
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
                    try {
                        int idx = s.indexOf("/5");
                        if (idx > 0) totale += Double.parseDouble(s.substring(idx - 1, idx).trim());
                    } catch (Exception ignored) {}
                }
                mediaStelleLabel.setText(String.format("⭐ Media Recensioni: %.1f / 5", totale / elenco.size()));
            } else {
                mediaStelleLabel.setText("⭐ Media Recensioni: N/A");
                recensioniList.setPlaceholder(new Label("Ancora nessuna recensione."));
            }
        });

        task.setOnFailed(e -> handleConnectionError((Exception) task.getException()));

        // Avvia il thread in background
        new Thread(task).start();
    }
}