package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Level {

	private int id;
	private Color color;

	public ArrayList<Gun> getGuns() {
		return guns;
	}

	public void setGuns(ArrayList<Gun> guns) {
		this.guns = guns;
	}

	private ArrayList<Gun> guns;
	private ArrayList<Enemy> enemies;
	private ArrayList<Bullet> bullets;
	private int[][] levelDistribution;

	private ArrayList<Wall> mapElements;

	public Level(int id, String levelMatrixPath) {
		this.id = id;
		guns = new ArrayList<>();
		enemies = new ArrayList<>();
		bullets = new ArrayList<>();
		try {
			levelDistribution = loadMatrixFromFile(levelMatrixPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		createMapWalls();
	}

	public static int[][] loadMatrixFromFile(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		Scanner scanner = new Scanner(file);

		int numRows = 10; // Número de filas de la matriz en txt
		int numCols = 15; // Número de columnas de la matriz en txt

		int[][] matrix = new int[numRows][numCols];

		int row = 0;
		while (scanner.hasNextLine() && row < numRows) {
			String line = scanner.nextLine();
			String[] values = line.trim().split("\\s+");

			for (int col = 0; col < numCols; col++) {
				matrix[row][col] = Integer.parseInt(values[col]);
			}

			row++;
		}

		scanner.close();

		return matrix;
	}

	public void createMapWalls() {
		mapElements = new ArrayList<>();
		for (int row = 0; row < levelDistribution.length; row++) {
			for (int col = 0; col < levelDistribution[0].length; col++) {
				if (levelDistribution[row][col] == 1) {
					mapElements.add(new Wall(new Image(HelloController.MAIN_ROUTE + "src/main/resources/com/example/animacionintro/wall/wall.png"), col, row));
					// gc.drawImage(wallImage, col * cellWidth, row * cellHeight, cellWidth, cellHeight);
				}
			}
		}
	}

	public void paint(GraphicsContext gc) {
		double cellWidth = gc.getCanvas().getWidth() / levelDistribution[0].length;
		double cellHeight = gc.getCanvas().getHeight() / levelDistribution.length;
		for (Wall wall : mapElements) {
			double x = wall.getCol() * cellWidth;
			double y = wall.getRow() * cellHeight;

			gc.drawImage(wall.getImage(), x, y, cellWidth, cellHeight);

		}
	}

	public boolean hasCollision(int row, int col) {
		if (row >= 0 && row < levelDistribution.length && col >= 0 && col < levelDistribution[0].length)
			return levelDistribution[row][col] == 1;
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}

	public void setEnemies(ArrayList<Enemy> enemies) {
		this.enemies = enemies;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}
}
