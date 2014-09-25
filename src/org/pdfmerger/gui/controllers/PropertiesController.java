package org.pdfmerger.gui.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class PropertiesController implements Initializable {

	@FXML private Label fileName;
	@FXML private Label filePath;
	@FXML private Label fileSize;
	@FXML private Label lastModified;
	@FXML private Label created;
	@FXML private Label accessed;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void populateFields(File file) {
		
		
		
		
		
		
		/*this.fileName.textProperty().setValue(filePath);
		this.filePath.textProperty().setValue(filePath);
		this.fileSize.textProperty().setValue(fileSize + " bytes");
		this.lastModified.textProperty().setValue(lastModified);
		this.created.textProperty().setValue(created);
		this.accessed.textProperty().setValue(accessed);*/
	}

}
