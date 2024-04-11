package com.app.prospectdeals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.poi.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProspectViewController {
    Stage primaryStage;
    File selectedDir;

    List<Directory> dirs;

    @FXML
    TableView<Directory> tableView;

    @FXML
    Button addAnother;
    @FXML
    Button prospect;
    @FXML
    TextField outputPath;

    Config config = new Config();

    private ObservableList<Directory> directoryList = FXCollections.observableArrayList();

    public void initData(File selectedDir, Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.selectedDir = selectedDir;

        Directory dir = new Directory();
        dir.setName(selectedDir.getName());

        List<Path> files = Files.list(Paths.get(selectedDir.getAbsolutePath())).toList();

        int numberOfFiles = (int) Files.list(selectedDir.toPath())
                .filter(path -> path.toString().toLowerCase().endsWith(".xlsx"))
                .count();

        dir.setNumberOfFiles(numberOfFiles);

        dir.setPath(selectedDir.getAbsolutePath());

        directoryList.add(dir);

        tableView.setItems(directoryList);
    }

    @FXML
    private void initialize() {
        TableColumn<Directory, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Directory, Integer> numberOfFilesColumn = new TableColumn<>("Number of Files");
        numberOfFilesColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfFiles"));

        tableView.getColumns().addAll(nameColumn, numberOfFilesColumn);

        this.outputPath.setText(config.OUTPUT_PATH);
    }

    public void onProspect(ActionEvent e) {
        if (this.outputPath.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Select an output path").show();
            return;
        }

        Workbook newWorkbook = new XSSFWorkbook();
        Sheet newSheet = newWorkbook.createSheet("Filtered Rows");
        int newRowNum = 0;
        boolean headerAdded = false;

        for (Directory dir : directoryList) {
            File[] excelFiles = new File(dir.getPath())
                    .listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".xlsx"));

            if (excelFiles != null && Arrays.stream(excelFiles).findAny().isPresent()) {

                if (!headerAdded) {
                    try (Workbook workbook = WorkbookFactory.create(excelFiles[0])) {
                        Sheet sheet = workbook.getSheetAt(0);

                        Row headerRow = sheet.getRow(0);
                        if (headerRow != null) {
                            Row newHeaderRow = newSheet.createRow(newRowNum++);
                            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                                Cell oldCell = headerRow.getCell(i);
                                Cell newCell = newHeaderRow.createCell(i);
                                if (oldCell != null) {
                                    newCell.setCellValue(oldCell.getStringCellValue());

                                    CellStyle oldCellStyle = oldCell.getCellStyle();
                                    CellStyle newCellStyle = newWorkbook.createCellStyle();
                                    newCellStyle.cloneStyleFrom(oldCellStyle);
                                    newCell.setCellStyle(newCellStyle);
                                }
                            }
                        }

                        headerAdded = true;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                for (File excelFile : excelFiles) {
                    try (Workbook workbook = WorkbookFactory.create(excelFile)) {
                        Sheet sheet = workbook.getSheetAt(0);

                        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                            Row row = sheet.getRow(rowIndex);
                            if (row != null) {
                                Cell cell = row.getCell(6);
                                if (cell != null && cell.getCellType() == CellType.STRING) {
                                    String value = cell.getStringCellValue();
                                    if ("CB".equalsIgnoreCase(value) || "PR".equalsIgnoreCase(value)) {
                                        Row newRow = newSheet.createRow(newRowNum++);
                                        for (int i = 0; i < 10; i++) {
                                            Cell oldCell = row.getCell(i);
                                            Cell newCell = newRow.createCell(i);
                                            if (oldCell != null) {
                                                switch (oldCell.getCellType()) {
                                                    case STRING:
                                                        newCell.setCellValue(oldCell.getStringCellValue());
                                                        break;
                                                    case NUMERIC:
                                                        newCell.setCellValue(oldCell.getNumericCellValue());
                                                        break;
                                                    case BOOLEAN:
                                                        newCell.setCellValue(oldCell.getBooleanCellValue());
                                                        break;
                                                    case FORMULA:
                                                        newCell.setCellFormula(oldCell.getCellFormula());
                                                        break;
                                                    default:
                                                        break;
                                                }

                                                CellStyle oldCellStyle = oldCell.getCellStyle();
                                                CellStyle newCellStyle = newWorkbook.createCellStyle();
                                                newCellStyle.cloneStyleFrom(oldCellStyle);
                                                newCell.setCellStyle(newCellStyle);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException | EncryptedDocumentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(outputPath.getText() + "/Prospect Sheet.xlsx")) {
            newWorkbook.write(outputStream);
            newWorkbook.close();

            // Navigate to MainView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();
            MainViewController controller = loader.getController();
            controller.initialize();

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void onAddFolder(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Directory");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            try {
                Directory newDir = new Directory();
                newDir.setName(selectedDirectory.getName());

                int numberOfFiles = (int) Files.list(selectedDirectory.toPath())
                        .filter(path -> path.toString().toLowerCase().endsWith(".xlsx"))
                        .count();
                newDir.setNumberOfFiles(numberOfFiles);

                newDir.setPath(selectedDirectory.getAbsolutePath());
                directoryList.add(newDir);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onChangeOutputPath(ActionEvent e) {
        var directoryChooser = new DirectoryChooser();

        File dir = directoryChooser.showDialog(primaryStage);

        if (dir != null) {
            this.config.OUTPUT_PATH = dir.getAbsolutePath();
            this.outputPath.setText(this.config.OUTPUT_PATH);
            this.config.save();
        }
    }

}
