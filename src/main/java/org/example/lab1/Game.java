package org.example.lab1;

import com.google.gson.Gson;

public class Game {
    public int numPlayers;
    public Player[] players;
    public Stage stage;
    public Creep creep, miniCreep;
    public double xBound, yBound;
    public final int MAX_PLAYERS = 4;

    public Game() {
        xBound = 784;
        yBound = 695;
        numPlayers = 0;
        players = new Player[4];
        stage = Stage.NO_PLAYERS;
        creep = new Creep(536, 349, 0, 2, xBound, yBound, 60);
        miniCreep = new Creep(676, 349, 0, -4, xBound, yBound, 30);
    }

    public GameState getState() {
        PlayerState[] playerStates = new PlayerState[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            playerStates[i] = players[i].getState();
        }
        return new GameState(creep.getState(), miniCreep.getState(), numPlayers, playerStates, stage);
    }

    public void update() {
        creep.move();
        miniCreep.move();
        for(int i = 0; i < numPlayers; i++) {
            players[i].update();
        }
    }
}
