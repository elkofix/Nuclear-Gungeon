package com.example.animacionintro;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCode.ENTER;

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

	public final int pauseState = 0;
	public final int playState = 1;
	public final int gameOverState = 2;
	public final int titleState = 3;
	public int gameState = titleState;
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		init();
	}

	public void init(){
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
		Stalker stalker1 = new Stalker(new Vector(400, 100), 2, avatar);
		new Thread(stalker1).start();
		l1.getEnemies().add(stalker1);


		Stalker stalker2 = new Stalker(new Vector(400, 300), 2, avatar);
		new Thread(stalker2).start();
		l1.getEnemies().add(stalker2);

		Guard guard = new Guard(new Vector(200, 200), 3, new Vector(300, 200), new Vector(100, 400), avatar, l1);
		new Thread(guard).start();
		l1.getEnemies().add(guard);

		l1.getGuns().add(gun);
		l1.getGuns().add(new Gun(new Vector(200, 200), 2, new Image("file:" + HelloApplication.class.getResource("gun/shotgun.png").getPath()), 2, 1000, 4, this));


		levels.add(l1);
		//Generar el segundo mapa
		Level l2 = new Level(1, LEVEL_ROUTE + "level_1.txt");
		l2.setColor(Color.GRAY);
		l2.getEnemies().add(new Stalker(new Vector(100, 100),100, avatar));
		l2.getEnemies().add(new Stalker(new Vector(100, 300),100, avatar));
		l2.getEnemies().add(new Stalker(new Vector(300, 300),100, avatar));

		Guard guard1 = new Guard(new Vector(200, 200), 10, new Vector(300, 200), new Vector(100, 400), avatar, l2);
		new Thread(guard1).start();
		l2.getEnemies().add(guard1);

		Guard guard2 = new Guard(new Vector(500, 400), 15, new Vector(600, 400), new Vector(400, 600),avatar, l2);
		new Thread(guard2).start();
		l2.getEnemies().add(guard2);

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

	public static boolean isAlive = true;

	private boolean rightClickPressed = false;





	public void onKeyReleased(KeyEvent event) {
		avatar.onKeyReleased(event);
	}

	public void onKeyPressed(KeyEvent event) {
		if(gameState == playState || gameState == pauseState) {
			System.out.println(event.getCode());
			avatar.onKeyPressed(event);
		}
		if(gameState == titleState){
			handleTitle(event);
		}
		if(gameState == gameOverState){
			handleGameOver(event);
		}
	}

	public void handleGameOver(KeyEvent event){
		if(event.getCode()==A){
			if(ui.commandNum>0) {
				ui.commandNum--;
			}
		}
		if(event.getCode()==D){
			if(ui.commandNum<2) {
				ui.commandNum++;
			}
		}
		if(event.getCode()==ENTER){
			if(ui.commandNum==0) {
				isAlive = true;
				FPS = 0;
				currentLevel = 0;
				init();
				gameState = titleState;
			}
			if(ui.commandNum==1) {
				Stage stg = (Stage) canvas.getScene().getWindow();
				stg.close();
			}
		}
	}

	public void handleTitle(KeyEvent event){
		if(event.getCode()==W){
			if(ui.commandNum>0) {
				ui.commandNum--;
			}else{
				ui.commandNum=2;
			}
		}
		if(event.getCode()==S){
			if(ui.commandNum<2) {
				ui.commandNum++;
			}else{
				ui.commandNum=0;
			}
		}
		if(event.getCode()==ENTER){
			if(ui.commandNum==0) {
				gameState = playState;
			}
			if(ui.commandNum==1) {
				gameState = playState;
			}
			if(ui.commandNum==2) {
				Stage stg = (Stage) canvas.getScene().getWindow();
				stg.close();
			}
		}
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
			Platform.runLater(()->{
				repaint();
			});
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
		try {
			if (gameState == playState) {
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
					ene.update();
					double distance = Math.sqrt(
							Math.pow(ene.pos.getX() - avatar.pos.getX(), 2) +
									Math.pow(ene.pos.getY() - avatar.pos.getY(), 2)
					);
					if (distance < 40) {
						if (avatar.isAttacking()) {
							level.getEnemies().get(i).isAlive = false;
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
						if (distance < 30 && !level.getBullets().get(i).enemy) {
							level.getBullets().remove(i);
							en.setHealth(en.getHealth() - 1); // Resta 1 a la salud del enemigo
							if (en.getHealth() <= 0) {
								level.getEnemies().remove(j); // Elimina el enemigo si su salud es menor o igual a 0
							}
							break;
						}

					}
				}
				for (int i = 0; i < level.getBullets().size(); i++) {
					Bullet bullet = level.getBullets().get(i);
					if (bullet.enemy) {
						double distance = Math.sqrt(
								Math.pow(bullet.pos.getX() - avatar.pos.getX(), 2) +
										Math.pow(bullet.pos.getY() - avatar.pos.getY(), 2)
						);

						if (distance < 30) {
							level.getBullets().remove(i);  // Elimina la bala del enemigo
							avatar.setCurrentLives(avatar.getCurrentLives() - 3);  // Resta una vida al avatar
							break;  // Sale del bucle para evitar índices inválidos después de eliminar la bala
						}
					}
				}
				for (int i = 0; i < level.getEnemies().size(); i++) {
					Enemy currentEnemy = level.getEnemies().get(i);
					currentEnemy.update();

					// Comprobar colisiones con otros enemigos
					for (int j = i + 1; j < level.getEnemies().size(); j++) {
						Enemy otherEnemy = level.getEnemies().get(j);
						double distance = Math.sqrt(
								Math.pow(currentEnemy.pos.getX() - otherEnemy.pos.getX(), 2) +
										Math.pow(currentEnemy.pos.getY() - otherEnemy.pos.getY(), 2)
						);
						if (distance < 30) {
							// Hay colisión entre los enemigos, ajusta sus posiciones
							Vector direction = currentEnemy.pos.subtract(otherEnemy.pos).normalize2();
							double overlap = 40 - distance;
							double displacementX = direction.getX() * overlap / 2;
							double displacementY = direction.getY() * overlap / 2;

							currentEnemy.pos.setX(currentEnemy.pos.getX() + displacementX);
							currentEnemy.pos.setY(currentEnemy.pos.getY() + displacementY);
							otherEnemy.pos.setX(otherEnemy.pos.getX() - displacementX);
							otherEnemy.pos.setY(otherEnemy.pos.getY() - displacementY);
						}
					}
				}

				avatar.update();
			}
		}catch (Exception e){
			e.printStackTrace();
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
		if(gameState==gameOverState){
			ui.draw(gc);
		}
		if(gameState==pauseState){
			ui.draw(gc);
		}
		if(gameState==titleState){
			ui.draw(gc);
		}
	}
}






