package com.example.animacionintro;

import javafx.scene.image.Image;

public class Wall {
	private final Image image;

	public boolean collission;

	public Wall(Image image, boolean collission) {
		this.image = image;
		this.collission = collission;
	}

	public Image getImage() {
		return image;
	}
}
