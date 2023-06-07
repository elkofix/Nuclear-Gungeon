package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Avatar extends Drawing implements Runnable{

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }


    private int lives;
    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    private boolean isAttacking;
    private boolean isLock;
    private Image[] idle;

    public Gun getGun() {
        return gun;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
    }

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private Gun gun;
    private Image[] run;

    private Image[] roll;
    private Image[] hit;

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    private int frame = 0;

    public boolean isRolling() {
        return isRolling;
    }

    public void setRolling(boolean rolling) {
        isRolling = rolling;
    }

    private boolean isRolling;
    private boolean isMoving;
    private boolean isFacingRight = true;

    public Avatar(){
        lives = 8;
        pos.setX(100);
        pos.setY(100);
        executorService.shutdown();
        idle = new Image[4];
        for(int i=1 ; i<=4   ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("idle/idle"+i+".png").getPath();
            idle[i-1] = new Image(uri);
        }
        run = new Image[4];
        for(int i=1 ; i<=4 ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("run/run"+i+".png").getPath();
            run[i-1] = new Image(uri);
        }

        roll = new Image[4];
        for(int i=1 ; i<=4 ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("roll/roll"+i+".png").getPath();
            roll[i-1] = new Image(uri);
        }
        hit = new Image[4];
        for(int i=1 ; i<=4 ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("hit/hit"+i+".png").getPath();
            hit[i-1] = new Image(uri);
        }
    }

    public void lock(){
        if (!executorService.isShutdown() && !executorService.isTerminated()) {
            return;
        }

        // Programar la recdarga después del tiempo de recarga
        executorService = Executors.newSingleThreadScheduledExecutor();
        isLock = true;
        executorService.schedule(() -> {
            isLock = false;
        }, 200, TimeUnit.MILLISECONDS);
        executorService.shutdown();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(isRolling ? roll[frame] : (isAttacking ? hit[frame] : (isMoving ? run[frame] : idle[frame])),
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


    public void roll(boolean Wpressed,boolean Apressed,boolean Dpressed,boolean Spressed){
        double d = 1.5;
        double dx = 1;
        if(Wpressed && !Apressed && !Dpressed && !Spressed){
            pos.setY(pos.getY() - d);
        }
        if(!Wpressed && !Apressed && Dpressed && !Spressed){
            pos.setX(pos.getX() + d);
        }
        if(!Wpressed && Apressed && !Dpressed && !Spressed){
            pos.setX(pos.getX() - d);
        }
        if(!Wpressed && !Apressed && !Dpressed && Spressed){
            pos.setY(pos.getY() + d);
        }
        if(Wpressed && Apressed && !Dpressed && !Spressed){
            pos.setY(pos.getY() - dx);
            pos.setX(pos.getX() - dx);
        }
        if(Wpressed && !Apressed && Dpressed && !Spressed){
            pos.setY(pos.getY() - dx);
            pos.setX(pos.getX() + dx);
        }
        if(!Wpressed && !Apressed && Dpressed && Spressed){
            pos.setY(pos.getY() + dx);
            pos.setX(pos.getX() + dx);
        }
        if(!Wpressed && Apressed && !Dpressed && Spressed){
            pos.setY(pos.getY() + dx);
            pos.setX(pos.getX() - dx);
        }


    }
    //Ejecutar en paralelo
    @Override
    public void run() {
        while (true) {
            frame = (frame + 1) % 4;
            try {
                Thread.sleep(120);
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
