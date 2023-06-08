package com.example.animacionintro;

public class CollissionChecker {

    HelloController gp;
    public CollissionChecker(HelloController gp) {
        this.gp =gp;
    }

    public void checkTile(Drawing drawing){
        double drawingLeftWorldX = drawing.world.getX() + drawing.solidArea.solidArea.getX();
        double drawingRightWorldX = drawing.world.getX() + drawing.solidArea.solidArea.getX() +drawing.solidArea.width;
        double drawingTopWorldY = drawing.world.getY() + drawing.solidArea.solidArea.getY();
        double drawingBottomWorldY = drawing.world.getY() + drawing.solidArea.solidArea.getY() + drawing.solidArea.height;

        double drawingLeftCol = drawingLeftWorldX/gp.tileSize;
        double drawingRightCol = drawingRightWorldX/gp.tileSize;
        double drawingTopRow = drawingTopWorldY/gp.tileSize;
        double drawingBottomRow = drawingBottomWorldY/gp.tileSize;

        double tileNum1, tileNum2;

        switch (drawing.direction){
            case "up":
                break;
            case "down":
                break;
            case "left":
                break;
            case "right":
                break;
        }
    }
}
