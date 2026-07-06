/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per la registrazione di un nuovo utente (Grafica Modernizzata).
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.client.RemoteServiceConnector;
import it.uninsubria.theknife.common.models.Utente;
import it.uninsubria.theknife.common.models.Ruolo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

public class SigninView extends Application {

    private Stage stagePrecedente; // Memorizziamo il vecchio

    public SigninView(Stage stagePrecedente) {
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Registrazione");

        // --- PANNELLO SINISTRO ---
        VBox leftPanel = new VBox(20);
        leftPanel.setPadding(new Insets(40));
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setMinWidth(350);
        leftPanel.setMaxWidth(350);
        leftPanel.setStyle("-fx-background-color: #1e1e1e;"); // Sfondo Antracite Scuro

        // Icona del blocco note/registrazione con bagliore coerente Teal
        Label iconLabel = new Label("📋");
        iconLabel.setStyle(
                "-fx-font-size: 72px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(36, 122, 130, 0.8), 25, 0.5, 0, 0);"
        );

        Label brandLabel = new Label("The Knife");
        brandLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI', Arial;");

        Label stateLabel = new Label("📝 Registrati");
        stateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #247a82; -fx-font-weight: bold;"); // Accento Teal chiaro

        leftPanel.getChildren().addAll(iconLabel, brandLabel, stateLabel);

        // ---- PANNELLO DESTRO (Card Contenitore Form) ---
        StackPane rightPanel = new StackPane();
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #eef2f3;"); // Grigio chiarissimo di sfondo
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Card bianca principale
        VBox cardLayout = new VBox(12);
        cardLayout.setMaxWidth(460);
        cardLayout.setPadding(new Insets(25, 30, 25, 30));
        cardLayout.setAlignment(Pos.CENTER);
        cardLayout.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 4);"
        );

        Label titleLabel = new Label("Crea un nuovo account");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e4d56; -fx-padding: 0 0 5 0;");

        // Stile comune moderno per gli input
        String inputStyle =
                "-fx-background-color: #f2ede4;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 8px 12px;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: #333;";

        // --- INIZIALIZZAZIONE COMPONENTI E APPLICAZIONE STILI ---
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome *");
        nomeField.setStyle(inputStyle);
        HBox.setHgrow(nomeField, Priority.ALWAYS);

        TextField cognomeField = new TextField();
        cognomeField.setPromptText("Cognome *");
        cognomeField.setStyle(inputStyle);
        HBox.setHgrow(cognomeField, Priority.ALWAYS);

        // Affianchiamo Nome e Cognome per risparmiare spazio verticale
        HBox nomeCognomeRow = new HBox(10, nomeField, cognomeField);

        TextField userField = new TextField();
        userField.setPromptText("Username *");
        userField.setStyle(inputStyle);
        userField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password *");
        passField.setStyle(inputStyle);
        passField.setMaxWidth(Double.MAX_VALUE);

        DatePicker dataNascitaPicker = new DatePicker();
        dataNascitaPicker.setPromptText("Data di Nascita");
        dataNascitaPicker.setStyle(inputStyle);
        dataNascitaPicker.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dataNascitaPicker, Priority.ALWAYS);

        TextField indirizzoField = new TextField();
        indirizzoField.setPromptText("Indirizzo");
        indirizzoField.setStyle(inputStyle);
        HBox.setHgrow(indirizzoField, Priority.ALWAYS);

        // Row per Data Nascita e Indirizzo
        HBox anagraficaRow = new HBox(10, dataNascitaPicker, indirizzoField);

        TextField cittaField = new TextField();
        cittaField.setPromptText("Città");
        cittaField.setStyle(inputStyle);
        HBox.setHgrow(cittaField, Priority.ALWAYS);

        TextField capField = new TextField();
        capField.setPromptText("CAP");
        capField.setStyle(inputStyle);
        capField.setPrefWidth(90);

        TextField nazioneField = new TextField();
        nazioneField.setPromptText("Nazione");
        nazioneField.setStyle(inputStyle);
        HBox.setHgrow(nazioneField, Priority.ALWAYS);

        // Row compatta per Città, CAP e Nazione (come nell'immagine di riferimento)
        HBox localitaRow = new HBox(10, cittaField, capField, nazioneField);

        TextField telefonoField = new TextField();
        telefonoField.setPromptText("Telefono (Opzionale)");
        telefonoField.setStyle(inputStyle);
        telefonoField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> ruoloBox = new ComboBox<>();
        ruoloBox.getItems().addAll("Cliente Standard", "Gestore Ristorante");
        ruoloBox.setPromptText("Seleziona Ruolo *");
        ruoloBox.setMaxWidth(Double.MAX_VALUE);
        ruoloBox.getSelectionModel().selectFirst();
        // Customizzazione grafica della ComboBox per integrarsi con il tema sabbia
        ruoloBox.setStyle(
                "-fx-background-color: #f2ede4;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-font-size: 13px;"
        );

        // --- GESTIONE LOGICA DATEPICKER  ---
        LocalDate limiteMassimo = LocalDate.now().minusYears(16);
        dataNascitaPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isAfter(limiteMassimo)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #e0e0e0;");
                }
            }
        });
        dataNascitaPicker.setOnShowing(e -> {
            if (dataNascitaPicker.getValue() == null) {
                dataNascitaPicker.setValue(limiteMassimo);
                dataNascitaPicker.getEditor().clear();
            }
        });
        dataNascitaPicker.setOnHidden(e -> {
            if (dataNascitaPicker.getEditor().getText().isEmpty()) {
                dataNascitaPicker.setValue(null);
            }
        });

        // --- BOTTONI E INTERATTIVITÀ ---
        String baseBtnStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 24px; -fx-cursor: hand; -fx-text-fill: white;";
        String tealBtnStyle = "-fx-background-color: #1e4d56;" + baseBtnStyle;
        String greyBtnStyle = "-fx-background-color: #7f8c8d;" + baseBtnStyle;

        Button registerBtn = new Button("Registrati");
        registerBtn.setMinWidth(130);
        registerBtn.setStyle(tealBtnStyle);
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle("-fx-background-color: #247a82;" + baseBtnStyle));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle(tealBtnStyle));

        Button indietroBtn = new Button("⬅ Indietro");
        indietroBtn.setMinWidth(130);
        indietroBtn.setStyle(greyBtnStyle);
        indietroBtn.setOnMouseEntered(e -> indietroBtn.setStyle("-fx-background-color: #95a5a6;" + baseBtnStyle));
        indietroBtn.setOnMouseExited(e -> indietroBtn.setStyle(greyBtnStyle));
        indietroBtn.setOnAction(e -> {
            primaryStage.close();
            if (stagePrecedente != null) {
                stagePrecedente.show();
            }
        });

        HBox bottoniLayout = new HBox(15, registerBtn, indietroBtn);
        bottoniLayout.setAlignment(Pos.CENTER);
        bottoniLayout.setPadding(new Insets(10, 0, 0, 0));

        Hyperlink accediLink = new Hyperlink("Hai già un account? Accedi qui");
        accediLink.setStyle("-fx-text-fill: #247a82; -fx-font-size: 13px; -fx-font-weight: bold;");
        accediLink.setOnAction(e -> {
            Stage loginStage = new Stage();
            new LoginView(primaryStage).start(loginStage);
            primaryStage.hide();
        });

        // Composizione del Form ordinato dentro la card
        cardLayout.getChildren().addAll(
                titleLabel,
                nomeCognomeRow,
                userField,
                passField,
                anagraficaRow,
                localitaRow,
                telefonoField,
                ruoloBox,
                bottoniLayout,
                accediLink
        );
        rightPanel.getChildren().add(cardLayout);

        // --- LOGICA  ----
        registerBtn.setOnAction(e -> {
            String nome = nomeField.getText().trim();
            String cognome = cognomeField.getText().trim();
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            String ruoloScelto = ruoloBox.getValue();

            // Validazione base
            if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "I campi contrassegnati con * sono obbligatori!").showAndWait();
                return;
            }

            // Preparazione oggetto (stessa logica di prima)
            Utente nuovoUtente = new Utente();
            nuovoUtente.setNome(nome.substring(0, 1).toUpperCase() + nome.substring(1).toLowerCase());
            nuovoUtente.setCognome(cognome.substring(0, 1).toUpperCase() + cognome.substring(1).toLowerCase());
            nuovoUtente.setUsername(username);
            nuovoUtente.setPassword(password);
            nuovoUtente.setRuolo("Gestore Ristorante".equals(ruoloScelto) ? Ruolo.Gestore : Ruolo.Cliente);
            if (dataNascitaPicker.getValue() != null) nuovoUtente.setDataNascita(dataNascitaPicker.getValue());
            if (!telefonoField.getText().trim().isEmpty()) nuovoUtente.setTelefono(telefonoField.getText().trim());

            String dom = String.format("%s, %s, %s, %s", indirizzoField.getText(), cittaField.getText(), capField.getText(), nazioneField.getText());
            nuovoUtente.setDomicilio(dom);

            // Esecuzione asincrona
            javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return RemoteServiceConnector.esegui(s -> s.registraUtente(nuovoUtente));
                }
            };

            task.setOnSucceeded(ev -> {
                if (task.getValue()) {
                    new Alert(Alert.AlertType.INFORMATION, "Registrazione completata con successo!").showAndWait();
                    primaryStage.hide();
                    if (stagePrecedente != null) stagePrecedente.show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Errore: l'username potrebbe essere già in uso.").showAndWait();
                }
            });

            task.setOnFailed(ev -> {
                task.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Errore di connessione al server.").showAndWait();
            });

            new Thread(task).start();
        });

        primaryStage.setOnCloseRequest(event -> {
            if (stagePrecedente != null) {
                stagePrecedente.show();
            }
        });

        // --- ROOT COMPOSITION  ---
        HBox mainRoot = new HBox();
        mainRoot.getChildren().addAll(leftPanel, rightPanel);

        // Risoluzione desktop uniforme 900x550
        Scene scene = new Scene(mainRoot, 900, 550);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}