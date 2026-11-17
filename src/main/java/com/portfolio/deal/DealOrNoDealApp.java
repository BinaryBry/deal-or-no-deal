
package com.portfolio.deal;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.*;

public class DealOrNoDealApp extends Application {

    private static final long[] PRIZES = {
            1, 5, 10, 25, 50, 75, 100, 200, 300, 400,
            500, 750, 1000, 5000, 10000, 25000, 50000,
            75000, 100000, 200000, 300000, 400000,
            500000, 750000, 1000000, 2000000
    };

    private static final int[] ROUND_COUNTS = {
            6, 5, 4, 3, 2, 1, 1, 1, 1
    };

    private final Map<Integer, Long> values = new HashMap<>();
    private final Set<Integer> opened = new HashSet<>();
    private final Map<Integer, StackPane> buttons = new HashMap<>();

    private final NumberFormat money =
            NumberFormat.getCurrencyInstance(Locale.US);

    private final Random random = new Random();

    private GridPane cases;
    private VBox leftBoard, rightBoard;
    private Label status, roundLabel, offerLabel, ownedLabel;
    private HBox offerActions;
    private HBox ownedDisplay;
    private Button deal, noDeal, restart;

    private int owned = -1;
    private int round = 0;
    private int openedThisRound = 0;

    private boolean offerPending = false;
    private boolean finished = false;

    private long currentOffer;

    private final String GOLD = "#ffd700";
    private final String BLACK = "#080808";

    @Override
    public void start(Stage stage) {

        Label heading = new Label("DEAL OR NO DEAL");
        heading.setFont(Font.font("Arial", FontWeight.BLACK, 37));
        heading.setTextFill(Color.web(GOLD));
        heading.setStyle(
                "-fx-effect: dropshadow(gaussian, #000000, 4, 1, 3, 3);"
        );

        StackPane titleBox = new StackPane(heading);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(8, 20, 8, 20));
        titleBox.setStyle(
                "-fx-background-color: #111111;" +
                        "-fx-border-color: #ffd700;" +
                        "-fx-border-width: 3;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;"
        );

