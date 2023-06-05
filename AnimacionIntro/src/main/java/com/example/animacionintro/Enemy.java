package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy extends Drawing implements Runnable{

    public Enemy(Vector pos){
        this.pos = pos;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.RED);
        gc.fillRect(pos.getX(), pos.getY(), 10,10);
        gc.strokeRect(pos.getX(), pos.getY(), 10,10);
    }
    public boolean isAlive = true;
    @Override
    public void run() {
        //Tercer plano
        while (isAlive) {
            double deltaX = Math.random() * 6 - 3;
            double deltaY = Math.random() * 6 - 3;
            pos.setY(pos.getY() + deltaY);
            pos.setX(pos.getX() + deltaX);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
