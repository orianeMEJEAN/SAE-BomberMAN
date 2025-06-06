module com.example.BomberMAN {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens com.example.BomberMAN to javafx.fxml;
    exports com.example.BomberMAN;
    exports com.example.BomberMAN.GamePlay;
    opens com.example.BomberMAN.GamePlay to javafx.fxml;
    opens com.example.BomberMAN.menu to javafx.fxml;
    exports com.example.BomberMAN.menu;
}