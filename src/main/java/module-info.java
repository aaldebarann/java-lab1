module org.example.lab1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires org.slf4j;

    opens org.example.lab1 to javafx.fxml;
    exports org.example.lab1;
}