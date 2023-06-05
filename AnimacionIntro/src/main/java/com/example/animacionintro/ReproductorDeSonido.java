package com.example.animacionintro;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ReproductorDeSonido implements Runnable {
    private String archivoSonido;
    private int timesPlayed = 0;
    File archivo;
    public ReproductorDeSonido(String archivoSonido) {
        this.archivoSonido = archivoSonido;
        archivo = new File(archivoSonido);
    }

    public void run() {
        try {
            // Obtener el archivo de sonido


            // Crear un flujo de entrada de audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);

            // Crear un clip de sonido
            Clip clip = AudioSystem.getClip();

            // Abrir el clip con el flujo de audio
            clip.open(audioStream);
            if(timesPlayed==0){
                // Establecer el control de volumen del clip
                FloatControl controlVolumen = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                // Establecer el volumen en decibelios (-80.0 a 6.0206)
                float volumen = -80.0f; // Ajusta el volumen aquí
                controlVolumen.setValue(volumen);
            }else{
                // Establecer el control de volumen del clip
                FloatControl controlVolumen = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                // Establecer el volumen en decibelios (-80.0 a 6.0206)
                float volumen = -10.0f; // Ajusta el volumen aquí
                controlVolumen.setValue(volumen);
            }
            timesPlayed++;
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