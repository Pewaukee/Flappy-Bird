package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Group group;
    private static Group archGroup;
    private static GridPane mainPane; // for the centered objects
    private static Pane altPane; // for non-centered objects
    private static boolean started = false;
    private static Button startButton;

    @Override
    public void start(Stage stage) throws IOException {
        group = new Group();
        archGroup = new Group();
        altPane = new Pane();

        startButton = new Button("Start Game!");

        mainPane = new GridPane();
        mainPane.setAlignment(Pos.CENTER);
        // Center the start button
        GridPane.setHalignment(startButton, HPos.CENTER);
        GridPane.setValignment(startButton, VPos.CENTER);

        mainPane.getChildren().addAll(group, startButton);
        mainPane.getChildren().add(altPane);

        scene = new Scene(mainPane, 1280, 720);
        stage.setScene(scene);
        stage.setTitle("Flappy Bird");
        stage.setResizable(false);
        stage.show();

        
        setBackground();
        startGame();
    }

    private void startGame() {

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!started) {
                    startMoving();
                }
            }
        });
    }

    private void startMoving() {
        /* purpose
         * start the game when the user first clicks the mouse
         * this will initialize the creation and movement of rectangles
         * using a createArch method to define where the rectangles can be
         * placed with respectable space in between them for the bird to pass through
         */
    }

    private void setBackground() throws FileNotFoundException {
        /* purpose
         * set the background image
         * to flappybirdbackground.png
         * using relative path
         */
        // taken from: https://www.tutorialspoint.com/how-to-display-an-image-in-javafx#:~:text=Create%20a%20FileInputStream%20representing%20the,to%20the%20setImage()%20method.
        try {
            InputStream inputStream = new FileInputStream("demo\\src\\main\\resources\\com\\example\\flappybirdbackground.png");
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setFitHeight(720);
            imageView.setFitWidth(1280);
            imageView.setPreserveRatio(true);
            group.getChildren().add(imageView);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}