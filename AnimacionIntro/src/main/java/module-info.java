module com.example.animacionintro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.animacionintro to javafx.fxml;
    exports com.example.animacionintro;

}