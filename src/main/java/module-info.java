module org.example.lab1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    opens org.example.lab1 to javafx.fxml;
    exports org.example.lab1;
}