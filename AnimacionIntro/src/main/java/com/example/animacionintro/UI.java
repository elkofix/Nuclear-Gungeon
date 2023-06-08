package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UI {
    private HelloController gp;
    private Font arial_40;
    private Font arial_10;
    int messageCounter =0;

    Image img;
    int time;
    public String message = "";
    private boolean messageOn = false;
    Image heart = new Image("file:" + HelloApplication.class.getResource("players/dandy/heart/heart1.png").getPath());
    Image empty = new Image("file:" + HelloApplication.class.getResource("players/dandy/heart/heart0.png").getPath());

    public void showCurrentWeapon(Image img){
        this.img = img;
    }
    public UI(HelloController gp){
        this.gp = gp;
        arial_40 = new Font("Arial", 40);
        arial_10 = new Font("Arial", 15);
    }

    public void showMessage(String message, int time){
        messageOn = true;
        this.message = message;
        this.time = time;
    }

    public void draw(GraphicsContext gc){
        if(gp.avatar.getGun()!=null){
            gc.setFont(arial_40);
            gc.setFill(Color.GREEN);
            gc.strokeText("Bullets ="+gp.avatar.getGun().getBulletQuantity(), 50, 50);
        }

        if(messageOn){

            gc.setFont(arial_10);
            gc.strokeText(message, gp.avatar.pos.getX()-30, gp.avatar.pos.getY()-30);
            messageCounter++;
            if(messageCounter>time){
                messageOn=false;
                messageCounter =0;
            }
        }
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
        gc.drawImage(img, 20, 30, 18,18);
    }
}
