package com.example.animacionintro;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelloController implements Initializable, Runnable {
	public static final String MAIN_ROUTE = System.getProperty("user.dir") + "/AnimacionIntro/";
	public static final String LEVEL_ROUTE = MAIN_ROUTE + "src/main/resources/com/example/animacionintro/levels/";
	@FXML
	private Canvas canvas;
	final int originalTileSize = 16;
	final int scale = 3;

	final int tileSize = originalTileSize * scale;
	final int maxScreenCol = 16;
	final int maxScreenRow = 12;
	final int screenWidth = tileSize * maxScreenCol;
	final int screenHeight = tileSize * maxScreenRow;

	public UI ui = new UI(this);
	int FPS = 60;
	private GraphicsContext gc;

	Thread gameThread;

	int momentum = 0;
	public static ArrayList<Level> levels;

	public static int currentLevel = 0;

	private double tempMouseX, tempMouseY;

	public Avatar avatar;

	public int playState = 1;

	public int pauseState = 0;

	public int gameOverState = 2;
	public int gameState = playState;
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		canvas.prefHeight(screenHeight);
		canvas.prefWidth(screenWidth);
		gameThread = new Thread(this);
		gc = canvas.getGraphicsContext2D();
		canvas.setCursor(javafx.scene.Cursor.NONE);
		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(this::onKeyPressed);
		canvas.setOnKeyReleased(this::onKeyReleased);
		canvas.setOnMousePressed(this::onMousePressed);
		canvas.setOnMouseMoved(this::onMouseMoved);
		canvas.setOnMouseReleased(this::onMouseReleased);
		this.avatar = new Avatar();
		avatar.gp=this;
		Gun gun = new Gun(new Vector(100, 200), 8, new Image("file:" + HelloApplication.class.getResource("gun/gun.png").getPath()), 2, 300, 1, this);
		new Thread(avatar).start(); //Esto ejecuta el código dentro de run() en paralelo
		levels = new ArrayList<>();
		ui.showCurrentWeapon(new Image("file:" + HelloApplication.class.getResource("ui/punch.png").getPath()));
		//Generar el primer mapa
		Level l1 = new Level(0, LEVEL_ROUTE + "level_1.txt");
		l1.setColor(Color.WHITE);
		Enemy e = new Enemy(new Vector(400, 100));
		new Thread(e).start();
		l1.getEnemies().add(e);
		l1.getEnemies().add(new Enemy(new Vector(400, 300)));
		l1.getGuns().add(gun);
		l1.getGuns().add(new Gun(new Vector(200, 200), 2, new Image("file:" + HelloApplication.class.getResource("gun/shotgun.png").getPath()), 2, 1000, 4, this));
		levels.add(l1);
		//Generar el segundo mapa
		Level l2 = new Level(1, LEVEL_ROUTE + "level_1.txt");
		l2.setColor(Color.GRAY);
		l2.getEnemies().add(new Enemy(new Vector(100, 100)));
		l2.getEnemies().add(new Enemy(new Vector(100, 300)));
		l2.getEnemies().add(new Enemy(new Vector(300, 300)));
		levels.add(l2);
		gameThread.start();
	}

	private void onMouseMoved(MouseEvent e) {
		double relativePosition = e.getX() - avatar.pos.getX();
		avatar.setFacingRight(
				relativePosition > 0
		);


		if (avatar.getGun() != null) {
			tempMouseX = e.getSceneX();
			tempMouseY = e.getSceneY();
			avatar.getGun().setSceneY(e.getSceneY());
			avatar.getGun().setSceneX(e.getSceneX());
			avatar.getGun().setFront(avatar.pos.getY() > e.getSceneY());
		} else {
			tempMouseX = e.getSceneX();
			tempMouseY = e.getSceneY();
		}
	}

	private void onMousePressed(MouseEvent e) {
		avatar.onMousePressed(e);
	}

	double dirX;
	double dirY;

	private void onMouseReleased(MouseEvent e) {
		if (!e.isSecondaryButtonDown()) {
			rightClickPressed = false;
		}
	}

	private boolean isAlive = true;

	private boolean rightClickPressed = false;





	public void onKeyReleased(KeyEvent event) {
		avatar.onKeyReleased(event);
	}

	public void onKeyPressed(KeyEvent event) {
		System.out.println(event.getCode());
		avatar.onKeyPressed(event);
	}


	public boolean isOutside(double x, double y) {
		return x < -10 || y < -10 || x > canvas.getWidth() || y > canvas.getHeight();
	}


	@Override
	public void run() {
		double drawInterval = 1000000000.0 / FPS;
		double nextDrawTime = System.nanoTime() + drawInterval;
		while (gameThread != null) {
			//Dibujar en el lienzo
			update();
			repaint();
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime /= 1000000;
				if (remainingTime < 0) {
					remainingTime = 0;
				}
				Thread.sleep((long) remainingTime);
				nextDrawTime += drawInterval;
			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		}
	}

	public void update() {
		if(gameState==playState) {
			Level level = levels.get(currentLevel);
			if (avatar.pos.getX() < 25) {
				avatar.pos.setX(25);
			}
			if (avatar.pos.getY() > canvas.getHeight() - 25) {
				avatar.pos.setY(canvas.getHeight() - 25);
			}
			if (avatar.pos.getY() < 0) {
				currentLevel = 1;
				avatar.pos.setY(canvas.getHeight());
			}

			//Colisiones
			for (int i = 0; i < level.getGuns().size(); i++) {
				Gun gun = level.getGuns().get(i);
				double distance = Math.sqrt(
						Math.pow(gun.pos.getX() - avatar.pos.getX(), 2) +
								Math.pow(gun.pos.getY() - avatar.pos.getY(), 2)
				);
				if (distance < 40) {
					if (avatar.Epressed) {
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
						Math.pow(ene.pos.getX() - avatar.pos.getX(), 2) +
								Math.pow(ene.pos.getY() - avatar.pos.getY(), 2)
				);
				if (distance < 40) {
					if (avatar.isAttacking()) {
						;
						level.getEnemies().remove(i);

					}

				}
			}

			for (int i = 0; i < level.getBullets().size(); i++) {
				Bullet bn = level.getBullets().get(i);
				for (int j = 0; j < level.getEnemies().size(); j++) {
					Enemy en = level.getEnemies().get(j);

					double distance = Math.sqrt(
							Math.pow(en.pos.getX() - bn.pos.getX(), 2) +
									Math.pow(en.pos.getY() - bn.pos.getY(), 2)
					);

					if (distance < 15) {
						level.getBullets().remove(i);
						level.getEnemies().remove(j);
					}

				}
			}
			avatar.update();
		}
	}

	public void repaint() {
		if (gameState == playState) {
			Level level = levels.get(currentLevel);
			gc.setFill(level.getColor());
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

			level.paint(gc);

			Gun gun = avatar.getGun();
			if (gun != null) {
				if (gun.isFront()) {
					gun.draw(gc);
					avatar.draw(gc);
				} else {
					avatar.draw(gc);
					gun.draw(gc);
				}
			} else {
				avatar.draw(gc);
			}

			for (int i = 0; i < level.getBullets().size(); i++) {
				level.getBullets().get(i).draw(gc);
				if (isOutside(level.getBullets().get(i).pos.getX(), level.getBullets().get(i).pos.getY())) {
					level.getBullets().remove(i);
				}
			}
			for (int i = 0; i < level.getEnemies().size(); i++) {
				level.getEnemies().get(i).draw(gc);
			}
			for (int i = 0; i < level.getGuns().size(); i++) {
				level.getGuns().get(i).draw(gc);
			}
			ui.draw(gc);
		}
	}
}






