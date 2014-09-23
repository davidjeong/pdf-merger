package org.pdfmerger.gui.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.pdfmerger.gui.Constants;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Main controller for the main.fxml
 * @author David Jeong
 */
public class MainController implements Initializable {

	@FXML private GridPane gridPane;
	@FXML private Button generate;
	@FXML private Label fileName1;
	@FXML private Label fileName2;
	@FXML private Button select1;
	@FXML private Button select2;
	
	private FileChooser fc;
	
	private File file1;
	private File file2;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		generateFileChooser();
	}
	
	private void generateFileChooser() {
		fc = new FileChooser();
		fc.setTitle(Constants.FILE_CHOOSER_TITLE);
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(Constants.PDF, Constants.PDF_EXTENSION));
	}
	
	public void createHandlers(Stage stage) {
		
		select1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				file1 = fc.showOpenDialog(stage);
				if (file1 != null) {
					fileName1.textProperty().setValue(file1.getName());
					System.out.println(file1.getPath());
				}
			}
		});
		
		select2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				file2 = fc.showOpenDialog(stage);
				if (file1 != null) {
					fileName2.textProperty().setValue(file2.getName());
					System.out.println(file2.getPath());
				}
			}
		});
		
		generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("generate clicked.");
				generatePDF();
				event.consume();
			}
		});
	}
	
	private void generatePDF() {
		if (file1 == null && file2 == null) {
			System.out.println("Both empty");
			return;
		}
		else if (file1 == null) {
			//file 1 is eimpty
			return;
		}
		else if (file2 == null) {
			return;	
		}
	}

}
