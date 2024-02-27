module com.example.graphicaltravelingtoolsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.graphicaltravelingtoolsystem to javafx.fxml;
    exports com.example.graphicaltravelingtoolsystem;
}