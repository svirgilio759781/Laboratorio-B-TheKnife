/**
 * Stefano Virgilio 759781 VA
 * Schermata di selezione del ruolo per utenti amministratori (Grafica Modernizzata).
 */
package it.uninsubria.theknife.client.views;

import it.uninsubria.theknife.common.models.Utente;
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

public class SceltaRuoloAdminView extends Application {

    private final Utente utenteLoggato;
    private final Stage stagePrecedente; // Memorizziamo il LoginView

    public SceltaRuoloAdminView(Utente utenteLoggato, Stage stagePrecedente) {
        this.utenteLoggato = utenteLoggato;
        this.stagePrecedente = stagePrecedente;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Knife - Selezione Ruolo");

        // --- SFONDO IMMERSIVO ---
        String imageUrl = "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=1600";
        ImageView backgroundImageView = new ImageView(new Image(imageUrl, 900, 550, false, true));

        // --- CARD CENTRALE SEMITRASPARENTE ---
        VBox cardLayout = new VBox(20);
        cardLayout.setMaxWidth(420);
        cardLayout.setMaxHeight(480);
        cardLayout.setPadding(new Insets(40, 40, 40, 40));
        cardLayout.setAlignment(Pos.CENTER);

        // Stile CSS per la card speculare alla Home
        cardLayout.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.75);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 4);"
        );

        // --- TESTI ---
        Label titleLabel = new Label("Pannello Admin");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1e4d56; -fx-font-family: 'Segoe UI', Helvetica, Arial;");

        Label subtitleLabel = new Label("Seleziona l'interfaccia operativa:");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555; -fx-font-style: italic; -fx-padding: 0 0 10 0;");

        // --- BOTTONI DI NAVIGAZIONE STILIZZATI ---
        String baseButtonStyle =
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 11px 20px;" +
                        "-fx-cursor: hand;";

        // Pulsante Cliente (Teal Scuro/Ardesia)
        Button entraComeClienteBtn = new Button("🛒 Entra come Cliente");
        entraComeClienteBtn.setMinWidth(260);
        entraComeClienteBtn.setStyle("-fx-background-color: #1e4d56;" + baseButtonStyle);
        entraComeClienteBtn.setOnMouseEntered(e -> entraComeClienteBtn.setStyle("-fx-background-color: #286672;" + baseButtonStyle));
        entraComeClienteBtn.setOnMouseExited(e -> entraComeClienteBtn.setStyle("-fx-background-color: #1e4d56;" + baseButtonStyle));
        entraComeClienteBtn.setOnAction(e -> {
            try {
                new CercaRistorantiClienteView(utenteLoggato, primaryStage).start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Pulsante Gestore (Teal Medio)
        Button entraComeGestoreBtn = new Button("💼 Entra come Gestore (Admin)");
        entraComeGestoreBtn.setMinWidth(260);
        entraComeGestoreBtn.setStyle("-fx-background-color: #247a82;" + baseButtonStyle);
        entraComeGestoreBtn.setOnMouseEntered(e -> entraComeGestoreBtn.setStyle("-fx-background-color: #2e9ba5;" + baseButtonStyle));
        entraComeGestoreBtn.setOnMouseExited(e -> entraComeGestoreBtn.setStyle("-fx-background-color: #247a82;" + baseButtonStyle));
        entraComeGestoreBtn.setOnAction(e -> {
            try {
                Stage prossimoStage = new Stage();
                new CercaRistorantiGestoreView(utenteLoggato, primaryStage).start(prossimoStage);
                primaryStage.hide();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Pulsante Indietro / Logout (Grigio Fumo Elegante)
        Button indietroBtn = new Button("⬅ Indietro (Logout)");
        indietroBtn.setMinWidth(260);
        indietroBtn.setStyle("-fx-background-color: #5a6b6c;" + baseButtonStyle);
        indietroBtn.setOnMouseEntered(e -> indietroBtn.setStyle("-fx-background-color: #718688;" + baseButtonStyle));
        indietroBtn.setOnMouseExited(e -> indietroBtn.setStyle("-fx-background-color: #5a6b6c;" + baseButtonStyle));
        indietroBtn.setOnAction(e -> {
            primaryStage.close();
            if (stagePrecedente != null) {
                stagePrecedente.show();
            }
        });

        // --- RITORNO AL LOGIN SE L'UTENTE CLICCA SULLA "X" DELLA FINESTRA ---
        primaryStage.setOnCloseRequest(event -> {
            if (stagePrecedente != null) {
                stagePrecedente.show();
            }
        });

        // Composizione degli elementi nella card centrale
        cardLayout.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                entraComeClienteBtn,
                entraComeGestoreBtn,
                indietroBtn
        );

        // --- CONTENITORE ROOT (StackPane per stratificare sfondo e card) ---
        StackPane rootLayout = new StackPane();
        rootLayout.setStyle("-fx-background-color: #142124;"); // Sfondo fallback scuro
        rootLayout.getChildren().addAll(backgroundImageView, cardLayout);

        // Risoluzione desktop uniforme 900x550
        Scene scene = new Scene(rootLayout, 900, 550);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}