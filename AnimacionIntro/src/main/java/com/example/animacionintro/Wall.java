package com.example.animacionintro;

import javafx.scene.image.Image;

public class Wall {
	private final Image image;
	private final int col, row;

	public Wall(Image image, int col, int row) {
		this.image = image;
		this.col = col;
		this.row = row;
	}

	public Image getImage() {
		return image;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}
}
