package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.InputStream;

public class UI {
    private HelloController gp;
    private Font arial_40;
    private Font arial_10;
    int messageCounter =0;
    public int commandNum = 0;
    Image img;
    int time;
    Image[] titleScreen;
    public String message = "";
    private boolean messageOn = false;
    Image heart = new Image("file:" + HelloApplication.class.getResource("players/dandy/heart/heart1.png").getPath());
    Image empty = new Image("file:" + HelloApplication.class.getResource("players/dandy/heart/heart0.png").getPath());

    public void showCurrentWeapon(Image img){
        this.img = img;
    }
    public UI(HelloController gp){
        this.gp = gp;
            //Se carga la fuente
        Font.loadFont(HelloApplication.class.getResourceAsStream("RetroGaming.ttf"), 12);
        arial_40 = Font.font("Retro Gaming", 40);
        arial_10 = new Font("Retro Gaming", 15);
        titleScreen = new Image[6];
        titleScreen[0] = new Image("file:" + HelloApplication.class.getResource("ui/title.png").getPath());
        titleScreen[1] = new Image("file:" + HelloApplication.class.getResource("ui/opt1.png").getPath());
        titleScreen[2] = new Image("file:" + HelloApplication.class.getResource("ui/opt2.png").getPath());
        titleScreen[3] = new Image("file:" + HelloApplication.class.getResource("ui/quit.png").getPath());
        titleScreen[4] = new Image("file:" + HelloApplication.class.getResource("ui/arrow.png").getPath());
        titleScreen[5] = new Image("file:" + HelloApplication.class.getResource("ui/try.png").getPath());
    }

    public void showMessage(String message, int time){
        messageOn = true;
        this.message = message;
        this.time = time;
    }

    public void drawBullets(GraphicsContext gc){
        if(gp.avatar.getGun()!=null){
            gc.setFont(arial_10);
            gc.setStroke(Color.RED);
            gc.strokeText(gp.avatar.getGun().getBulletQuantity()+"", 55, 55);
        }
    }

    public void drawMessage(GraphicsContext gc){
        if(messageOn){
            gc.setFont(arial_10);
            gc.strokeText(message, gp.avatar.pos.getX()-30, gp.avatar.pos.getY()-30);
            messageCounter++;
            if(messageCounter>time){
                messageOn=false;
                messageCounter =0;
            }
        }
    }

    public void drawLive(GraphicsContext gc){
        double width=0;
        for (int i = 0; i < gp.avatar.getCurrentLives(); i++) {
            gc.drawImage(heart, width, 0, 30,30);
            width+=30;
        }
        int rest = gp.avatar.getLives()-gp.avatar.getCurrentLives();
        if(rest<=gp.avatar.getLives()){
            for (int i = 0; i < gp.avatar.getLives()-gp.avatar.getCurrentLives(); i++) {
                gc.drawImage(empty, width, 0, 30,30);
                width+=30;
            }
        }else{
            for (int i = 0; i < gp.avatar.getLives(); i++) {
                gc.drawImage(empty, width, 0, 30,30);
                width+=30;
            }
        }
    }
    public void draw(GraphicsContext gc){
        if(gp.gameState == gp.playState) {
            drawBullets(gc);
            drawMessage(gc);
            drawLive(gc);
            gc.drawImage(img, 30, 40, 18, 18);
        }
        if(gp.gameState == gp.gameOverState){
            gc.setFill(Color.rgb(0,0,0, .5));
            gc.setFont(arial_40);
            gc.setStroke(Color.RED);
            gc.fillRect(0,0,gp.screenWidth, gp.screenHeight);
            gc.strokeText("Game over", 150, 200);
            if(commandNum==0){
                gc.drawImage(titleScreen[4], 100, 250, 150, 50);
            }
            if(commandNum==1){
                gc.drawImage(titleScreen[4], 275, 250, 150, 50);
            }
            gc.drawImage(titleScreen[5], 150, 250, 150, 50);
            gc.drawImage(titleScreen[3], 300, 250, 150, 50);
        }
        if(gp.gameState == gp.titleState){
            drawTitleScreen(gc);
        }
    }

    public void drawTitleScreen(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0 , gp.screenWidth, gp.screenHeight);
        gc.setFont(arial_40);
        String text = "Nuclear Gungeon";
        if(commandNum==0){
            gc.drawImage(titleScreen[4], 135, 200, 150, 50);
        }
        if(commandNum==1){
            gc.drawImage(titleScreen[4], 135, 250, 150, 50);
        }
        if(commandNum==2){
            gc.drawImage(titleScreen[4], 170, 300, 150, 50);
        }
        gc.drawImage(titleScreen[0], 50, 10, 500, 100);
        gc.drawImage(titleScreen[1], 210, 200, 150, 50);
        gc.drawImage(titleScreen[2], 210, 250, 150, 50);
        gc.drawImage(titleScreen[3], 210, 300, 150, 50);
    }
}
