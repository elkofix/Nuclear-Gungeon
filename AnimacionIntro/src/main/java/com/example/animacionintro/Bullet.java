package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bullet extends Drawing {

    public Vector getDir() {
        return dir;
    }

    public boolean enemy;

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public double rotationAngle;

    Image img = new Image("file:" + HelloApplication.class.getResource("bullet/bullet.png").getPath());

    public void setDir(Vector dir) {
        this.dir = dir;
    }

    private Vector dir;
    public Bullet(Vector pos, Vector dir, boolean enemy){
        this.pos = pos;
        this.enemy = enemy;
        this.dir = dir;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(img,pos.getX()-5, pos.getY()-5, 20,20);
        gc.getCanvas().getScene();
        pos.setX( pos.getX() + dir.getX() );
        pos.setY( pos.getY() + dir.getY() );

    }
}
