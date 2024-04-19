package com.app.prospectdeals;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainViewController {
    public Stage primaryStage;

    @FXML
    private ImageView imageView;

    @FXML
    private Button selectFilesButton;

    @FXML
    private void handleSelectFilesButtonClick(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select excel files");

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

        if (selectedFiles != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("prospect-view.fxml"));
            Parent root = loader.load();

            ProspectViewController prospectViewController = loader.getController();
            prospectViewController.initData(selectedFiles, primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            System.out.println("No Folders selected.");
        }
    }

    public void initialize() {
        Image image = new Image(getClass().getResourceAsStream("/images/folder.png"));
        imageView.setImage(image);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}