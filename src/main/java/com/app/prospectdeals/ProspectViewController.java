package com.app.prospectdeals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ProspectViewController {
    Stage primaryStage;
    File selectedDir;

    @FXML
    TableView<Directory> tableView;

    @FXML
    Button addAnother;
    @FXML
    Button prospect;

    private ObservableList<Directory> directoryList = FXCollections.observableArrayList();

    public void initData(File selectedDir, Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.selectedDir = selectedDir;

        Directory dir = new Directory();
        dir.setName(selectedDir.getName());

        List<Path> files = Files.list(Paths.get(selectedDir.getAbsolutePath())).toList();
        int numberOfFiles = 0;

        for (Path file : files) {
            List<String> fileSplit = List.of(file.getFileName().toString().split("\\."));
            if (!fileSplit.isEmpty()) {
                String exec = fileSplit.get(fileSplit.size() - 1);

                if (exec.equals("xlsx")) {
                    numberOfFiles++;
                }
            }
        }

        dir.setNumberOfFiles(numberOfFiles);

        // Add dir to the ObservableList
        directoryList.add(dir);

        // Bind the ObservableList to the TableView
        tableView.setItems(directoryList);
    }

    // Initialize method where you set up your TableView columns
    @FXML
    private void initialize() {
        TableColumn<Directory, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Directory, Integer> numberOfFilesColumn = new TableColumn<>("Number of Files");
        numberOfFilesColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfFiles"));

        tableView.getColumns().addAll(nameColumn, numberOfFilesColumn);
    }

    // Other methods...

    public void onProspect(ActionEvent e) {
        
    }

    public void onAddFolder(ActionEvent e) {
        // Create a directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Directory");

        // Show the directory chooser dialog
        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            try {
                // Create a new Directory object for the selected directory
                Directory newDir = new Directory();
                newDir.setName(selectedDirectory.getName());

                // Count the number of Excel files in the selected directory
                int numberOfFiles = (int) Files.list(selectedDirectory.toPath())
                        .filter(path -> path.toString().toLowerCase().endsWith(".xlsx"))
                        .count();
                newDir.setNumberOfFiles(numberOfFiles);

                // Add the new directory to the table view
                directoryList.add(newDir);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
