package com.example.animacionintro;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Level {

    private int id;
    private Color color;

    public ArrayList<Gun> getGuns() {
        return guns;
    }

    public void setGuns(ArrayList<Gun> guns) {
        this.guns = guns;
    }

    private ArrayList<Gun> guns;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;

    public Level(int id){
        this.id = id;
        guns = new ArrayList<>();
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(ArrayList<Bullet> bullets) {
        this.bullets = bullets;
    }
}
