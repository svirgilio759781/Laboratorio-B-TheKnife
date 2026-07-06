/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per la ricerca in realtime dei ristoranti.
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.client.RemoteServiceConnector;
import it.uninsubria.theknife.common.models.Ristorante;
import it.uninsubria.theknife.common.models.Utente;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CercaRistorantiClienteView extends Application {

    private TextField nomeField, locazioneField, cucinaField;
    private ComboBox<String> stelleMediaBox;
    private ToggleGroup costoGroup;
    private CheckBox prenotabileCheck, takeawayCheck, preferitiCheck;
    private ListView<Ristorante> risultatiList;
    private final Utente utenteLoggato;
    private final Stage stagePrecedente;

    public CercaRistorantiClienteView(Utente utenteLoggato, Stage stagePrecedente) {
        this.utenteLoggato = utenteLoggato;
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Cerca Ristoranti");

        String bgStyle = "-fx-background-color: #f4f7f6; -fx-font-family: 'Segoe UI', sans-serif;";
        String cardStyle = "-fx-background-color: #ffffff; -fx-padding: 25; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5);";
        String titleStyle = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;";
        String customCss = ".list-cell:selected { -fx-background-color: #d1e7dd !important; -fx-background-radius: 10; } " +
                ".list-cell:selected .label { -fx-text-fill: #2c3e50 !important; } " +
                ".scroll-bar:vertical { -fx-pref-width: 0; -fx-opacity: 0; }";

        Label headerLabel = new Label("Esplora Ristoranti");
        headerLabel.setStyle(titleStyle);
        HBox header = new HBox(headerLabel);
        header.setPadding(new Insets(10, 0, 20, 0));



        nomeField = createStyledTextField("Cerca per nome...");
        locazioneField = createStyledTextField("Cerca per città...");
        cucinaField = createStyledTextField("Tipo di cucina...");

        costoGroup = new ToggleGroup();
        HBox costoLayout = new HBox(8);
        for (String s : new String[]{"Tutti", "$", "$$", "$$$", "$$$$"}) {
            RadioButton rb = new RadioButton(s);
            rb.setToggleGroup(costoGroup);
            if (s.equals("Tutti")) rb.setSelected(true);
            costoLayout.getChildren().add(rb);
        }

        prenotabileCheck = new CheckBox("Prenotabile");
        takeawayCheck = new CheckBox("Consegna a domicilio");
        preferitiCheck = new CheckBox("Solo Preferiti");

        stelleMediaBox = new ComboBox<>();
        stelleMediaBox.getItems().addAll("Qualsiasi punteggio", "⭐ 1+ Stelle", "⭐⭐ 2+ Stelle", "⭐⭐⭐ 3+ Stelle", "⭐⭐⭐⭐ 4+ Stelle", "⭐⭐⭐⭐⭐ 5 Stelle");
        stelleMediaBox.getSelectionModel().selectFirst();
        stelleMediaBox.setMaxWidth(Double.MAX_VALUE);

        Button prenotazioneButton = new Button("📅 Le Mie Prenotazioni");
        prenotazioneButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 4px; -fx-cursor: hand;");
        prenotazioneButton.setOnAction(e -> {
            Stage prenotazioneStage = new Stage();
            VisualizzaPrenotazioniClienteView view = new VisualizzaPrenotazioniClienteView(utenteLoggato, primaryStage);
            try { primaryStage.hide(); view.start(prenotazioneStage); } catch (Exception ex) { ex.printStackTrace(); }
        });

        Button indietroBtn = new Button("⬅ Logout");
        indietroBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        indietroBtn.setOnAction(e -> { primaryStage.close(); if (stagePrecedente != null) stagePrecedente.show(); });

        HBox bottoniLayout = new HBox(10);
        bottoniLayout.setAlignment(Pos.CENTER_LEFT);
        bottoniLayout.getChildren().addAll(prenotazioneButton, indietroBtn);

        VBox sidebar = new VBox(18, new Label("Filtri di Ricerca"), nomeField, locazioneField, cucinaField, new Label("Fascia Prezzo:"), costoLayout, prenotabileCheck, takeawayCheck, preferitiCheck, new Label("Valutazione Minima:"), stelleMediaBox, bottoniLayout);
        sidebar.setStyle(cardStyle);
        sidebar.setPrefWidth(360);

        risultatiList = new ListView<>();
        risultatiList.getStylesheets().add("data:text/css," + customCss);
        risultatiList.setStyle("-fx-background-color: transparent;");
        risultatiList.setPlaceholder(new Label("Nessun ristorante trovato."));

        risultatiList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Ristorante item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    VBox card = new VBox(5);
                    card.setPadding(new Insets(15));

                    // Applichiamo lo stile CSS corretto con il raggio (radius)
                    if (getIndex() % 2 == 0) {
                        // Card Grigia
                        card.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10; -fx-border-color: #dcdcdc; -fx-border-radius: 10;");
                    } else {
                        // Card Bianca
                        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #dcdcdc; -fx-border-radius: 10;");
                    }

                    Label nome = new Label(item.getNome());
                    nome.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

                    Label info = new Label("📍 " + item.getLocazione() + "  |  🍴 " + item.getTipoCucina());
                    info.setStyle("-fx-text-fill: #7f8c8d;");

                    card.getChildren().addAll(nome, info);
                    setGraphic(card);

                    // Impostiamo la cella della ListView come trasparente
                    setStyle("-fx-background-color: transparent; -fx-padding: 5 0 5 0;");
                }
            }
        });

        risultatiList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Ristorante sel = risultatiList.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    try {
                        primaryStage.hide();
                        Stage dettaglioStage = new Stage();
                        DettaglioRistoranteClienteView dettaglio = new DettaglioRistoranteClienteView(sel, utenteLoggato, primaryStage);
                        dettaglio.start(dettaglioStage);
                        dettaglioStage.setOnCloseRequest(event -> primaryStage.show());
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        });

        VBox mainContent = new VBox(15, new Label("Risultati Trovati"), risultatiList);
        VBox.setVgrow(risultatiList, Priority.ALWAYS);
        mainContent.setStyle(cardStyle);

        // Listeners
        nomeField.textProperty().addListener((o, old, n) -> eseguiRicerca());
        locazioneField.textProperty().addListener((o, old, n) -> eseguiRicerca());
        cucinaField.textProperty().addListener((o, old, n) -> eseguiRicerca());
        costoGroup.selectedToggleProperty().addListener((o, old, n) -> eseguiRicerca());
        prenotabileCheck.selectedProperty().addListener((o, old, n) -> eseguiRicerca());
        takeawayCheck.selectedProperty().addListener((o, old, n) -> eseguiRicerca());
        preferitiCheck.selectedProperty().addListener((o, old, n) -> eseguiRicerca());
        stelleMediaBox.valueProperty().addListener((o, old, n) -> eseguiRicerca());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30));
        root.setStyle(bgStyle);
        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(mainContent);
        BorderPane.setMargin(mainContent, new Insets(0, 0, 0, 25));

        eseguiRicerca();
        primaryStage.setScene(new Scene(root, 1000, 650));
        primaryStage.show();
    }
    // Crea un campo di testo con stile UI predefinito
    private TextField createStyledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8;");
        return tf;
    }
    // Esegue la ricerca filtrata sul server via task asincrono
    private void eseguiRicerca() {
        // Usiamo un Task per mantenere la UI fluida durante la chiamata RMI
        javafx.concurrent.Task<java.util.List<Ristorante>> task = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<Ristorante> call() throws Exception {
                int costo;
                RadioButton sr = (RadioButton) costoGroup.getSelectedToggle();
                if (sr != null && !sr.getText().equals("Tutti")) costo = sr.getText().length();
                else {
                    costo = 0;
                }

                // Sfruttiamo il wrapper 'esegui' che gestisce la riconnessione automatica
                return RemoteServiceConnector.esegui(s -> s.cercaRistoranti(
                        nomeField.getText(),
                        locazioneField.getText(),
                        cucinaField.getText(),
                        costo,
                        prenotabileCheck.isSelected(),
                        takeawayCheck.isSelected(),
                        preferitiCheck.isSelected(),
                        utenteLoggato.getId(),
                        stelleMediaBox.getSelectionModel().getSelectedIndex()
                ));
            }
        };

        task.setOnSucceeded(e -> {
            risultatiList.getItems().setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Impossibile caricare i ristoranti. Il server non è raggiungibile.");
                alert.showAndWait();
            });
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}