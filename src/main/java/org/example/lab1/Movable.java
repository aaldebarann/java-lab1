package org.example.lab1;

public abstract class Movable {
    public double xBound, yBound;
    public double xSpeed, ySpeed;
    public double x, y;
    public double radius;
    public Movable(double x, double y, double xSpeed, double ySpeed,
             double xBound, double yBound, double radius) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.xBound = xBound;
        this.yBound = yBound;
        this.radius = radius;
    }

    protected boolean updatePosition() {
        x += xSpeed;
        y += ySpeed;
        return (radius <= x && x < xBound - radius &&
                radius <= y && y < yBound - radius);
    }
    protected abstract boolean move();


}
