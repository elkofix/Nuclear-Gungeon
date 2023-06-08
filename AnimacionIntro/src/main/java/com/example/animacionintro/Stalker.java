package com.example.animacionintro;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Stalker extends Enemy {
    private boolean isAlive = true;
    private double followDistance = 210; // Distancia a partir de la cual el Stalker sigue al Avatar
    private Avatar avatar;
    private double speed = 2.0; // Velocidad de movimiento del Stalker

    private Image[] idle;
    private int frame;


    public void getStalkerImages(){
        idle = new Image[4];
        for(int i=1 ; i<=4   ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("enemy/hongo"+i+".png").getPath();
            idle[i-1] = new Image(uri);
        }
    }
    public Stalker(Vector position, int health, Avatar avatar) {
        super(position, health);
        this.avatar = avatar;
        getStalkerImages();
    }

    @Override
    public void update() {
        // Lógica de actualización específica para el Perseguidor
        if (shouldFollowAvatar()) {
            followAvatar();
        }
    }

    private boolean shouldFollowAvatar() {
        if (avatar == null) {
            return false;
        }

        double avatarX = avatar.pos.getX(); // Obtener la posición x del avatar
        double avatarY = avatar.pos.getY(); // Obtener la posición y del avatar
        double distance = pos.distanceTo(new Vector(avatarX, avatarY));
        return distance <= followDistance;
    }

    private void followAvatar() {
        if (avatar == null) {
            return;
        }

        double avatarX = avatar.pos.getX(); // Obtener la posición x del avatar
        double avatarY = avatar.pos.getY(); // Obtener la posición y del avatar
        Vector direction = new Vector(avatarX, avatarY).subtract(pos);
        direction.normalize();
        pos.setX(pos.getX() + direction.getX() * speed);
        pos.setY(pos.getY() + direction.getY() * speed);
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(idle[frame], pos.getX(), pos.getY(), 30, 30);
    }

    @Override
    public void run() {
        // Tercer plano
        while (isAlive) {
            frame = (frame + 1) % 4;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public double getFollowDistance() {
        return followDistance;
    }

    public void setFollowDistance(double followDistance) {
        this.followDistance = followDistance;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
