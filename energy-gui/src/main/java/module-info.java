module at.technikum.energygui {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.technikum.energygui to javafx.fxml;
    exports at.technikum.energygui;
}