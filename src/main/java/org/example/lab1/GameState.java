package org.example.lab1;

public record GameState(CreepState creep, CreepState miniCreep, int numPlayers,
                        PlayerState[] playerStates, Stage stage) {
}
