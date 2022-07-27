package com.example;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.geometry.Bounds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Random;
import java.util.function.DoubleBinaryOperator;

/**
 * JavaFX App
 */
public class App extends Application implements PhysicsInterface{

    private static Scene scene;
    private static Group group;
    private static Group archGroup;
    private static Group birdGroup;
    private static Group scoreGroup;
    private static GridPane mainPane; // for the centered objects
    private static Pane altPane; // for non-centered objects
    private static Label score; // for keeping score
    private static boolean started = false;
    private static Button startButton;
    private static Timeline timeline;
    private static final double MOVEMENT_SPEED = -1.25; // movement speed for rectangles
    private static final int GAP = 200; // for distance between the arches
    private static final int DISTANCE_BEWTEEN_ARCHES = 400; // for creating a new arch
    private static final int DISTANCE_UNTIL_BOTTOM = 50; // account for the ground
    private static final Random random = new Random();
    private static Label bird; // this way can set image to label and move it
    private static double velocity; // keep track of the velocity of the bird
    private static double curTime; // keep track of time before screen clicked
    private static final double START_VELOCITY = 1;
    private static final int WIDTH = 100;
    private static final int BIRD_X_POSITION = 200; // set initial coords
    private static final int BIRD_Y_POSITION = 300; // of the bird
    private static final int SIZE_OF_BIRD = 50;
    private static final int TIME_INTERVAL = 5; // in milliseconds

    @Override
    public void start(Stage stage) throws IOException {
        group = new Group();
        archGroup = new Group();
        birdGroup = new Group();
        scoreGroup = new Group();
        altPane = new Pane(archGroup, birdGroup, scoreGroup);

        startButton = new Button("Start Game!");
        startButton.setTranslateX(600);
        startButton.setTranslateY(300);
        altPane.getChildren().add(startButton);


        mainPane = new GridPane();
        mainPane.setAlignment(Pos.CENTER);
        // Center the start button
        GridPane.setHalignment(startButton, HPos.CENTER);
        GridPane.setValignment(startButton, VPos.CENTER);
        setBackground();

        mainPane.getChildren().addAll(group);
        mainPane.getChildren().add(altPane);

        
        
        score = new Label("Score: 0");
        score.setTranslateX(525);
        score.setTranslateY(30);
        //score.setFont(Font.loadFont("file:resources/fonts/FFFFORWA.TTF", 16));
        score.setFont(Font.font("Verdana", 50));
        scoreGroup.getChildren().add(score);

        scene = new Scene(mainPane, 1280, 720);
        stage.setScene(scene);
        stage.setTitle("Flappy Bird");
        stage.setResizable(false);
        stage.show();

        
        startGame();
    }

