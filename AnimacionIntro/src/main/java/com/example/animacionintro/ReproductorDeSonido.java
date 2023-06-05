package com.example.animacionintro;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ReproductorDeSonido implements Runnable {
    private String archivoSonido;

    public ReproductorDeSonido(String archivoSonido) {
        this.archivoSonido = archivoSonido;
    }

    public void run() {
        try {
            // Obtener el archivo de sonido
            File archivo = new File(archivoSonido);

            // Crear un flujo de entrada de audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);

            // Crear un clip de sonido
            Clip clip = AudioSystem.getClip();

            // Abrir el clip con el flujo de audio
            clip.open(audioStream);

            // Reproducir el sonido
            clip.start();

            // Esperar hasta que se termine de reproducir
           /* while (!clip.isRunning()) {
                Thread.sleep(10);
            }

            while (clip.isRunning()) {
                Thread.sleep(10);
            }*/

            // Cerrar el clip y el flujo de audio
            //clip.close();
            //audioStream.close();

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}