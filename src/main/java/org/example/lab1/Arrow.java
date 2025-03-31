package org.example.lab1;

import static java.lang.Math.pow;

public class Arrow extends Movable {

    public final Player owner;
    public final Game game;

    public Arrow(double x, double y,
                 double xBound, double yBound, Player owner, Game game) {
        super(x, y, 20.0, 0, xBound, yBound, 5);
        this.owner = owner;
        this.game = game;
    }

    public boolean touchCreep(Creep creep) {
        double dist = pow(x - creep.x, 2) + pow(y - creep.y, 2);
        return dist <= pow(creep.radius, 2);
    }

    @Override
    public boolean move() {
        if(updatePosition()) {
            if(touchCreep(game.creep)) {
                owner.score++;
                return false;
            }
            if(touchCreep(game.miniCreep)) {
                owner.score+=2;
                return false;
            }
            return true;
        } else {
             return false;
        }
    }

    public ArrowState getState() {
        return new ArrowState(x, y);
    }
}
