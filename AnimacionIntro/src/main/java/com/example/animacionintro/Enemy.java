package com.example.animacionintro;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public abstract class Enemy extends Drawing implements Runnable {
    protected int health;
    protected Level level;

    public Enemy(Vector position, int health) {
        this.pos= position;
        this.health = health;
    }

    public abstract void update();

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            destroy();
        }
    }

    protected void destroy() {
        level.getEnemies().remove(this);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
