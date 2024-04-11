module com.app.prospectdeals {
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.app.prospectdeals to javafx.fxml;
    exports com.app.prospectdeals;
}