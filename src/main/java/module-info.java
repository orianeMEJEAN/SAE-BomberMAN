module com.example.BomberMAN {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    opens com.example.BomberMAN to javafx.fxml;
    exports com.example.BomberMAN;
    exports com.example.BomberMAN.GamePlay;
    opens com.example.BomberMAN.GamePlay to javafx.fxml;
}