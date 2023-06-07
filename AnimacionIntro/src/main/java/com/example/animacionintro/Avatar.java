package com.example.animacionintro;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.animacionintro.HelloController.currentLevel;
import static com.example.animacionintro.HelloController.levels;

public class Avatar extends Drawing implements Runnable{

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    private ReproductorDeSonido reproductorDeSonido = new ReproductorDeSonido(System.getProperty("user.dir")+"/AnimacionIntro/src/main/resources/audio/disparo.wav");


    public int getCurrentLives() {
        return currentLives;
    }

    public void setCurrentLives(int currentLives) {
        this.currentLives = currentLives;
    }

    private int currentLives;

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    private int lives;
    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    private int speed;
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

    private boolean Apressed = false;
    private boolean Wpressed = false;
    private boolean Spressed = false;
    private boolean Dpressed = false;


    private boolean tempApressed = false;
    private boolean tempWpressed = false;
    private boolean tempSpressed = false;
    private boolean tempDpressed = false;

    private boolean rightClickPressed = false;
    public boolean Epressed = false;

    Image heart = new Image("file:" + HelloApplication.class.getResource("heart/heart1.png").getPath());
    Image empty = new Image("file:" + HelloApplication.class.getResource("heart/heart0.png").getPath());
    public Avatar(){
        lives = 8;
        new Thread(reproductorDeSonido).start();
        pos.setY(100);
        pos.setX(100);
        speed = 3;
        currentLives = lives;
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

    public void onMousePressed(MouseEvent e){
        if(e.isSecondaryButtonDown()){
            rightClickPressed = true;
        }else {
            if(getGun()!=null) {
                if(getGun().getBulletQuantity()>0) {
                    if(!getGun().isLock() && !isLock() && !getGun().isReloading()){
                        new Thread(reproductorDeSonido).start();
                        System.out.println("X: " + e.getX() + "Y: " + e.getY());
                        getGun().setMousePressed(true);
                        double rand = 0;
                        for (int i = 0; i < getGun().getFirePower(); i++) {
                            if(getGun().getFirePower()!=1){
                                rand = Math.random()*50;
                            }
                            double diffX = e.getX()+rand - getGun().pos.getX();
                            double diffY = e.getY()+rand - getGun().pos.getY();
                            Vector diff = new Vector(diffX, diffY);
                            diff.normalize();
                            diff.setMag(20);
                            Bullet b =new Bullet(
                                    new Vector(getGun().pos.getX(), getGun().pos.getY()),
                                    diff
                            );
                            b.setRotationAngle(getGun().getRotationAngle());
                            levels.get(currentLevel).getBullets().add(
                                    b
                            );
                        }

                        getGun().lock();
                        getGun().setBulletQuantity(getGun().getBulletQuantity() - 1);
                    }
                }else{
                    if(!getGun().isReloading()){
                        getGun().reload();
                    }
                }
            }else{

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, this::punchCharacter),
                        new KeyFrame(Duration.millis(10), this::punchCharacter)
                );
                timeline.setCycleCount(40);
                timeline.setOnFinished(this::stopPunch);
                timeline.play();
            }
        }
    }

    public void onKeyReleased(KeyEvent event){
        switch (event.getCode()){
            case W: Wpressed = false; isMoving = false; break;
            case A: Apressed = false; isMoving = false;break;
            case S: Spressed = false; isMoving = false; break;
            case D: Dpressed = false; isMoving = false;break;
            case E: Epressed = false; isMoving = false; break;
        }
    }
    public void onKeyPressed(KeyEvent event){
        System.out.println(event.getCode());
        switch (event.getCode()){
            case W: Wpressed = true ;isMoving = true; break;
            case A: Apressed = true; isMoving = true;break;
            case S: Spressed = true; isMoving = true;break;
            case D: Dpressed = true; isMoving = true;break;
            case E: Epressed = true; isMoving = true;break;
            case SPACE: setCurrentLives(getCurrentLives()-1); break;
            case R: if(getGun()!=null){gun.reload();};
        }
    }

    public void update(){
        if(!isRolling() && !isLock()) {
            if (Wpressed) {
                pos.setY(pos.getY() - speed);
            }
            if (Apressed) {
                pos.setX(pos.getX() - speed);
            }
            if (Spressed) {
                pos.setY(pos.getY() + speed);
            }
            if (Dpressed) {
                pos.setX(pos.getX() + speed);
            }
        }

        if(rightClickPressed){
            if(!isRolling() && isMoving() && !isLock()) {
                rightClickPressed = false;
                setRolling(true);
                if(getGun()!=null){
                    getGun().setShow(false);
                    setLock(true);
                }
                tempApressed = Apressed;
                tempWpressed = Wpressed;
                tempSpressed = Spressed;
                tempDpressed = Dpressed;
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, this::rollCharacter),
                        new KeyFrame(Duration.millis(10), this::rollCharacter)
                );
                timeline.setCycleCount(40);
                timeline.setOnFinished(this::stopRoll);
                timeline.play();
            }
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
        setMoving(Wpressed || Spressed || Dpressed || Apressed);
        gc.drawImage(isRolling ? roll[frame] : (isAttacking ? hit[frame] : (isMoving ? run[frame] : idle[frame])),
                isFacingRight?pos.getX()-25:pos.getX()+25,
                pos.getY()-25,
                isFacingRight?50:-50,
                50);
        double width=0;
        for (int i = 0; i < currentLives; i++) {
            gc.drawImage(heart, width, 0, 30,30);
            width+=30;
        }
        int rest = lives-currentLives;
        if(rest<=lives){
            for (int i = 0; i < lives-currentLives; i++) {
                gc.drawImage(empty, width, 0, 30,30);
                width+=30;
            }
        }else{
            for (int i = 0; i < lives; i++) {
                gc.drawImage(empty, width, 0, 30,30);
                width+=30;
            }
        }
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

    private void stopRoll(ActionEvent actionEvent) {
        setRolling(false);
        lock();
        if(getGun()!=null){
            getGun().setShow(true);
        }
    }


    private void punchCharacter(ActionEvent actionEvent) {
        setAttacking(true);
        setLock(true);
    }

    private void stopPunch(ActionEvent actionEvent) {
        setFrame(3);
        setLock(false);
        setAttacking(false);
    }

    private void rollCharacter(ActionEvent actionEvent) {
        roll(tempWpressed, tempApressed, tempDpressed, tempSpressed);
    }
}
