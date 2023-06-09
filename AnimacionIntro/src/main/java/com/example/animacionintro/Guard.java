package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Random;

public class Guard extends Enemy {
    private Vector patrolPoint1;
    private Vector patrolPoint2;
    private Vector currentPatrolPoint;
    private Avatar avatar;
    private Level level;
    private double speed = 1.0; // Velocidad de movimiento del Guardia
    private ReproductorDeSonido reproductorDeSonido = new ReproductorDeSonido(System.getProperty("user.dir")+"/AnimacionIntro/src/main/resources/audio/disparo.wav");

    public int actionLockCounter = 0;

    public Guard(Vector position, int health, Avatar avatar, Level level, HelloController gp) {
        super(position, health, gp);
        solidArea = new Colission(world, 30, 30);
        new Thread(reproductorDeSonido).start();
        this.currentPatrolPoint = patrolPoint1;
        this.avatar = avatar;
        this.level = level;
        getSGuardImages();
    }
    private Image[] idle;
    private int frame;


    public void getSGuardImages(){
        idle = new Image[4];
        for(int i=1 ; i<=4   ; i++) {
            String uri = "file:" + HelloApplication.class.getResource("enemy/hongo"+i+".png").getPath();
            idle[i-1] = new Image(uri);
        }
    }

    public void setAction(){

        actionLockCounter ++;
        if(actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;

            if (i <= 25) {
                direction = "up";
            }
            if (i > 25 && i <= 50) {
                direction = "down";
            }
            if (i > 50 && i <= 75) {
                direction = "left";
            }
            if (i > 75 && i <= 100) {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }

    @Override
    public void update() {
        // Lógica de actualización específica para el Guardia
        setAction();
        collisionOn= false;
        gp.cChecker.checkTile(this);
        if(collisionOn==false){
            switch (direction){
                case "up": world.setY(world.getY()-speed); break;
                case "down": world.setY(world.getY()+speed); break;
                case "left": world.setX(world.getX()-speed); break;
                case "right": world.setX(world.getX()+speed); break;

            }
        }

            double distanceToPlayer = Math.sqrt(
                    Math.pow(avatar.world.getX() - world.getX(), 2) +
                            Math.pow(avatar.world.getY() - world.getY(), 2)
            );

            if (distanceToPlayer < 150) {
                // Disparar al jugador
                shootPlayer();
            }

    }
    private boolean isShooting = false;

    private void shootPlayer() {
        if (isShooting) {
            return; // Evitar llamadas repetidas mientras se está generando una bala
        }

        isShooting = true; // Establecer el estado de generación de balas como verdadero

        // Crear un nuevo hilo para generar las balas
        Thread shootThread = new Thread(() -> {
            // Generar una nueva bala y agregarla al nivel
            double screenX = world.getX() - gp.avatar.world.getX() + gp.avatar.pos.getX();
            double screenY = world.getY() - gp.avatar.world.getY() + gp.avatar.pos.getY(); ;
            Bullet bullet = new Bullet(new Vector(screenX, screenY), getDirectionToPlayer(), true);
            level.getBullets().add(bullet);
            new Thread(reproductorDeSonido).start();

            // Esperar un tiempo antes de generar la siguiente bala
            try {
                Thread.sleep(1450); // Tiempo de espera entre generación de balas (1 segundo)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isShooting = false; // Restablecer el estado de generación de balas como falso
        });

        // Iniciar el hilo de generación de balas
        shootThread.start();
    }

    private Vector getDirectionToPlayer() {
        // Calcular la dirección hacia el jugador
        double screenX = world.getX() - gp.avatar.world.getX() + gp.avatar.pos.getX();
        double screenY = world.getY() - gp.avatar.world.getY() + gp.avatar.pos.getY(); ;
        double dx =  avatar.pos.getX()- screenX;
        double dy =   avatar.pos.getY()-screenY;
        return new Vector(dx, dy).normalize2();
    }

    private boolean shouldChangePatrolPoint() {
        double distance = world.distanceTo(currentPatrolPoint);
        return distance <= 5; // Cambiar de punto de patrulla cuando se está cerca del punto actual
    }

    private void changePatrolPoint() {
        if (currentPatrolPoint.equals(patrolPoint1)) {
            currentPatrolPoint = patrolPoint2;
        } else {
            currentPatrolPoint = patrolPoint1;
        }
    }

    private void patrol() {
        if (isShooting) {
            return; // Si está disparando, no realizar movimientos
        }
        Vector direction = currentPatrolPoint.subtract(world);
        direction.normalize();
        world.setX(world.getX() + direction.getX() * getSpeed());
        world.setY(world.getY() + direction.getY() * getSpeed());
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void draw(GraphicsContext gc) {
        double screenX = world.getX() - gp.avatar.world.getX() + gp.avatar.pos.getX();
        double screenY = world.getY() - gp.avatar.world.getY() + gp.avatar.pos.getY(); ;
        gc.drawImage(idle[frame], screenX, screenY, 30, 30);
    }

    @Override
    public void run() {
        // Segundo plano
        while (isAlive) {
            frame = (frame + 1) % 4;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
