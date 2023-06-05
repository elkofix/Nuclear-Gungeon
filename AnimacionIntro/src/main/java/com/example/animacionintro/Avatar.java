package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Avatar extends Drawing implements Runnable{


    private Image[] idle;

    public Gun getGun() {
        return gun;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
    }

    private Gun gun;
    private Image[] run;
    private int frame = 0;

    private boolean isMoving;
    private boolean isFacingRight = true;

    public Avatar(){
        pos.setX(100);
        pos.setY(100);

        idle = new Image[6];
        for(int i=1 ; i<=6 ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("idle/player-idle"+i+".png").getPath();
            idle[i-1] = new Image(uri);
        }
        run = new Image[6];
        for(int i=1 ; i<=6 ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("pistol/player-run-shoott"+i+".png").getPath();
            run[i-1] = new Image(uri);
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(isMoving?run[frame]:idle[frame],
                isFacingRight?pos.getX()-25:pos.getX()+25,
                pos.getY()-25,
                isFacingRight?50:-50,
                50);
    }

    public void pickGun(Gun gun){
        new Thread(gun).start();
        this.gun = gun;
        gun.pos = this.pos;
        gun.setHasPlayer(true);

    }

    //Ejecutar en paralelo
    @Override
    public void run() {
        while (true) {
            frame = (frame + 1) % 6;
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        isFacingRight = facingRight;
    }
}