        HBox header = new HBox(titleBox);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(12, 0, 8, 0));

        cases = new GridPane();
        cases.setHgap(9);
        cases.setVgap(15);
        cases.setAlignment(Pos.CENTER);

        leftBoard = createPrizePanel();
        rightBoard = createPrizePanel();

        status = label("Pick the briefcase you want to keep!");
        status.setWrapText(true);
        status.setAlignment(Pos.CENTER);
        status.setMaxWidth(650);

        roundLabel = label("");
        ownedLabel = label("");
        offerLabel = label("");

        roundLabel.setTextFill(Color.web(GOLD));
        offerLabel.setTextFill(Color.web(GOLD));

        deal = action(
                "DEAL",
                "#218c48",
                () -> finish(
                        "You accepted the banker's offer of " +
                                money.format(currentOffer) +
                                "! Your case held " +
                                money.format(values.get(owned)) + "."
                )
        );

        noDeal = action(
                "NO DEAL",
                "#b5222e",
                () -> {
                    offerPending = false;
                    offerActions.setVisible(false);
                    offerActions.setManaged(false);
                    round++;
                    openedThisRound = 0;
                    updateStatus();
                }
        );

        offerActions = new HBox(15, deal, noDeal);
        offerActions.setAlignment(Pos.CENTER);
        offerActions.setVisible(false);
        offerActions.setManaged(false);

        restart = action(
                "NEW GAME",
                "#b58b00",
                this::newGame
        );

        ownedDisplay = new HBox(10);
        ownedDisplay.setAlignment(Pos.CENTER_LEFT);

        VBox gameArea = new VBox(
                14,
                roundLabel,
                cases,
                offerLabel,
                offerActions,
                restart
        );

        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(10));

        HBox mainArea = new HBox(
                15,
                leftBoard,
                gameArea,
                rightBoard
        );

        mainArea.setAlignment(Pos.CENTER);
        HBox.setHgrow(gameArea, Priority.ALWAYS);

        HBox bottomBar = new HBox(18);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(12));
        bottomBar.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #ffd700;" +
                        "-fx-border-width: 4;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        VBox messageArea = new VBox(7, ownedLabel, status);
        messageArea.setAlignment(Pos.CENTER);
        HBox.setHgrow(messageArea, Priority.ALWAYS);

        bottomBar.getChildren().addAll(
                ownedDisplay,
                messageArea
        );

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(mainArea);
        layout.setBottom(bottomBar);

        layout.setPadding(new Insets(12));
        layout.setStyle(
                "-fx-background-color: " +
                        "radial-gradient(center 50% 40%, radius 90%, " +
                        "#493700, #171100 65%, #050505 100%);"
        );

        Scene scene = new Scene(layout, 1180, 780);

        stage.setTitle("Deal or No Deal | JavaFX Edition");
        stage.setScene(scene);
        stage.setMinWidth(1050);
        stage.setMinHeight(700);
        stage.show();

        newGame();
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(
                "Arial", FontWeight.BOLD, 15
        ));
        l.setTextFill(Color.WHITE);
        return l;
    }

    private Button action(
            String text,
            String color,
            Runnable task
    ) {
        Button b = new Button(text);

        b.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #ffd700;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-radius: 6;" +
                        "-fx-padding: 10 25;"
        );

        b.setOnAction(e -> task.run());
        return b;
    }

    private VBox createPrizePanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(180);
        panel.setMinWidth(180);

        panel.setStyle(
                "-fx-background-color: #050505;" +
                        "-fx-border-color: #ffd700;" +
                        "-fx-border-width: 5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        return panel;
    }

    // Creates a silver metallic briefcase using JavaFX shapes.
    private StackPane caseButton(int number) {

        StackPane container = new StackPane();
        container.setPrefSize(88, 73);
        container.setMinSize(88, 73);
        container.setMaxSize(88, 73);

        Rectangle handle = new Rectangle(30, 13);
        handle.setArcWidth(7);
        handle.setArcHeight(7);
        handle.setFill(Color.web("#202020"));
        handle.setStroke(Color.web("#d8d8d8"));
        handle.setStrokeWidth(3);
        handle.setTranslateY(-29);

        Rectangle body = new Rectangle(84, 53);
        body.setArcWidth(9);
        body.setArcHeight(9);

        body.setFill(new LinearGradient(
                0, 0, 0, 1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.00, Color.web("#f8f8f8")),
                new Stop(0.18, Color.web("#aaaaaa")),
                new Stop(0.35, Color.web("#eeeeee")),
                new Stop(0.60, Color.web("#777777")),
                new Stop(0.80, Color.web("#dedede")),
                new Stop(1.00, Color.web("#666666"))
        ));

        body.setStroke(Color.web("#333333"));
        body.setStrokeWidth(2);
        body.setTranslateY(5);

        Rectangle topLine = new Rectangle(78, 2);
        topLine.setFill(Color.web("#555555"));
        topLine.setTranslateY(-5);

        Rectangle bottomLine = new Rectangle(78, 2);
        bottomLine.setFill(Color.web("#eeeeee"));
        bottomLine.setTranslateY(23);

        Label numberLabel = new Label(
                String.valueOf(number)
        );

        numberLabel.setFont(Font.font(
                "Arial",
                FontWeight.BLACK,
                34
        ));

        numberLabel.setTextFill(Color.BLACK);
        numberLabel.setTranslateY(5);

        numberLabel.setStyle(
                "-fx-effect: dropshadow(gaussian, white, 1, 1, 1, 1);"
        );

        container.getChildren().addAll(
                handle,
                body,
                topLine,
                bottomLine,
                numberLabel
        );

        container.setStyle(
                "-fx-cursor: hand;"
        );

        return container;
    }

    private void newGame() {

        List<Long> shuffled = new ArrayList<>();

        for (long p : PRIZES) {
            shuffled.add(p);
        }

        Collections.shuffle(shuffled);

        values.clear();
        opened.clear();
        buttons.clear();

        owned = -1;
        round = 0;
        openedThisRound = 0;
        offerPending = false;
        finished = false;

        for (int i = 1; i <= 26; i++) {
            values.put(i, shuffled.get(i - 1));
        }

        cases.getChildren().clear();

        for (int i = 1; i <= 26; i++) {

            final int id = i;

            StackPane briefcase = caseButton(i);

            buttons.put(i, briefcase);

            cases.add(
                    briefcase,
                    (i - 1) % 6,
                    (i - 1) / 6
            );

            briefcase.setOnMouseClicked(
                    e -> selectCase(id)
            );
        }

        offerActions.setVisible(false);
        offerActions.setManaged(false);
        offerLabel.setText("");

        updateOwnedDisplay();
        updateStatus();
        updateBoard();
    }

    private void selectCase(int id) {

        if (
                finished ||
                        offerPending ||
                        opened.contains(id) ||
                        id == owned
        ) {
            return;
        }

        if (owned == -1) {

            owned = id;

            StackPane selected = buttons.get(id);
            selected.setVisible(false);

            updateOwnedDisplay();
            updateStatus();
            return;
        }

        opened.add(id);
        openedThisRound++;

        StackPane briefcase = buttons.get(id);

        // Animate the case before removing it.
        ScaleTransition pop = new ScaleTransition(
                Duration.millis(180),
                briefcase
        );

        pop.setFromX(1);
        pop.setFromY(1);
        pop.setToX(1.12);
        pop.setToY(1.12);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);

        pop.setOnFinished(e -> {
            briefcase.setVisible(false);
        });

        pop.play();

        status.setText(
                "Case " + id + " contained " +
                        money.format(values.get(id)) + "!"
        );

        updateBoard();

        if (opened.size() == 24) {
            finalChoice();
            return;
        }

        if (
                round < ROUND_COUNTS.length &&
                        openedThisRound >= ROUND_COUNTS[round]
        ) {
            showOffer();
        } else {
            updateStatusAfterOpen();
        }
    }

    private void updateOwnedDisplay() {

        ownedDisplay.getChildren().clear();

        if (owned == -1) {
            Label placeholder = label("YOUR BRIEFCASE");
            placeholder.setTextFill(Color.web(GOLD));
            ownedDisplay.getChildren().add(placeholder);
            return;
        }

        StackPane personalCase = caseButton(owned);

        Label text = label("YOUR\nBRIEFCASE");
        text.setFont(Font.font(
                "Arial",
                FontWeight.BLACK,
                17
        ));

        HBox display = new HBox(
                8,
                personalCase,
                text
        );

        display.setAlignment(Pos.CENTER_LEFT);
        ownedDisplay.getChildren().add(display);
    }

    private void updateStatusAfterOpen() {

        roundLabel.setText(
                "ROUND " + (round + 1) +
                        "  •  " +
                        (ROUND_COUNTS[round] - openedThisRound) +
                        " cases until the banker calls"
        );

        ownedLabel.setText(
                "YOUR CASE: #" + owned
        );
    }

    private void updateStatus() {

        ownedLabel.setText(
                owned == -1
                        ? "YOUR CASE: NOT SELECTED"
                        : "YOUR CASE: #" + owned
        );

        if (finished) {
            return;
        }

        if (owned == -1) {

            roundLabel.setText("CHOOSE YOUR CASE");

            status.setText(
                    "Select one briefcase to keep until the end."
            );

        } else if (opened.size() == 24) {

            roundLabel.setText("FINAL TWO CASES");

        } else {

            roundLabel.setText(
                    "ROUND " + (round + 1) +
                            "  •  Open " +
                            (ROUND_COUNTS[round] - openedThisRound) +
                            " more cases"
            );

            status.setText(
                    "Select a silver briefcase to reveal its value."
            );
        }
    }

    private void showOffer() {

        List<Long> remaining = new ArrayList<>();

        for (int i = 1; i <= 26; i++) {
            if (!opened.contains(i)) {
                remaining.add(values.get(i));
            }
        }

        double average = remaining.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        double fraction = Math.min(
                .95,
                .25 + round * .08
        );

        currentOffer = Math.max(
                1,
                Math.round(
                        average * fraction *
                                (.92 + random.nextDouble() * .16)
                )
        );

        offerPending = true;

        offerLabel.setText(
                "BANKER'S OFFER: " +
                        money.format(currentOffer)
        );

        status.setText(
                "The banker is calling. Take the money or keep playing?"
        );

        roundLabel.setText(
                "ROUND " + (round + 1) + " COMPLETE"
        );

        offerActions.setVisible(true);
        offerActions.setManaged(true);
    }

    private void finalChoice() {

        int other = -1;

        for (int i = 1; i <= 26; i++) {
            if (
                    i != owned &&
                            !opened.contains(i)
            ) {
                other = i;
            }
        }

        final int swapId = other;

        Alert a = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        a.setTitle("Final Decision");
        a.setHeaderText(
                "Keep your case or swap?"
        );

        a.setContentText(
                "Your case is #" + owned +
                        ". The other remaining case is #" + other + "."
        );

        ButtonType keep = new ButtonType("KEEP");
        ButtonType swap = new ButtonType("SWAP");

        a.getButtonTypes().setAll(keep, swap);

        Optional<ButtonType> result = a.showAndWait();

        // Closing the dialog without choosing counts as keeping your case,
        // so the game can't get stuck with one unopened case left.
        int chosen = result.isPresent() && result.get() == swap
                ? swapId
                : owned;

        finish(
                "You " +
                        (chosen == owned ? "kept" : "swapped to") +
                        " case #" + chosen +
                        " and won " +
                        money.format(values.get(chosen)) +
                        "! Your original case held " +
                        money.format(values.get(owned)) + "."
        );
    }

    private void finish(String message) {

        finished = true;
        offerPending = false;

        offerActions.setVisible(false);
        offerActions.setManaged(false);

        roundLabel.setText("GAME OVER");
        status.setText(message);
        offerLabel.setText("Thanks for playing!");

        for (StackPane b : buttons.values()) {
            b.setDisable(true);
        }
    }

    private void updateBoard() {

        leftBoard.getChildren().clear();
        rightBoard.getChildren().clear();

        Set<Long> removed = new HashSet<>();

        for (int id : opened) {
            removed.add(values.get(id));
        }

        for (int i = 0; i < PRIZES.length; i++) {

            long prize = PRIZES[i];

            Label amount = new Label(
                    money.format(prize)
            );

            amount.setFont(Font.font(
                    "Arial",
                    FontWeight.BLACK,
                    16
            ));

            amount.setAlignment(Pos.CENTER_RIGHT);
            amount.setMaxWidth(Double.MAX_VALUE);
            amount.setPrefHeight(32);
            amount.setPadding(
                    new Insets(3, 8, 3, 8)
            );

            if (removed.contains(prize)) {

                // JavaFX Labels ignore -fx-strikethrough, so the crossed-out
                // amount is drawn as a Text node inside the label instead.
                Text struck = new Text(money.format(prize));
                struck.setFont(Font.font("Arial", FontWeight.BLACK, 16));
                struck.setFill(Color.web("#222222"));
                struck.setStrikethrough(true);

                amount.setGraphic(struck);
                amount.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                amount.setStyle(
                        "-fx-background-color: #51471b;" +
                                "-fx-background-radius: 3;"
                );

            } else {

                amount.setStyle(
                        "-fx-background-color: " +
                                "linear-gradient(to bottom, " +
                                "#fff8b0, #ffd700 55%, #a87900);" +
                                "-fx-text-fill: black;" +
                                "-fx-background-radius: 3;"
                );
            }

            if (i < 13) {
                leftBoard.getChildren().add(amount);
            } else {
                rightBoard.getChildren().add(amount);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
