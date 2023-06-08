package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    HelloController gp;
    public Wall[] walls;
    int[][] mapTileNum;

    public TileManager(HelloController gp){
        this.gp = gp;
        walls = new Wall[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap();
    }

    public void getTileImage(){
        walls[0] = new Wall(new Image("file:" + HelloApplication.class.getResource("wall/wall1.png").getPath()), false);
        walls[1] = new Wall(new Image("file:" + HelloApplication.class.getResource("wall/wall.png").getPath()), true);
    }

    public void draw(GraphicsContext gc){

        int worldCol = 0;
        int worldRow = 0;
        while (worldCol<gp.maxWorldCol && worldRow <gp.maxWorldRow){
            int tileNum = mapTileNum[worldCol][worldRow];

            int worldX = worldCol *gp.tileSize;
            int worldY = worldRow *gp.tileSize;

            double screenX = worldX - gp.avatar.world.getX() + gp.avatar.pos.getX();
            double screenY = worldY - gp.avatar.world.getY() + gp.avatar.pos.getY();
            if(walls[tileNum]!=null) {
                gc.drawImage(walls[tileNum].getImage(), screenX-(gp.tileSize*6.2), screenY-(gp.tileSize*4.2), gp.tileSize, gp.tileSize);
            }
            worldCol++;

            if(worldCol== gp.maxWorldCol){
                worldCol=0;
                worldRow++;
            }

        }
    }

    public void loadMap(){
        try {
            InputStream ins = (HelloApplication.class.getResourceAsStream("levels/mapa.txt"));
            BufferedReader bf = new BufferedReader(new InputStreamReader(ins));
            int col = 0;
            int row = 0;
            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = bf.readLine();
                while (col< gp.maxWorldCol){
                    String[] numbers = line.split(" ");
                    int nume = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = nume;
                    col++;
                }
                if(col >= gp.maxScreenCol){
                    col =0;
                    row++;
                }
            }
            bf.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
