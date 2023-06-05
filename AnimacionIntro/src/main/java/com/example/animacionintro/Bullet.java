package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet extends Drawing {

    public Vector getDir() {
        return dir;
    }

    public void setDir(Vector dir) {
        this.dir = dir;
    }

    private Vector dir;
    public Bullet(Vector pos, Vector dir){
        this.pos = pos;
        this.dir = dir;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillOval(pos.getX()-5, pos.getY()-5, 20,20);
        gc.getCanvas().getScene();
        pos.setX( pos.getX() + dir.getX() );
        pos.setY( pos.getY() + dir.getY() );
    }
}
