/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per l'inserimento di un nuovo ristorante basata sul modello ufficiale.
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

public class InserireRistoranteView extends Application {

    private final Utente gestoreLoggato;
    private final Runnable onRistoranteInserito;
    private final Stage stagePrecedente;

    private TextField nomeField, indirizzoField, locazioneField, cucinaField, latField, lngField, telefonoField, urlField, websiteUrlField, premiField, serviziField;
    private ComboBox<String> prezzoBox;
    private TextArea descrizioneArea;
    private CheckBox greenStarCheck, consegnaCheck, prenotazioneCheck;

    public InserireRistoranteView(Utente gestoreLoggato, Runnable onRistoranteInserito, Stage stagePrecedente) {
        this.gestoreLoggato = gestoreLoggato;
        this.onRistoranteInserito = onRistoranteInserito;
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Gestione Ristoranti");

        // UI Components
        Label titoloLabel = new Label("Registra un Nuovo Ristorante");
        titoloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Layout principale
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        // Helper per campi
        nomeField = addField(grid, "Nome Ristorante *", 0);
        cucinaField = addField(grid, "Tipo Cucina *", 1);
        indirizzoField = addField(grid, "Indirizzo *", 2);
        locazioneField = addField(grid, "Città *", 3);
        telefonoField = addField(grid, "Telefono *", 4);

        prezzoBox = new ComboBox<>();
        prezzoBox.getItems().addAll("$", "$$", "$$$", "$$$$");
        prezzoBox.setPromptText("Seleziona fascia prezzo");
        prezzoBox.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Fascia Prezzo:"), 0, 5);
        grid.add(prezzoBox, 1, 5);

        latField = addField(grid, "Latitudine (-90/90)", 6);
        lngField = addField(grid, "Longitudine (-180/180)", 7);

        // Sezione Opzioni
        HBox flags = new HBox(15,
                greenStarCheck = new CheckBox("Stella Verde"),
                consegnaCheck = new CheckBox("Consegna"),
                prenotazioneCheck = new CheckBox("Prenotazione Online")
        );
        grid.add(new Label("Servizi Extra:"), 0, 8);
        grid.add(flags, 1, 8);

        // Descrizione
        descrizioneArea = new TextArea();
        descrizioneArea.setPrefRowCount(3);
        descrizioneArea.setPromptText("Breve descrizione del ristorante...");
        grid.add(new Label("Descrizione:"), 0, 9);
        grid.add(descrizioneArea, 1, 9);

        // Bottoni
        Button salvaButton = new Button("Salva Ristorante");
        salvaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        salvaButton.setOnAction(e -> gestisciSalvataggio(primaryStage));

        Button indietroBtn = new Button("Annulla");
        indietroBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20;");
        indietroBtn.setOnAction(e -> { primaryStage.close(); if(stagePrecedente != null) stagePrecedente.show(); });

        HBox buttonBox = new HBox(15, salvaButton, indietroBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(20, titoloLabel, new Separator(), grid, buttonBox);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-width: 1;");

        primaryStage.setScene(new Scene(root, 600, 700));
        primaryStage.show();
    }

    private TextField addField(GridPane grid, String label, int row) {
        grid.add(new Label(label), 0, row);
        TextField tf = new TextField();
        tf.setPrefWidth(300);
        grid.add(tf, 1, row);
        return tf;
    }

    private void gestisciSalvataggio(Stage currentStage) {
        String nome = nomeField.getText().trim();
        String cucina = cucinaField.getText().trim();
        String indirizzo = indirizzoField.getText().trim();
        String locazione = locazioneField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String prezzo = prezzoBox.getValue();

        // Validazione base
        if (nome.isEmpty() || cucina.isEmpty() || indirizzo.isEmpty() || locazione.isEmpty() || telefono.isEmpty() || prezzo == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tutti i campi contrassegnati con * e la fascia prezzo sono obbligatori!");
            alert.showAndWait();
            return;
        }

        // Validazione coordinate
        Double latitudine, longitudine;
        try {
            latitudine = Double.valueOf(latField.getText().trim());
            longitudine = Double.valueOf(lngField.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Coordinate non valide.").showAndWait();
            return;
        }

        // Creazione oggetto Ristorante
        Ristorante nuovoRistorante = new Ristorante(0, nome, indirizzo, locazione, prezzo, cucina, latitudine, longitudine, telefono);

        // Set dei campi opzionali (Verificando che non siano null)
        if (urlField != null && !urlField.getText().trim().isEmpty())
            nuovoRistorante.setUrl(urlField.getText().trim());

        if (websiteUrlField != null && !websiteUrlField.getText().trim().isEmpty())
            nuovoRistorante.setWebsiteUrl(websiteUrlField.getText().trim());

        if (premiField != null && !premiField.getText().trim().isEmpty())
            nuovoRistorante.setPremi(premiField.getText().trim());

        if (serviziField != null && !serviziField.getText().trim().isEmpty())
            nuovoRistorante.setServizi(serviziField.getText().trim());

        if (descrizioneArea != null && !descrizioneArea.getText().trim().isEmpty())
            nuovoRistorante.setDescrizione(descrizioneArea.getText().trim());

        // Set booleani
        nuovoRistorante.setGreenStar(greenStarCheck.isSelected() ? 1 : 0);
        nuovoRistorante.setConsegna(consegnaCheck.isSelected());
        nuovoRistorante.setPrenotazione(prenotazioneCheck.isSelected());
        nuovoRistorante.setidGestore(gestoreLoggato.getId());

        // Esecuzione asincrona
        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.inserireRistorante(nuovoRistorante, gestoreLoggato.getId()));
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                new Alert(Alert.AlertType.INFORMATION, "Ristorante registrato con successo!").showAndWait();
                if (onRistoranteInserito != null) onRistoranteInserito.run();
                currentStage.close();
                if (stagePrecedente != null) stagePrecedente.show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Errore durante l'inserimento nel database.").showAndWait();
            }
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore di connessione al server.").showAndWait();
        });

        new Thread(task).start();
    }
}