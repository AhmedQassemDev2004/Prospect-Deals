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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ProspectViewController {
    Stage primaryStage;
    List<File> selectedFiles;

    @FXML
    TableView<File> tableView;

    @FXML
    Button addFiles;
    @FXML
    Button prospect;
    @FXML
    TextField outputPath;

    @FXML
    TextField additionalText;

    Config config = new Config();

    private ObservableList<File> files = FXCollections.observableArrayList();

    public void initData(List<File> selectedFiles, Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.selectedFiles = selectedFiles;

        this.files.addAll(selectedFiles);

        tableView.setItems(this.files);
    }

    @FXML
    private void initialize() {
        TableColumn<File, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableView.getColumns().addAll(nameColumn);

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

        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".xlsx")) {

                if (!headerAdded) {
                    try (Workbook workbook = WorkbookFactory.create(file)) {
                        Sheet sheet = workbook.getSheetAt(0);

                        Row headerRow = sheet.getRow(0);
                        CellStyle oldCellStyle = null;
                        if (headerRow != null) {
                            Row newHeaderRow = newSheet.createRow(newRowNum++);
                            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                                Cell oldCell = headerRow.getCell(i);
                                Cell newCell = newHeaderRow.createCell(i);
                                if (oldCell != null) {
                                    newCell.setCellValue(oldCell.getStringCellValue());

                                    oldCellStyle = oldCell.getCellStyle();
                                    CellStyle newCellStyle = newWorkbook.createCellStyle();
                                    newCellStyle.cloneStyleFrom(oldCellStyle);
                                    newCell.setCellStyle(newCellStyle);
                                }
                            }
                            Cell baseFileHeader = newHeaderRow.createCell(headerRow.getLastCellNum());
                            baseFileHeader.setCellValue("Base File");
                            if (oldCellStyle != null) {
                                // Clone the style from oldCellStyle and apply it to baseFileHeader
                                CellStyle newCellStyle = newWorkbook.createCellStyle();
                                newCellStyle.cloneStyleFrom(oldCellStyle);
                                baseFileHeader.setCellStyle(newCellStyle);
                            }
                        }

                        headerAdded = true;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                try (Workbook workbook = WorkbookFactory.create(file)) {
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

                                    Cell baseFileCell = newRow.createCell(10);
                                    baseFileCell.setCellValue(file.getName());
                                }
                            }
                        }
                    }
                } catch (IOException | EncryptedDocumentException ex) {
                    ex.printStackTrace();
                }
            }
        }

        for (int colIndex = 0; colIndex < newSheet.getRow(0).getLastCellNum(); colIndex++) {
            newSheet.autoSizeColumn(colIndex);
        }

        String resultName;
        if (additionalText.getText().isEmpty()) {
            resultName = "/Prospect Sheet -- " + additionalText.getText() + " -- " + (LocalDateTime.now().toString().replace(':', '@')) + ".xlsx";
        } else {
            resultName = "/Prospect Sheet -- " + (LocalDateTime.now().toString().replace(':', '@')) + ".xlsx";
        }


        try (FileOutputStream outputStream = new FileOutputStream(outputPath.getText() + resultName)) {
            newWorkbook.write(outputStream);
            newWorkbook.close();

            // Navigate to MainView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();
            MainViewController controller = loader.getController();
            controller.initialize();

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            controller.setPrimaryStage(primaryStage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void onAddFiles(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel Files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

        if (selectedFiles != null) {
            this.files.addAll(selectedFiles);
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
