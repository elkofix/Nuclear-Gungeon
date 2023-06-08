package com.example.animacionintro;

public class Vector {

    private double x, y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void normalize() {
        double mag = Math.sqrt(x * x + y * y);
        double angle = Math.atan2(y, x);
        x = 1 * Math.cos(angle);
        y = 1 * Math.sin(angle);
    }
    public Vector normalize2() {
        double magnitude = Math.sqrt(x * x + y * y);
        if (magnitude != 0) {
            x /= magnitude;
            y /= magnitude;
        }
        return this;
    }
    public Vector clone() {
        return new Vector(x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setMag(int scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public Vector subtract(Vector other) {
        double newX = this.x - other.getX();
        double newY = this.y - other.getY();
        return new Vector(newX, newY);
    }

    public double distanceTo(Vector other) {
        double deltaX = other.getX() - this.x;
        double deltaY = other.getY() - this.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
