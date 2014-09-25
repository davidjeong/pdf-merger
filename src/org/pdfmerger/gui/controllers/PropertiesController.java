package org.pdfmerger.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

/**
 * Controller class for the properties.fxml.
 * @author David Jeong
 *
 */
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
	
	/**
	 * Method for tool tip generation.
	 */
	private void createTooltips() {
		fileName.setTooltip(new Tooltip(fileName.textProperty().getValue()));
		filePath.setTooltip(new Tooltip(filePath.textProperty().getValue()));
		fileSize.setTooltip(new Tooltip(fileSize.textProperty().getValue()));
		lastModified.setTooltip(new Tooltip(lastModified.textProperty().getValue()));
		created.setTooltip(new Tooltip(created.textProperty().getValue()));
		accessed.setTooltip(new Tooltip(accessed.textProperty().getValue()));
	}
	
	/**
	 * Method which populates the properties window.
	 * @param fileName {@link String} value of the file name.
	 * @param filePath {@link String} value of the file path.
	 * @param fileSize {@link long} value of the file size, in bytes.
	 * @param lastModified {@link String} representation of the last modified date.
	 * @param created {@link String} representation of the file creation date.
	 * @param accessed {@link String} representation of the last accessed date.
	 */
	public void populateFields(String fileName, String filePath, String fileSize, String lastModified, String created, String accessed) {
		this.fileName.textProperty().setValue(filePath);
		this.filePath.textProperty().setValue(filePath);
		this.fileSize.textProperty().setValue(fileSize + " bytes");
		this.lastModified.textProperty().setValue(lastModified);
		this.created.textProperty().setValue(created);
		this.accessed.textProperty().setValue(accessed);
		createTooltips();
	}

}
