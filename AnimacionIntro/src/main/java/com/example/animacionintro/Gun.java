package com.example.animacionintro;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Gun extends Drawing implements Runnable{
    private boolean isReloading;
    private boolean isLock;
    private int firerate;
    ScheduledExecutorService executorService;
    ScheduledExecutorService executorServiceFire;
    private int reloadTime;
    private boolean isFront;

    private double rotationAngle;
    private int bulletSize;
    private boolean mousePressed;
    private int bulletQuantity;
    private boolean hasPlayer;
    private double mouseX;
    private double mouseY;
    private Rotate rotateX, rotateY;
    private Image img;
    private Vector center;
    private double SceneX;
    private double SceneY;
    int frame;

    public int getFirePower() {
        return firePower;
    }

    public void setFirePower(int firePower) {
        this.firePower = firePower;
    }

    int firePower;

    public Gun(Vector vector, int bulletQuantity, Image img, int reloadTime, int firerate, int firePower){
        this.center = vector;
        this.firePower = firePower;
        this.firerate = firerate;
        pos = vector;
        IMAGE_WIDTH = img.getWidth();
        IMAGE_HEIGHT = img.getHeight();
        this.img = img;
        this.reloadTime = reloadTime;
        this.bulletQuantity = bulletQuantity;
        this.bulletSize = bulletQuantity;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorServiceFire = Executors.newSingleThreadScheduledExecutor();
        executorServiceFire.shutdown();
        executorService.shutdown();
    }

    public void reload(){
        if (bulletQuantity == bulletSize) {
            // El arma ya está completamente cargada
            return;
        }

        // Detener la ejecución actual (si hay alguna)
        if (!executorService.isShutdown() && !executorService.isTerminated()) {
            System.out.println("La recarga ya está en progreso");
            return;
        }

        // Programar la recdarga después del tiempo de recarga
        executorService = Executors.newSingleThreadScheduledExecutor();
        isReloading = true;
        executorService.schedule(() -> {
            bulletQuantity = bulletSize;
            System.out.println(bulletQuantity);
            System.out.println("¡Arma recargada!");
            isReloading = false;
        }, reloadTime, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    public void lock(){
        System.out.println(bulletQuantity);
        // Detener la ejecución actual (si hay alguna)
        if (!executorServiceFire.isShutdown() && !executorServiceFire.isTerminated()) {
            System.out.println("El arma ya esta bloqueada");
            return;
        }

        // Programar la recdarga después del tiempo de recarga
        executorServiceFire = Executors.newSingleThreadScheduledExecutor();
        isLock = true;
        executorServiceFire.schedule(() -> {
            System.out.println("¡Arma bloqueada!");
            isLock = false;
        }, firerate, TimeUnit.MILLISECONDS);
        executorServiceFire.shutdown();
    }

    @Override
    public void draw(GraphicsContext gc) {
        center = pos;
        if(hasPlayer) {
            gc.save();
            double centerX = center.getX();
            double centerY = center.getY();
            gc.setFill(Color.BLUE);
            gc.fillOval(mouseX, mouseY, 10, 10);
            // Calcular la posición de la imagen en función
            // Calcular la posición de la imagen en función de la distancia fija y el ángulo de rotación
            double angle = calculateRotationAngle(centerX, centerY, mouseX, mouseY);
            double x = centerX + Math.cos(Math.toRadians(angle)) * IMAGE_WIDTH/18;
            double y = centerY + Math.sin(Math.toRadians(angle)) * IMAGE_WIDTH/18;

            // Calcular el ángulo de rotación basado en la posición del mouse
            double rotationAngle = calculateRotationAngle(centerX, centerY, mouseX, mouseY);

            // Determinar si la imagen debe reflejarse en el eje Y
            boolean reflectY = mouseX > centerX;

            gc.translate(x, y+IMAGE_WIDTH/40);
            this.rotationAngle = rotationAngle;
            gc.rotate(rotationAngle);

            if (reflectY) {
                gc.scale(1, -1); // Reflejar en el eje Y
            }
            gc.drawImage(img, IMAGE_WIDTH / 6, IMAGE_HEIGHT / 6, -IMAGE_WIDTH/6, -IMAGE_HEIGHT/6);
            gc.restore();
        }else{
            gc.drawImage(img, pos.getX(), pos.getY(), IMAGE_WIDTH/6, IMAGE_HEIGHT/6);
        }
    }

    private double calculateRotationAngle(double centerX, double centerY, double targetX, double targetY) {
        double deltaX = targetX - centerX;
        double deltaY = targetY - centerY;
        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }


    @Override
    public void run() {
        while (true) {
            mouseX = SceneX;
            mouseY = SceneY;
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public double getIMAGE_WIDTH() {
        return IMAGE_WIDTH;
    }

    public void setIMAGE_WIDTH(double IMAGE_WIDTH) {
        this.IMAGE_WIDTH = IMAGE_WIDTH;
    }

    public double getIMAGE_HEIGHT() {
        return IMAGE_HEIGHT;
    }

    public void setIMAGE_HEIGHT(double IMAGE_HEIGHT) {
        this.IMAGE_HEIGHT = IMAGE_HEIGHT;
    }

    private double IMAGE_WIDTH;

    private double IMAGE_HEIGHT;


    public void setSceneX(double sceneX) {
        SceneX = sceneX;
    }



    public double getSceneY() {
        return SceneY;
    }

    public void setSceneY(double sceneY) {
        SceneY = sceneY;
    }

    public boolean isReloading() {
        return isReloading;
    }
    public boolean isMousePressed() {
        return mousePressed;
    }
    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }
    public boolean isHasPlayer() {
        return hasPlayer;
    }
    public void setHasPlayer(boolean hasPlayer) {
        this.hasPlayer = hasPlayer;
    }
    public int getBulletQuantity() {
        return bulletQuantity;
    }
    public void setBulletQuantity(int bulletQuantity) {
        this.bulletQuantity = bulletQuantity;
    }
    public boolean isFront() {
        return isFront;
    }
    public void setFront(boolean front) {
        isFront = front;
    }
    public void setMouseX(double mouseX) {
        this.mouseX = mouseX;
    }
    public double getMouseY() {
        return mouseY;
    }
    public double getMouseX() {
        return mouseX;
    }
    public void setMouseY(double mouseY) {
        this.mouseY = mouseY;
    }
    public void setReloading(boolean reloading) {
        isReloading = reloading;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}
