/**
 * Stefano Virgilio 759781 VA
 * Schermata principale di benvenuto per l'indirizzamento degli utenti (Grafica Modernizzata).
 */
package it.uninsubria.theknife.client.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Home");

        // --- SFONDO  ---
        String imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1000";
        // Creiamo l'immagine con backgroundLoading = true per non bloccare la UI
        Image image = new Image(imageUrl, 900, 550, false, true, true);
        ImageView backgroundImageView = new ImageView(image);

        // Gestione dell'errore
        image.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) {
                System.err.println("Errore caricamento immagine: " + image.getException().getMessage());

                // Opzione A: Carica un'immagine di default presente nel progetto
                backgroundImageView.setImage(new Image(getClass().getResourceAsStream("/assets/fallback-ristorante.jpg")));

                // Opzione B: Rimuovi l'immagine o imposta uno sfondo colorato
                // backgroundImageView.setVisible(false);
            }
        });

        // --- CARD CENTRALE SEMITRASPARENTE (Effetto Vetro / Glassmorphism) ---
        VBox cardLayout = new VBox(20);
        cardLayout.setMaxWidth(420);
        cardLayout.setMaxHeight(480);
        cardLayout.setPadding(new Insets(35, 40, 35, 40));
        cardLayout.setAlignment(Pos.CENTER);

        // Stile CSS per la card: sfondo bianco molto trasparente, angoli arrotondati e ombra delicata
        cardLayout.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.75);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 4);"
        );

        // --- TESTI  ---
        Label titleLabel = new Label("The Knife");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #1e4d56; -fx-font-family: 'Segoe UI', Helvetica, Arial;");

        Label subtitleLabel = new Label("Find and manage the best restaurants");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555; -fx-font-style: italic; -fx-padding: 0 0 15 0;");

        // --- BOTTONI DI NAVIGAZIONE STILIZZATI (Card interne con stile Teal) ---

        // Stile base comune per i bottoni: angoli arrotondati, testo chiaro, cursore a manina
        String baseButtonStyle =
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-cursor: hand;";

        // Bottone Login
        Button loginBtn = new Button("🔐 Accedi");
        loginBtn.setMinWidth(260);
        loginBtn.setStyle("-fx-background-color: #1e4d56;" + baseButtonStyle);
        // Effetto visivo al passaggio del mouse (Hover)
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #286672;" + baseButtonStyle));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #1e4d56;" + baseButtonStyle));
        loginBtn.setOnAction(e -> {
            Stage loginStage = new Stage();
            new LoginView(primaryStage).start(loginStage);
            primaryStage.hide();
        });

        // Bottone Signin
        Button signinBtn = new Button("📝 Registrati");
        signinBtn.setMinWidth(260);
        signinBtn.setStyle("-fx-background-color: #247a82;" + baseButtonStyle);
        signinBtn.setOnMouseEntered(e -> signinBtn.setStyle("-fx-background-color: #2e9ba5;" + baseButtonStyle));
        signinBtn.setOnMouseExited(e -> signinBtn.setStyle("-fx-background-color: #247a82;" + baseButtonStyle));
        signinBtn.setOnAction(e -> {
            Stage signinStage = new Stage();
            new SigninView(primaryStage).start(signinStage);
            primaryStage.hide();
        });

        // Bottone Ospite Anonimo
        Button anonimoBtn = new Button("👀 Entra come Ospite");
        anonimoBtn.setMinWidth(260);
        anonimoBtn.setStyle("-fx-background-color: #5a6b6c;" + baseButtonStyle);
        anonimoBtn.setOnMouseEntered(e -> anonimoBtn.setStyle("-fx-background-color: #718688;" + baseButtonStyle));
        anonimoBtn.setOnMouseExited(e -> anonimoBtn.setStyle("-fx-background-color: #5a6b6c;" + baseButtonStyle));
        anonimoBtn.setOnAction(e -> {
            Stage anonimoStage = new Stage();
            new CercaRistorantiAnonimoView(primaryStage).start(anonimoStage);
            primaryStage.hide();
        });

        // Aggiungiamo i componenti alla card centrale
        cardLayout.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                loginBtn,
                signinBtn,
                anonimoBtn
        );

        // --- CONTENITORE ROOT (StackPane per stratificare sfondo e card) ---
        StackPane rootLayout = new StackPane();
        rootLayout.setStyle("-fx-background-color: #142124;"); // Sfondo di fallback scuro ardesia
        rootLayout.getChildren().addAll(backgroundImageView, cardLayout);

        // Dimensione della finestra più ampia e proporzionata (900x550 come un'applicazione desktop moderna)
        Scene scene = new Scene(rootLayout, 900, 550);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}