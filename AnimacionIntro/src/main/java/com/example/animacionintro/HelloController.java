package com.example.animacionintro;

import javafx.application.Platform;
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

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelloController implements Initializable {


    @FXML
    private Canvas canvas;
    private GraphicsContext gc;

    private ReproductorDeSonido reproductorDeSonido = new ReproductorDeSonido(System.getProperty("user.dir")+"/src/main/resources/audio/disparo.wav");

    private ArrayList<Level> levels;
    private int currentLevel = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::onKeyPressed);
        canvas.setOnKeyReleased(this::onKeyReleased);
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseMoved(this::onMouseMoved);
        //canvas.setOnMouseDragged(this::handleMouseDragged);
        avatar = new Avatar();
        avatar.setGun(new Gun(avatar.pos, 8, new Image("file:" + HelloApplication.class.getResource("gun/gun.png").getPath()), 2));
        new Thread(avatar).start(); //Esto ejecuta el código dentro de run() en paralelo
        new Thread(avatar.getGun()).start();
        levels = new ArrayList<>();

        //Generar el primer mapa
        Level l1 = new Level(0);
        l1.setColor(Color.WHITE);
        Enemy e = new Enemy(new Vector(400, 100));
        new Thread(e).start();
        l1.getEnemies().add(e);
        l1.getEnemies().add(new Enemy(new Vector(400, 300)));
        levels.add(l1);
        //Generar el segundo mapa
        Level l2 = new Level(1);
        l2.setColor(Color.GRAY);
        l2.getEnemies().add(new Enemy(new Vector(100, 100)));
        l2.getEnemies().add(new Enemy(new Vector(100, 300)));
        l2.getEnemies().add(new Enemy(new Vector(300, 300)));
        levels.add(l2);

        draw();
    }
    private void onMouseMoved(MouseEvent e) {
        double relativePosition = e.getX()-avatar.pos.getX();
        avatar.setFacingRight(
                relativePosition > 0
        );
        if(avatar.getGun()!=null){
            avatar.getGun().setSceneY(e.getSceneY());
            avatar.getGun().setSceneX(e.getSceneX());
            avatar.getGun().setFront(avatar.pos.getY()>e.getSceneY());
        }
    }

    private void onMousePressed(MouseEvent e) {
        if(avatar.getGun()!=null) {
            if(avatar.getGun().getBulletQuantity()>0) {
                new Thread(reproductorDeSonido).start();
                System.out.println("X: " + e.getX() + "Y: " + e.getY());
                avatar.getGun().setMousePressed(true);
                double diffX = e.getX() - avatar.pos.getX();
                double diffY = e.getY() - avatar.pos.getY();
                Vector diff = new Vector(diffX, diffY);
                diff.normalize();
                diff.setMag(15);


                levels.get(currentLevel).getBullets().add(
                        new Bullet(
                                new Vector(avatar.pos.getX(), avatar.pos.getY()),
                                diff
                        )
                );
                avatar.getGun().setBulletQuantity(avatar.getGun().getBulletQuantity()-1);
            }else{
                if(!avatar.getGun().isReloading()){
                    avatar.getGun().reload();
                }
            }
        }
    }


    private boolean isAlive = true;

    private boolean Apressed = false;
    private boolean Wpressed = false;
    private boolean Spressed = false;
    private boolean Dpressed = false;


    private Avatar avatar;



    public void onKeyReleased(KeyEvent event){
        switch (event.getCode()){
            case W: Wpressed = false; break;
            case A: Apressed = false; break;
            case S: Spressed = false; break;
            case D: Dpressed = false; break;
        }
    }
    public void onKeyPressed(KeyEvent event){
        System.out.println(event.getCode());
        switch (event.getCode()){
            case W: Wpressed = true; break;
            case A: Apressed = true; break;
            case S: Spressed = true; break;
            case D: Dpressed = true; break;
            case R: avatar.getGun().reload();
        }
    }


    public void draw(){
        //
        Thread ae = new Thread(()->{
            while(isAlive){
                //Dibujar en el lienzo
                Level level = levels.get(currentLevel);

                Platform.runLater(()->{//Runnable
                    //Lo que hagamos aqui, corre en el main thread
                    gc.setFill(level.getColor());
                    gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
                    avatar.setMoving(Wpressed || Spressed || Dpressed || Apressed);
                    //avatar.draw(gc);
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
                });

                //Calculos geometricos


                //Paredes
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
                for(int i=0 ; i<level.getBullets().size() ; i++){
                    Bullet bn = level.getBullets().get(i);
                    for(int j=0 ; j<level.getEnemies().size() ; j++){
                        Enemy en = level.getEnemies().get(j);

                        double distance = Math.sqrt(
                                Math.pow(en.pos.getX()-bn.pos.getX(), 2) +
                                        Math.pow(en.pos.getY()-bn.pos.getY(), 2)
                        );

                        if(distance < 10){
                            level.getBullets().remove(i);
                            level.getEnemies().remove(j);
                        }

                    }
                }



                if(Wpressed){
                    avatar.pos.setY(avatar.pos.getY()-3);
                }
                if (Apressed) {
                    avatar.pos.setX(avatar.pos.getX()-3);
                }
                if (Spressed) {
                    avatar.pos.setY(avatar.pos.getY()+3);
                }
                if (Dpressed) {
                    avatar.pos.setX(avatar.pos.getX()+3);
                }


                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {e.printStackTrace();}
            }
        });
        ae.start();
    }


    public boolean isOutside(double x, double y){
        return x<-10 || y<-10 || x>canvas.getWidth() || y>canvas.getHeight();
    }


}






