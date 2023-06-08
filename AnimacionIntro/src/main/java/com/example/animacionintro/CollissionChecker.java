package com.example.animacionintro;

public class CollissionChecker {

    HelloController gp;
    public CollissionChecker(HelloController gp) {
        this.gp =gp;
    }

    public void checkTile(Drawing drawing){
        int drawingLeftWorldX = (int) (drawing.world.getX() + drawing.solidArea.solidArea.getX());
        int drawingRightWorldX =(int) (drawing.world.getX() + drawing.solidArea.solidArea.getX() +drawing.solidArea.width);
        int drawingTopWorldY = (int)(drawing.world.getY() + drawing.solidArea.solidArea.getY());
        int drawingBottomWorldY = (int)(drawing.world.getY() + drawing.solidArea.solidArea.getY() + drawing.solidArea.height);

        int drawingLeftCol = drawingLeftWorldX/gp.tileSize;
        int drawingRightCol = drawingRightWorldX/gp.tileSize;
        int drawingTopRow = drawingTopWorldY/gp.tileSize;
        int drawingBottomRow = drawingBottomWorldY/gp.tileSize;

        int tileNum1, tileNum2;

        switch (drawing.direction){
            case "up":
                drawingTopRow = (drawingTopWorldY - drawing.speed-5)/gp.tileSize;
                tileNum1 = gp.tile.mapTileNum[drawingLeftCol][drawingTopRow];
                tileNum2 = gp.tile.mapTileNum[drawingRightCol][drawingTopRow];

                if (gp.tile.walls[tileNum1].collission || gp.tile.walls[tileNum2].collission) {
                        drawing.collisionOn = true;
                }
                break;
            case "down":
                drawingBottomRow = (drawingBottomWorldY + drawing.speed+5)/gp.tileSize;
                tileNum1 = gp.tile.mapTileNum[drawingLeftCol][drawingBottomRow];
                tileNum2 = gp.tile.mapTileNum[drawingRightCol][drawingBottomRow];

                if (gp.tile.walls[tileNum1].collission || gp.tile.walls[tileNum2].collission) {
                    drawing.collisionOn = true;
                }
                break;
            case "left":
                drawingLeftCol = (drawingLeftWorldX - drawing.speed-5)/gp.tileSize;
                tileNum1 = gp.tile.mapTileNum[drawingLeftCol][drawingTopRow];
                tileNum2 = gp.tile.mapTileNum[drawingLeftCol][drawingBottomRow];

                if (gp.tile.walls[tileNum1].collission || gp.tile.walls[tileNum2].collission) {
                    drawing.collisionOn = true;
                }
                break;
            case "right":
                drawingRightCol = (drawingRightWorldX + drawing.speed+5)/gp.tileSize;
                tileNum1 = gp.tile.mapTileNum[drawingRightCol][drawingTopRow];
                tileNum2 = gp.tile.mapTileNum[drawingRightCol][drawingBottomRow];

                if (gp.tile.walls[tileNum1].collission || gp.tile.walls[tileNum2].collission) {
                    drawing.collisionOn = true;
                }
                break;
        }
    }


}
