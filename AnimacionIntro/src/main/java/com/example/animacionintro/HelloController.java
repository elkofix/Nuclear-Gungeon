package com.example.animacionintro;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HelloController implements Initializable, Runnable {


    @FXML
    private Canvas canvas;
    final int originalTileSize = 16;
    final int scale = 3;

    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    private GraphicsContext gc;

    Thread gameThread;

    private ReproductorDeSonido reproductorDeSonido = new ReproductorDeSonido(System.getProperty("user.dir")+"/AnimacionIntro/src/main/resources/audio/disparo.wav");

    int momentum = 0;
    private ArrayList<Level> levels;
    private int currentLevel = 0;

    private double tempMouseX, tempMouseY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        canvas.prefHeight(screenHeight);
        canvas.prefWidth(screenWidth);
        gameThread = new Thread(this);

        gc = canvas.getGraphicsContext2D();
        new Thread(reproductorDeSonido).start();
        canvas.setCursor(javafx.scene.Cursor.NONE);
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::onKeyPressed);
        canvas.setOnKeyReleased(this::onKeyReleased);
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseMoved(this::onMouseMoved);
        canvas.setOnMouseReleased(this::onMouseReleased);
        avatar = new Avatar();
        Gun gun = new Gun(new Vector(100, 200), 8, new Image("file:" + HelloApplication.class.getResource("gun/gun.png").getPath()), 2, 300,1);
        new Thread(avatar).start(); //Esto ejecuta el código dentro de run() en paralelo
        levels = new ArrayList<>();

        //Generar el primer mapa
        Level l1 = new Level(0);
        l1.setColor(Color.WHITE);
        Enemy e = new Enemy(new Vector(400, 100));
        new Thread(e).start();
        l1.getEnemies().add(e);
        l1.getEnemies().add(new Enemy(new Vector(400, 300)));
        l1.getGuns().add(gun);
        l1.getGuns().add(new Gun(new Vector(200, 200), 2, new Image("file:" + HelloApplication.class.getResource("gun/shotgun.png").getPath()), 2, 1000, 4));
        levels.add(l1);
        //Generar el segundo mapa
        Level l2 = new Level(1);
        l2.setColor(Color.GRAY);
        l2.getEnemies().add(new Enemy(new Vector(100, 100)));
        l2.getEnemies().add(new Enemy(new Vector(100, 300)));
        l2.getEnemies().add(new Enemy(new Vector(300, 300)));
        levels.add(l2);
        gameThread.start();
    }
    private void onMouseMoved(MouseEvent e) {
        double relativePosition = e.getX()-avatar.pos.getX();
        avatar.setFacingRight(
                relativePosition > 0
        );


        if(avatar.getGun()!=null){
            tempMouseX = e.getSceneX();
            tempMouseY = e.getSceneY();
            avatar.getGun().setSceneY(e.getSceneY());
            avatar.getGun().setSceneX(e.getSceneX());
            avatar.getGun().setFront(avatar.pos.getY()>e.getSceneY());
        }else{
            tempMouseX = e.getSceneX();
            tempMouseY = e.getSceneY();
        }
    }

    private void onMousePressed(MouseEvent e) {
        if(e.isSecondaryButtonDown()){
            rightClickPressed = true;
        }else {
            if(avatar.getGun()!=null) {
                if(avatar.getGun().getBulletQuantity()>0) {
                    if(!avatar.getGun().isLock() && !avatar.isLock() && !avatar.getGun().isReloading()){
                        new Thread(reproductorDeSonido).start();
                        System.out.println("X: " + e.getX() + "Y: " + e.getY());
                        avatar.getGun().setMousePressed(true);
                        double rand = 0;
                        for (int i = 0; i < avatar.getGun().getFirePower(); i++) {
                            if(avatar.getGun().getFirePower()!=1){
                                rand = Math.random()*50;
                            }
                            double diffX = e.getX()+rand - avatar.getGun().pos.getX();
                            double diffY = e.getY()+rand - avatar.getGun().pos.getY();
                            Vector diff = new Vector(diffX, diffY);
                            diff.normalize();
                            diff.setMag(20);
                            Bullet b =new Bullet(
                                    new Vector(avatar.getGun().pos.getX(), avatar.getGun().pos.getY()),
                                    diff
                            );
                            b.setRotationAngle(avatar.getGun().getRotationAngle());
                            levels.get(currentLevel).getBullets().add(
                             b
                            );
                        }

                        avatar.getGun().lock();
                        avatar.getGun().setBulletQuantity(avatar.getGun().getBulletQuantity() - 1);
                    }
                }else{
                    if(!avatar.getGun().isReloading()){
                        avatar.getGun().reload();
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

    private void punchCharacter(ActionEvent actionEvent) {
        avatar.setAttacking(true);
        avatar.setLock(true);
    }

    private void stopPunch(ActionEvent actionEvent) {
        avatar.setFrame(3);
        avatar.setLock(false);
        avatar.setAttacking(false);
    }

    double dirX;
    double dirY;
    private void onMouseReleased(MouseEvent e){
        if(!e.isSecondaryButtonDown()){
            rightClickPressed = false;
        }
    }

    private boolean isAlive = true;

    private boolean Apressed = false;
    private boolean Wpressed = false;
    private boolean Spressed = false;
    private boolean Dpressed = false;

    private boolean tempApressed = false;
    private boolean tempWpressed = false;
    private boolean tempSpressed = false;
    private boolean tempDpressed = false;

    private boolean rightClickPressed = false;
    private boolean Epressed = false;

    private Avatar avatar;



    public void onKeyReleased(KeyEvent event){
        switch (event.getCode()){
            case W: Wpressed = false; break;
            case A: Apressed = false; break;
            case S: Spressed = false; break;
            case D: Dpressed = false; break;
            case E: Epressed = false; break;
        }
    }
    public void onKeyPressed(KeyEvent event){
        System.out.println(event.getCode());
        switch (event.getCode()){
            case W: Wpressed = true; break;
            case A: Apressed = true; break;
            case S: Spressed = true; break;
            case D: Dpressed = true; break;
            case E: Epressed = true; break;
            case SPACE: avatar.setCurrentLives(avatar.getCurrentLives()-1); break;
            case R: avatar.getGun().reload();
        }
    }
    private void stopRoll(ActionEvent actionEvent) {
        avatar.setRolling(false);
        avatar.lock();
        if(avatar.getGun()!=null){
            avatar.getGun().setShow(true);
        }
    }

    private void rollCharacter(ActionEvent actionEvent) {
        avatar.roll(tempWpressed, tempApressed, tempDpressed, tempSpressed);
    }


    public boolean isOutside(double x, double y){
        return x<-10 || y<-10 || x>canvas.getWidth() || y>canvas.getHeight();
    }


    @Override
    public void run() {
        while (gameThread!=null){
                //Dibujar en el lienzo
                update();
                repaint();

        }
    }

    public void update(){
        Level level = levels.get(currentLevel);
        if(avatar.pos.getX() < 25){
            avatar.pos.setX(25);
        }
        if(avatar.pos.getY() > canvas.getHeight() - 25){
            avatar.pos.setY( canvas.getHeight() -25 );
        }
        if(avatar.pos.getY() < 0){
            currentLevel = 1;
            avatar.pos.setY(canvas.getHeight());
        }

        //Colisiones
        for (int i = 0; i < level.getGuns().size(); i++) {
            Gun gun = level.getGuns().get(i);
            double distance = Math.sqrt(
                    Math.pow(gun.pos.getX()-avatar.pos.getX(), 2) +
                            Math.pow(gun.pos.getY()-avatar.pos.getY(), 2)
            );
            if(distance < 40){
                if(Epressed){
                    gun.setSceneX(tempMouseX);
                    gun.setSceneY(tempMouseY);
                    avatar.pickGun(gun);
                    level.getGuns().remove(i);

                }

            }
        }

        for (int i = 0; i < level.getEnemies().size(); i++) {
            Enemy ene = level.getEnemies().get(i);
            double distance = Math.sqrt(
                    Math.pow(ene.pos.getX()-avatar.pos.getX(), 2) +
                            Math.pow(ene.pos.getY()-avatar.pos.getY(), 2)
            );
            if(distance < 40){
                if(avatar.isAttacking()){;
                    level.getEnemies().remove(i);

                }

            }
        }

        for(int i=0 ; i<level.getBullets().size() ; i++){
            Bullet bn = level.getBullets().get(i);
            for(int j=0 ; j<level.getEnemies().size() ; j++){
                Enemy en = level.getEnemies().get(j);

                double distance = Math.sqrt(
                        Math.pow(en.pos.getX()-bn.pos.getX(), 2) +
                                Math.pow(en.pos.getY()-bn.pos.getY(), 2)
                );

                if(distance < 15){
                    level.getBullets().remove(i);
                    level.getEnemies().remove(j);
                }

            }
        }

        if(rightClickPressed){
            if(!avatar.isRolling() && avatar.isMoving() && !avatar.isLock()) {
                rightClickPressed = false;
                avatar.setRolling(true);
                if(avatar.getGun()!=null){
                    avatar.getGun().setShow(false);
                    avatar.setLock(true);
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
        if(!avatar.isRolling() && !avatar.isLock()) {
            if (Wpressed) {
                avatar.pos.setY(avatar.pos.getY() - 3);
            }
            if (Apressed) {
                avatar.pos.setX(avatar.pos.getX() - 3);
            }
            if (Spressed) {
                avatar.pos.setY(avatar.pos.getY() + 3);
            }
            if (Dpressed) {
                avatar.pos.setX(avatar.pos.getX() + 3);
            }
        }


        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }

    public void repaint(){
        Level level = levels.get(currentLevel);
        gc.setFill(level.getColor());
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        avatar.setMoving(Wpressed || Spressed || Dpressed || Apressed);
        Gun gun = avatar.getGun();
        if(gun!=null){
            if(gun.isFront()){
                gun.draw(gc);
                avatar.draw(gc);
            }else{
                avatar.draw(gc);
                gun.draw(gc);
            }
        }else {
            avatar.draw(gc);
        }

        for(int i=0 ; i<level.getBullets().size() ; i++){
            level.getBullets().get(i).draw(gc);
            if(isOutside(level.getBullets().get(i).pos.getX(), level.getBullets().get(i).pos.getY())){
                level.getBullets().remove(i);
            }
        }
        for(int i=0 ; i<level.getEnemies().size() ; i++){
            level.getEnemies().get(i).draw(gc);
        }
        for(int i=0 ; i<level.getGuns().size() ; i++){
            level.getGuns().get(i).draw(gc);
        }
    }
}






