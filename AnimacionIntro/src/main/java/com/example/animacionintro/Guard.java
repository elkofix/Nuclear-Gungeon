package com.example.animacionintro;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Guard extends Enemy {
    private boolean isAlive = true;
    private Vector patrolPoint1;
    private Vector patrolPoint2;
    private Vector currentPatrolPoint;
    private Avatar avatar;
    private Level level;
    private double speed = 1.0; // Velocidad de movimiento del Guardia

    public Guard(Vector position, int health, Vector patrolPoint1, Vector patrolPoint2, Avatar avatar, Level level) {
        super(position, health);
        this.patrolPoint1 = patrolPoint1;
        this.patrolPoint2 = patrolPoint2;
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
            String uri = "file:" + HelloApplication.class.getResource("enemy/fantasma"+i+".png").getPath();
            idle[i-1] = new Image(uri);
        }
    }

    @Override
    public void update() {
        // Lógica de actualización específica para el Guardia
        if (shouldChangePatrolPoint()) {
            changePatrolPoint();
        }
        patrol();
        double distanceToPlayer = Math.sqrt(
                Math.pow(avatar.pos.getX() - pos.getX(), 2) +
                        Math.pow(avatar.pos.getY() - pos.getY(), 2)
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
            Bullet bullet = new Bullet(pos.clone(), getDirectionToPlayer(), true);
            level.getBullets().add(bullet);

            // Esperar un tiempo antes de generar la siguiente bala
            try {
                Thread.sleep(1000); // Tiempo de espera entre generación de balas (1 segundo)
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
        double dx = avatar.pos.getX() - pos.getX();
        double dy = avatar.pos.getY() - pos.getY();
        return new Vector(dx, dy).normalize2();
    }

    private boolean shouldChangePatrolPoint() {
        double distance = pos.distanceTo(currentPatrolPoint);
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
        Vector direction = currentPatrolPoint.subtract(pos);
        direction.normalize();
        pos.setX(pos.getX() + direction.getX() * getSpeed());
        pos.setY(pos.getY() + direction.getY() * getSpeed());
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(idle[frame], pos.getX(), pos.getY(), 30, 30);
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
