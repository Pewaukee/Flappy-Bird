module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // to import custom font
    // https://stackoverflow.com/questions/51503140/the-import-java-awt-cannot-be-resolved

    opens com.example to javafx.fxml;
    exports com.example;
}
