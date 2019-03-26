import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
/**
* The SnakeMain class uses javafx to recreate a simple snake game.
* Code referenced from:
* Title: JavaFX Game: Snake Classic
* Author: Almas B.
* @author
* T08-GR04
*/

public class SnakeMain extends Application {

    /**A direction can be up, down, left, or right.*/
    public enum Direction {
      UP, DOWN, LEFT, RIGHT
    }

    public static final int RECT_SIZE = 30;
    public static final int WINDOW_W = 600;
    public static final int WINDOW_H = 450;

    /**The default direction is right.*/
    private Direction direction = Direction.RIGHT;

    private boolean moved = false;
    private boolean running = false;

    private Timeline timeline = new Timeline();

    /** list to keep track of the snake location and movements. */
    private ObservableList<Node> snake;

    private Parent createContent() {
      Pane root = new Pane();
      root.setPrefSize(WINDOW_W, WINDOW_H);

      Group snakeB = new Group();

      /** Assign children from snakeB Group to the snake list. */
      snake = snakeB.getChildren();

      /** Food graphics. */
      Rectangle food = new Rectangle(RECT_SIZE, RECT_SIZE);
      food.setFill(Color.GREEN);

      /** Locate food at a random point on the screen. */
      food.setTranslateX((int)(Math.random() * (WINDOW_W - RECT_SIZE)) / RECT_SIZE * RECT_SIZE);
      food.setTranslateY((int)(Math.random() * (WINDOW_H - RECT_SIZE)) / RECT_SIZE * RECT_SIZE);

      /** Lower the duration value to increase the difficulty of the game; snake will move faster. */
      KeyFrame snakeframe = new KeyFrame(Duration.seconds(0.3), event -> {
        if(!running)
          return;

        /** Check if food has been added. */
        boolean toRemove = snake.size() > 1;

        /** If condition toRemove is true, remove the last element(tail) from snake list. This will make the tail become the head. Else, everything remains the same. */
        Node tail = toRemove ? snake.remove(snake.size() - 1) : snake.get(0);

        double xTail = tail.getTranslateX();
        double yTail = tail.getTranslateY();

        /** There are four cases (one for each direction). */
        switch(direction) {
          case UP:
            tail.setTranslateX(snake.get(0).getTranslateX());
            tail.setTranslateY(snake.get(0).getTranslateY() - RECT_SIZE);
            break;
          case DOWN:
            tail.setTranslateX(snake.get(0).getTranslateX());
            tail.setTranslateY(snake.get(0).getTranslateY() + RECT_SIZE);
            break;
          case LEFT:
            tail.setTranslateX(snake.get(0).getTranslateX() - RECT_SIZE);
            tail.setTranslateY(snake.get(0).getTranslateY());
            break;
          case RIGHT:
            tail.setTranslateX(snake.get(0).getTranslateX() + RECT_SIZE);
            tail.setTranslateY(snake.get(0).getTranslateY());
            break;
        }

        moved = true;

        /** Since we removed one block, add one block back. */
        if(toRemove)
          snake.add(0, tail);

        /** Collision detection - its own body. REMEMBER: TAIL HERE MEANS HEAD. Restart game if condition is true.*/
        for (Node rect : snake) {
          if(rect != tail && tail.getTranslateX() == rect.getTranslateX() && tail.getTranslateY() == rect.getTranslateY()) {
            restartGame();
            break;
          }
        }

        /** Collision detection - walls. Restart game condition is true. */
        if(tail.getTranslateX() < 0 || tail.getTranslateX() >= WINDOW_W || tail.getTranslateY() < 0 || tail.getTranslateY() >= WINDOW_H) {
          restartGame();
        }

        /** Check if the snake hits food. ("tail" (head) coordinates = food coordinates) */
        if(tail.getTranslateX() == food.getTranslateX() && tail.getTranslateY() == food.getTranslateY()){

          /** Add new food. */
          food.setTranslateX((int)(Math.random() * (WINDOW_W - RECT_SIZE)) / RECT_SIZE * RECT_SIZE);
          food.setTranslateY((int)(Math.random() * (WINDOW_H - RECT_SIZE)) / RECT_SIZE * RECT_SIZE);
          Rectangle rect = new Rectangle(RECT_SIZE, RECT_SIZE);

          /** Add food to snake. */
          rect.setTranslateX(xTail);
          rect.setTranslateY(yTail);
          snake.add(rect);
        }
      });

      timeline.getKeyFrames().add(snakeframe);
      timeline.setCycleCount(Timeline.INDEFINITE);

      root.getChildren().addAll(food, snakeB);
      return root;
    }

    /** Private method to retart the game. */
    private void restartGame() {
      stopGame();
      startGame();
    }

    /** Private method to stop the game. */
    private void stopGame() {
      running = false;
      timeline.stop();
      snake.clear();
    }

    /** Private method to start the game. */
    private void startGame() {
      /** Default direction; snake will start running right. */
      direction = Direction.RIGHT;
      Rectangle head = new Rectangle(RECT_SIZE, RECT_SIZE);
      snake.add(head);
      timeline.play();
      running = true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
      Scene scene = new Scene(createContent());
      scene.setOnKeyPressed(event -> {
        if(!moved)
          return;

        switch (event.getCode()) {
          case S:
            if (direction != Direction.DOWN)
              direction = Direction.UP;
              break;
          case X:
            if (direction != Direction.UP)
                direction = Direction.DOWN;
              break;
           case Z:
            if (direction != Direction.RIGHT)
                  direction = Direction.LEFT;
                break;
            case C:
              if (direction != Direction.LEFT)
                  direction = Direction.RIGHT;
                break;
        }
        moved = false;
      });

      primaryStage.setTitle("Snake Game");
      primaryStage.setScene(scene);
      primaryStage.show();
      startGame();
    }

    public static void main(String[] args){
      launch(args);
    }
}
