package org.example.lab1;

import com.google.gson.Gson;

public class Test {
    public static void main(String[] args) {
        ArrowState arrowState = new ArrowState(100, 4);
        PlayerState playerState = new PlayerState("1000-7", 100, 1200,
                true, true, arrowState);
        ArrowState arrowState1 = new ArrowState(200, 400);
        PlayerState playerState1 = new PlayerState("Чупапупс", 200, 200,
                true, true, arrowState1);
        CreepState creep = new CreepState(200, 100);
        CreepState miniCreep = new CreepState(400, 100);
        PlayerState[] playerStates = {playerState, playerState1};
        GameState state = new GameState(creep, miniCreep, 3, playerStates, Stage.RUNNING);
        Gson json = new Gson();
        String str = json.toJson(state);
        System.out.println("Send :" + str);
    }
}
