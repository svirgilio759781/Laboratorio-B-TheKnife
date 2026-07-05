/**
 * Stefano Virgilio 759781 VA
 * Interfaccia grafica per l'autenticazione dell'utente (Grafica Modernizzata).
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.client.RemoteServiceConnector;
import it.uninsubria.theknife.common.models.Ruolo;
import it.uninsubria.theknife.common.models.Utente;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class LoginView extends Application {

    private final Stage stagePrecedente; // Memorizziamo la HomeView

    // Il costruttore accetta lo stage della prima vista (Home)
    public LoginView(Stage stagePrecedente) {
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Login");

        // --- PANNELLO SINISTRO (Brand e Icona) ---
        VBox leftPanel = new VBox(20);
        leftPanel.setPadding(new Insets(40));
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setMinWidth(350);
        leftPanel.setMaxWidth(350);
        leftPanel.setStyle("-fx-background-color: #1e1e1e;"); // Sfondo Antracite Scuro

        // Icona stilizzata del lucchetto con bagliore (creata tramite CSS e Label)
        Label iconLabel = new Label("🔒");
        iconLabel.setStyle(
                "-fx-font-size: 72px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(36, 122, 130, 0.8), 25, 0.5, 0, 0);" // Bagliore Teal come in foto
        );

        Label brandLabel = new Label("The Knife");
        brandLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI', Arial;");

        Label stateLabel = new Label("🔒 Accedi");
        stateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Accento arancione/giallo

        leftPanel.getChildren().addAll(iconLabel, brandLabel, stateLabel);

        // --- PANNELLO DESTRO (Card Chiara con il Form) ---
        StackPane rightPanel = new StackPane();
        rightPanel.setPadding(new Insets(40));
        rightPanel.setStyle("-fx-background-color: #eef2f3;"); // Sfondo grigio chiarissimo di base
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // La Card centrale semitrasparente/bianca
        VBox cardLayout = new VBox(18);
        cardLayout.setMaxWidth(360);
        cardLayout.setMaxHeight(340);
        cardLayout.setPadding(new Insets(30, 35, 30, 35));
        cardLayout.setAlignment(Pos.CENTER);
        cardLayout.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 4);"
        );

        Label titleLabel = new Label("The Knife");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1e4d56;");

        // Stile comune per i campi di input
        String inputStyle =
                "-fx-background-color: #f2ede4;" + // Tonalità sabbia chiarissima come nel mockup
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 10px;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #333;";

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(280);
        userField.setStyle(inputStyle);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(280);
        passField.setStyle(inputStyle);

        // Stili per i bottoni
        String baseBtnStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-text-fill: white;";
        String tealBtnStyle = "-fx-background-color: #1e4d56;" + baseBtnStyle;
        String greyBtnStyle = "-fx-background-color: #7f8c8d;" + baseBtnStyle;

        Button loginBtn = new Button("Accedi");
        loginBtn.setMinWidth(130);
        loginBtn.setStyle(tealBtnStyle);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #247a82;" + baseBtnStyle));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(tealBtnStyle));

        Button indietroBtn = new Button("⬅ Indietro");
        indietroBtn.setMinWidth(130);
        indietroBtn.setStyle(greyBtnStyle);
        indietroBtn.setOnMouseEntered(e -> indietroBtn.setStyle("-fx-background-color: #95a5a6;" + baseBtnStyle));
        indietroBtn.setOnMouseExited(e -> indietroBtn.setStyle(greyBtnStyle));
        indietroBtn.setOnAction(e -> {
            primaryStage.close(); // Chiude la LoginView
            if (stagePrecedente != null) {
                stagePrecedente.show(); // Torna esplicitamente alla Home
            }
        });

        HBox bottoniLayout = new HBox(15, loginBtn, indietroBtn);
        bottoniLayout.setAlignment(Pos.CENTER);

        Hyperlink registratiLink = new Hyperlink("Non hai un account? Registrati qui");
        registratiLink.setStyle("-fx-text-fill: #247a82; -fx-font-size: 13px; -fx-underline: false; -fx-font-weight: bold;");
        registratiLink.setOnAction(e -> {
            Stage signinStage = new Stage();
            new SigninView(primaryStage).start(signinStage);
            primaryStage.close();
        });

        // Composizione degli elementi nella card del form
        cardLayout.getChildren().addAll(titleLabel, userField, passField, bottoniLayout, registratiLink);
        rightPanel.getChildren().add(cardLayout);

        // --- LOGICA ED EVENTI ---
        loginBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Username e Password sono obbligatori!").showAndWait();
                return;
            }

            // Task asincrono per il login
            javafx.concurrent.Task<Utente> loginTask = new javafx.concurrent.Task<>() {
                @Override
                protected Utente call() throws Exception {
                    // Utilizzo del wrapper di connessione
                    return RemoteServiceConnector.esegui(s -> s.login(username, password));
                }
            };

            loginTask.setOnSucceeded(ev -> {
                Utente utenteLoggato = loginTask.getValue();
                if (utenteLoggato != null) {
                    try {
                        Stage prossimoStage = new Stage();
                        if (utenteLoggato.getRuolo() == Ruolo.Cliente) {
                            new CercaRistorantiClienteView(utenteLoggato, primaryStage).start(prossimoStage);
                            primaryStage.close();
                        } else if (utenteLoggato.getRuolo() == Ruolo.Gestore) {
                            new CercaRistorantiGestoreView(utenteLoggato, primaryStage).start(prossimoStage);
                            primaryStage.hide();
                        } else if (utenteLoggato.getRuolo() == Ruolo.Admin) {
                            new SceltaRuoloAdminView(utenteLoggato, primaryStage).start(prossimoStage);
                            primaryStage.hide();
                        }
                    } catch (Exception ec) {
                        ec.printStackTrace();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Credenziali non valide!").showAndWait();
                }
            });

            loginTask.setOnFailed(ev -> {
                loginTask.getException().printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Errore di connessione al server!").showAndWait();
            });

            new Thread(loginTask).start();
        });

        // --- RITORNO ALLA HOME SE L'UTENTE CLICCA SULLA "X" DELLA FINESTRA ---
        primaryStage.setOnCloseRequest(event -> {
            if (stagePrecedente != null) {
                stagePrecedente.show(); // Fa risvegliare la HomeView
            }
        });

        // --- LAYOUT GLOBALE CORRENTE (HBox) ---
        HBox mainRoot = new HBox();
        mainRoot.getChildren().addAll(leftPanel, rightPanel);

        // Ridimensionamento speculare ai 900x550 dell'applicazione complessiva
        Scene scene = new Scene(mainRoot, 900, 550);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}