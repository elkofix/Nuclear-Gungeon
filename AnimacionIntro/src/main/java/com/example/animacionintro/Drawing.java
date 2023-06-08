package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public abstract class Drawing {

    protected Vector pos = new Vector(0,0);
    protected int speed;
    public abstract void draw(GraphicsContext gc);

    protected Vector world;

    String direction = "";
    public Colission solidArea;
    protected  boolean collissionOn;
}
