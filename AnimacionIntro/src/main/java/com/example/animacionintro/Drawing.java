package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public abstract class Drawing {

    protected Vector pos = new Vector(0,0);
    protected int speed;
    public abstract void draw(GraphicsContext gc);

    protected Vector world;

    public boolean collisionOn = false;

    public boolean conllisionUp= false;
    public boolean collisionDown = false;

    public boolean collisionLeft = false;

    public boolean colssionRight = false;
    protected String direction = "";

    protected Colission solidArea;


}