    private void startGame() {

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                startButton.setOpacity(0.0); // make button invisible
                startButton.setDisable(true);
                started = true; // if started is true, then the arches can start moving
                addArch(); // add the first set of arches
                initBird(); // add the picture of the bird
            }
        });
        // define the time cycle
        timeline = new Timeline(
            new KeyFrame(Duration.millis(TIME_INTERVAL), 
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (started) {
                        updateArches();
                        curTime += 0.005;
                        updateBirdPosition();
                    }
                }
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // this and the time cycle can occur at the 'same time'
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (started) {
                    // the tiles should be already moving, so we need to update the bird movement only
                    velocity = START_VELOCITY;
                    curTime = 0;
                }
            }
        });
    }

    private void updateScore() {
        /*purpose
         * update the score by 1 by
         * using a substring and finding 
         * Integer.valueOf
         */
        int curScore = Integer.valueOf(score.getText().substring(7));
        score.setText(String.format("Score: %d", ++curScore));
    }

    private void updateBirdPosition() {
        checkLoseConditions();
        double curVelocity = finalVelocity(velocity, curTime);
        velocity = curVelocity;
        setRotate(curVelocity); // if object is rising up, rotate back, else rotate forward
        bird.setTranslateY(bird.getTranslateY() - curVelocity);
    }

    private void checkLoseConditions() {
        /*purpose
         * checking if either the bird is 
         * in the sky, hit the ground, or 
         * crashed into one of the pillars
         */
        // check sky/ground
        Node node = birdGroup.getChildren().get(0);
        double yPosition = node.getTranslateY();
        if (yPosition <= 0 || yPosition >= 720-DISTANCE_UNTIL_BOTTOM-SIZE_OF_BIRD) {
            // to get the top border line of the bird
            gameOver();
        }
        // check all pillars that can collide with bird
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        int i = 0;
        while (i < archGroup.getChildren().size()) {
            Node arch = archGroup.getChildren().get(i);
            // arch.getTranslateY() will only check top arch, which is all that's needed
            Bounds rectBounds = arch.localToScene(arch.getBoundsInLocal());
            double midpoint = (rectBounds.getMinX() + rectBounds.getMaxX())/2;
            if (rectBounds.getMinY() <= 0 &&
                midpoint >= 200+MOVEMENT_SPEED && 
                midpoint < 200) {
                updateScore();
            }
            if (rectBounds.getMaxX() >= BIRD_X_POSITION) {
                
                if (bounds.intersects(rectBounds)) {System.out.println(String.format("%f", rectBounds.getMaxX()));gameOver();}
            }
            if (rectBounds.getMaxX() == 0) {
                archGroup.getChildren().remove(arch); // use while loop to delete mid loop
            }
            i++;
        }
    }

    private void gameOver() {
        started = false; // paause all movement
        timeline.stop(); // pause unneccessary code running
        System.out.println("GAME OVER");

    }

    private void setRotate(double curVelocity) {
        /*purpose
         * set the rotation of the bird label
         * for 4 cases
         * 1. high upward velocity (speed>0.5) remember neg is up
         * 2. low speed (-0.5<=speed<=-0.5)
         * 3. high downward velocity (speed<-0.5)
         * 4. divebomb (speed<-1)
         */
        if (curVelocity < -1) {bird.setRotate(70);}
        else if (curVelocity < -0.5) {bird.setRotate(35);}
        else if (curVelocity > 0.5) {bird.setRotate(325);}
        else {bird.setRotate(0);}
    }

    private void initBird() {
        // idea from: https://www.tutorialspoint.com/how-to-add-an-image-as-label-using-javafx
        bird = new Label();
        // starting position, also x never changes
        bird.setTranslateX(BIRD_X_POSITION);
        bird.setTranslateY(BIRD_Y_POSITION);

        Image image = new Image(getClass().getResourceAsStream("bird.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(SIZE_OF_BIRD);
        imageView.setPreserveRatio(true);
        bird.setGraphic(imageView);

        birdGroup.getChildren().add(bird);

        velocity = START_VELOCITY;
        curTime = 0;
    }

    private void updateArches() {
        
        for (int i = 0; i < archGroup.getChildren().size(); i++) {
            // the rectangles only need to move left, so only x should be updated
            double x = archGroup.getChildren().get(i).getTranslateX();
            // move node to the left
            archGroup.getChildren().get(i).setTranslateX(x + MOVEMENT_SPEED);
        }
        /* now check and see if the 
         * last added arch is a certain
         * distance away from the right edge
         * if so, add a new arch
         */
        // idea from: https://stackoverflow.com/questions/31148690/get-real-position-of-a-node-in-javafx
        Node node = archGroup.getChildren().get(archGroup.getChildren().size()-1); // get last added node object
        Bounds bounds = node.localToScene(node.getBoundsInLocal()); // declare a new bounds object to get correct location of node

        if (1280 - bounds.getMinX() >= DISTANCE_BEWTEEN_ARCHES) {
            addArch();
        }
    }

    private void addArch() {
        /*purpose
         * define a arch of two rectangles
         * and the arch should always
         * start from the same place, 
         * off the screen to the right
         * to be moved in afterward
         */
        int topArchHeight = random.nextInt(720-GAP-DISTANCE_UNTIL_BOTTOM) + 1;
        int bottomArchY = topArchHeight + GAP;
        drawRectangle(1280, 0, WIDTH, topArchHeight); // top arch
        drawRectangle(1280, bottomArchY, WIDTH, 720-bottomArchY-DISTANCE_UNTIL_BOTTOM); // bottom arch
    }

    @Override
    public double finalVelocity(double velocity, double time) {
        return velocity + acceleration * time;
    }

    //@Override
    //public double finalPosition(double position, double velocity, double time) {
        //return position + velocity * time + 0.5 * acceleration * Math.pow(time, 2);
    //}

    private void drawRectangle(int x, int y, int width, int height) {
        /*purpose
         * draw a rectangle given the arguments, 
         * set a black stroke and a semi-light green
         * color to the rectangle
         */
        Rectangle r = new Rectangle();
        r.setX(x);
        r.setY(y);
        r.setHeight(height);
        r.setWidth(width);
        r.setFill(Color.rgb(72, 201, 5)); // semi light green
        //TODO stroke messing with things?
        //r.setStroke(Color.BLACK);
        //r.setStrokeWidth(5.0);
        archGroup.getChildren().add(r);
    }

    private void setBackground() throws FileNotFoundException {
        /* purpose
         * set the background image
         * to background.png
         * using relative path
         */
        // taken from: https://www.tutorialspoint.com/how-to-display-an-image-in-javafx#:~:text=Create%20a%20FileInputStream%20representing%20the,to%20the%20setImage()%20method.
        try {
            InputStream inputStream = new FileInputStream("demo/src/main/resources/com/example/background.png");
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setFitHeight(720);
            imageView.setFitWidth(1280);
            imageView.setPreserveRatio(false);
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