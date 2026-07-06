/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per la modifica di un ristorante esistente.
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

public class ModificaRistoranteView extends Application {

    private final Utente gestoreLoggato;
    private final Ristorante ristoranteDaModificare;
    private final Runnable onRistoranteModificato;
    private final Stage stagePrecedente;

    private TextField nomeField, indirizzoField, locazioneField, cucinaField, latField, lngField, telefonoField, urlField, websiteUrlField, premiField, serviziField;
    private ComboBox<String> prezzoBox;
    private TextArea descrizioneArea;
    private CheckBox greenStarCheck, consegnaCheck, prenotazioneCheck;

    public ModificaRistoranteView(Utente gestoreLoggato, Ristorante ristoranteDaModificare, Runnable onRistoranteModificato, Stage stagePrecedente) {
        this.gestoreLoggato = gestoreLoggato;
        this.ristoranteDaModificare = ristoranteDaModificare;
        this.onRistoranteModificato = onRistoranteModificato;
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Modifica: " + ristoranteDaModificare.getNome());

        Label titoloLabel = new Label("Modifica Dati Ristorante");
        titoloLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        // Campi Anagrafici
        nomeField = addField(grid, "Nome Ristorante *", 0, ristoranteDaModificare.getNome());
        cucinaField = addField(grid, "Tipo Cucina *", 1, ristoranteDaModificare.getTipoCucina());
        indirizzoField = addField(grid, "Indirizzo *", 2, ristoranteDaModificare.getIndirizzo());
        locazioneField = addField(grid, "Città *", 3, ristoranteDaModificare.getLocazione());
        telefonoField = addField(grid, "Telefono *", 4, ristoranteDaModificare.getTelefono());

        // Fascia Prezzo
        prezzoBox = new ComboBox<>();
        prezzoBox.getItems().addAll("$", "$$", "$$$", "$$$$");
        prezzoBox.getSelectionModel().select(ristoranteDaModificare.getPrezzo());
        prezzoBox.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Fascia Prezzo:"), 0, 5);
        grid.add(prezzoBox, 1, 5);

        latField = addField(grid, "Latitudine (-90/90)", 6, String.valueOf(ristoranteDaModificare.getLatitudine()));
        lngField = addField(grid, "Longitudine (-180/180)", 7, String.valueOf(ristoranteDaModificare.getLongitudine()));

        // Campi Extra
        urlField = addField(grid, "URL Guida (Opz.):", 8, ristoranteDaModificare.getUrl());
        websiteUrlField = addField(grid, "Sito Web (Opz.):", 9, ristoranteDaModificare.getWebsiteUrl());
        premiField = addField(grid, "Premi (Opz.):", 10, ristoranteDaModificare.getPremi());
        serviziField = addField(grid, "Servizi (Opz.):", 11, ristoranteDaModificare.getServizi());

        // Sezione Opzioni
        greenStarCheck = new CheckBox("Stella Verde");
        greenStarCheck.setSelected(ristoranteDaModificare.getGreenStar() == 1);
        consegnaCheck = new CheckBox("Consegna");
        consegnaCheck.setSelected(ristoranteDaModificare.isConsegna());
        prenotazioneCheck = new CheckBox("Prenotazione Online");
        prenotazioneCheck.setSelected(ristoranteDaModificare.isPrenotazione());

        HBox flags = new HBox(15, greenStarCheck, consegnaCheck, prenotazioneCheck);
        grid.add(new Label("Servizi Extra:"), 0, 12);
        grid.add(flags, 1, 12);

        // Descrizione
        descrizioneArea = new TextArea(ristoranteDaModificare.getDescrizione());
        descrizioneArea.setPrefRowCount(3);
        descrizioneArea.setPromptText("Breve descrizione...");
        grid.add(new Label("Descrizione:"), 0, 13);
        grid.add(descrizioneArea, 1, 13);

        // Bottoni
        Button salvaButton = new Button("💾 Salva Modifiche");
        salvaButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        salvaButton.setOnAction(e -> gestisciModifica(primaryStage));

        Button indietroBtn = new Button("Annulla");
        indietroBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20;");
        indietroBtn.setOnAction(e -> { primaryStage.close(); if(stagePrecedente != null) stagePrecedente.show(); });

        HBox buttonBox = new HBox(15, salvaButton, indietroBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(20, titoloLabel, new Separator(), grid, buttonBox);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-width: 1;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        primaryStage.setScene(new Scene(scrollPane, 650, 750));
        primaryStage.show();
    }

    private TextField addField(GridPane grid, String label, int row, String value) {
        grid.add(new Label(label), 0, row);
        TextField tf = new TextField(value != null ? value : "");
        tf.setPrefWidth(350);
        grid.add(tf, 1, row);
        return tf;
    }

    private void gestisciModifica(Stage currentStage) {
        // Validazione input
        String nome = nomeField.getText().trim();
        String cucina = cucinaField.getText().trim();
        String indirizzo = indirizzoField.getText().trim();
        String locazione = locazioneField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String prezzo = prezzoBox.getValue();

        if (nome.isEmpty() || cucina.isEmpty() || indirizzo.isEmpty() || locazione.isEmpty() || telefono.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "I campi obbligatori (*) non possono essere vuoti!").showAndWait();
            return;
        }

        Double latitudine, longitudine;
        try {
            latitudine = Double.valueOf(latField.getText().trim());
            longitudine = Double.valueOf(lngField.getText().trim());
            if (latitudine < -90 || latitudine > 90 || longitudine < -180 || longitudine > 180) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Coordinate geografiche non valide!").showAndWait();
            return;
        }

        // Aggiornamento dell'oggetto modello
        ristoranteDaModificare.setNome(nome);
        ristoranteDaModificare.setTipoCucina(cucina);
        ristoranteDaModificare.setIndirizzo(indirizzo);
        ristoranteDaModificare.setLocazione(locazione);
        ristoranteDaModificare.setLatitudine(latitudine);
        ristoranteDaModificare.setLongitudine(longitudine);
        ristoranteDaModificare.setTelefono(telefono);
        ristoranteDaModificare.setPrezzo(prezzo);
        ristoranteDaModificare.setUrl(urlField.getText().trim().isEmpty() ? null : urlField.getText().trim());
        ristoranteDaModificare.setWebsiteUrl(websiteUrlField.getText().trim().isEmpty() ? null : websiteUrlField.getText().trim());
        ristoranteDaModificare.setPremi(premiField.getText().trim().isEmpty() ? null : premiField.getText().trim());
        ristoranteDaModificare.setServizi(serviziField.getText().trim().isEmpty() ? null : serviziField.getText().trim());
        ristoranteDaModificare.setDescrizione(descrizioneArea.getText().trim().isEmpty() ? null : descrizioneArea.getText().trim());
        ristoranteDaModificare.setGreenStar(greenStarCheck.isSelected() ? 1 : 0);
        ristoranteDaModificare.setConsegna(consegnaCheck.isSelected());
        ristoranteDaModificare.setPrenotazione(prenotazioneCheck.isSelected());

        // 3. Esecuzione asincrona
        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return RemoteServiceConnector.esegui(s -> s.aggiornaRistorante(ristoranteDaModificare));
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                new Alert(Alert.AlertType.INFORMATION, "Ristorante aggiornato con successo!").showAndWait();
                currentStage.close();
                if (onRistoranteModificato != null) onRistoranteModificato.run();
            } else {
                new Alert(Alert.AlertType.ERROR, "Impossibile aggiornare i dati nel database.").showAndWait();
            }
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore di connessione al server.").showAndWait();
        });

        new Thread(task).start();
    }
}