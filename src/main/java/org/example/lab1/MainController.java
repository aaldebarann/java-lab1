package org.example.lab1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class MainController {

    @FXML
    Circle creep, minicreep, arrow;
    @FXML
    Polygon player;
    @FXML
    AnchorPane mainPane;
    @FXML
    private Label score, shots;

    private int shotsNum, scoreNum;
    private boolean gameIsStarted = false;
    private boolean gameIsPaused = false;
    private boolean shooting = false;
    Thread t = null;
    private final int creepSpeed = 1, minicreepSpeed = -2, arrowSpeed = 10;

    void next() {
        nextCreep();
        nextMinicreep();
        if(shooting) {
            nextArrow();
        }
        score.setText(String.valueOf(scoreNum));
        shots.setText(String.valueOf(shotsNum));
    }
    void nextCreep() {
        double height = mainPane.getHeight();
        double y = creep.getLayoutY();
        y += creepSpeed;
        if(y > height - creep.getRadius()) y = 0;
        creep.setLayoutY(y);
    }
    void nextMinicreep() {
        double height = mainPane.getHeight();
        double y = minicreep.getLayoutY();
        y += minicreepSpeed;
        if(y < minicreep.getRadius()) y = height;
        minicreep.setLayoutY(y);
    }
    void nextArrow() {
        arrow.setVisible(true);
        double width = mainPane.getWidth();
        double x = arrow.getLayoutX(), y = arrow.getLayoutY();
        x += arrowSpeed;
        double r1 = minicreep.getRadius(), r2 = creep.getRadius();
        double x1 = minicreep.getLayoutX(), y1 = minicreep.getLayoutY();
        double x2 = creep.getLayoutX(), y2 = creep.getLayoutY();
        if((x - x1)*(x - x1) + (y - y1)*(y - y1) <= r1*r1) {
            shooting = false;
            scoreNum += 2;
            arrow.setVisible(false);
            arrow.setLayoutX(player.getLayoutX());
            return;
        }
        if((x - x2)*(x - x2) + (y - y2)*(y - y2) <= r2*r2) {
            shooting = false;
            scoreNum += 1;
            arrow.setVisible(false);
            arrow.setLayoutX(player.getLayoutX());
            return;
        }
        if(x > width) {
            shooting = false;
            arrow.setVisible(false);
            arrow.setLayoutX(player.getLayoutX());
            return;
        }
        arrow.setLayoutX(x);
    }

    @FXML
    public void start() {
        gameIsStarted = true;
        shotsNum = 0;
        scoreNum = 0;
        shots.setText("0");
        score.setText("0");
        arrow.setVisible(false);
        arrow.setLayoutX(player.getLayoutX());

        double height = mainPane.getHeight();
        creep.setLayoutY(height / 2);
        minicreep.setLayoutY(height / 2);

        if(t == null)
        {
            t = new Thread(
                    ()->
                    {
                        gameIsStarted = true;
                        gameIsPaused = false;
                        while (gameIsStarted)
                        {
                            Platform.runLater(this::next);
                            try {
                                if(gameIsPaused)
                                {
                                    synchronized (this)
                                    {
                                        this.wait();
                                    }
                                    gameIsPaused = false;
                                }

                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                gameIsStarted = false;
                                t = null;
                            }
                        }
                    }
            );
            t.start();
        }
    }
    @FXML
    public void end() {
        if(t != null)
        {
            gameIsStarted = false;
            t.interrupt();
        }
    }
    @FXML
    void pause(){
        if(t != null)
            gameIsPaused = true;
    }
    @FXML
    void cont(){
        synchronized (this)
        {
            if(t != null)
               this.notifyAll();
        }
    }
    @FXML
    public void shoot() {
        if(gameIsStarted)
            if(!shooting) {
                shooting = true;
                shots.setText(String.valueOf(++shotsNum));
            }
    }
}