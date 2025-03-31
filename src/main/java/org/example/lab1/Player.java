package org.example.lab1;

public class Player {
    public String name;
    public int score, shots;
    public boolean isReady;
    public boolean isShooting;
    public Arrow arrow;
    public Game game;

    public Player(String name, double yPosition, double xBound, double yBound, Game game) {
        this.name = name;
        arrow = new Arrow(112, yPosition, xBound, yBound, this, game);
        reset();
    }

    public void reset() {
        score = 0;
        shots = 0;
        isShooting = false;
        isReady = false;
    }

    public PlayerState getState() {
        return new PlayerState(name, score, shots, isReady, isShooting, arrow.getState());
    }

    public void update() {
        if(isShooting) {
            isShooting = arrow.move();
            if(!isShooting) {
                arrow.x = 112;
            }
        }
    }
}