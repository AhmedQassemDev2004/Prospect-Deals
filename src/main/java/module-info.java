module com.app.prospectdeals {
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.app.prospectdeals to javafx.fxml;
    exports com.app.prospectdeals;
}