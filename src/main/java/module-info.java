module com.example.ce216project {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.ce216project to javafx.fxml;
    exports com.example.ce216project;
}