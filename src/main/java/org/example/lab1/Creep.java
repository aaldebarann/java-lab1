package org.example.lab1;

public class Creep extends Movable {

    public Creep(double x, double y, double xSpeed, double ySpeed,
                 double xBound, double yBound, double radius) {
        super(x, y, xSpeed, ySpeed, xBound, yBound, radius);
    }

    @Override
    public boolean move() {
        if(!updatePosition()) {
            if (y <= radius) {
                y = yBound - radius;
            } else {
                y = radius;
            }
        }
        return true;
    }

    public CreepState getState() {
        return new CreepState(x, y);
    }
}
