module module.name {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.proyecto;
    opens com.proyecto to javafx.fxml;
    requires transitive javafx.graphics;
}
