package org.pdfmerger.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.pdfmerger.gui.Constants;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
	@FXML private ProgressBar progress;

	private FileChooser fc;

	private File file1;
	private File file2;

	private File destinationFile;
	private boolean generated;
	private Task<Boolean> service;
	private boolean running = false;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		generateFileChooser();
	}

	private void generateFileChooser() {
		fc = new FileChooser();
		fc.setTitle(Constants.FILE_CHOOSER_TITLE);
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(Constants.PDF, Constants.PDF_EXTENSION));
	}

	/**
	 * Method which creates the handlers needed by the controller.
	 * @param stage {@link Stage} for the main window.
	 */
	public void createHandlers(Stage stage) {
		
		select1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				file1 = fc.showOpenDialog(stage);
				if (file1 != null) {
					fileName1.textProperty().setValue(file1.getName());
					if (generated) {
						generated = false;
						generate.textProperty().setValue(Constants.PDF_GENERATE);
					}
				}
			}
		});

		select2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				file2 = fc.showOpenDialog(stage);
				if (file2 != null) {
					fileName2.textProperty().setValue(file2.getName());
					if (generated) {
						generated = false;
						generate.textProperty().setValue(Constants.PDF_GENERATE);
					}
				}
			}
		});

		generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (checkValid()) {
					destinationFile = fc.showSaveDialog(stage);
					if (destinationFile != null) {
						service = createTask();
						running = true;
						progress.progressProperty().bind(service.progressProperty());
						new Thread(service).start();
					}
				}
				event.consume();
			}
		});
	}
	
	/**
	 * Disable all buttons, preventing user from clicking.
	 */
	private void disableButtons() {
		select1.setDisable(true);
		select2.setDisable(true);
		generate.setDisable(true);
	}
	
	/**
	 * Enable all buttons, to allow user to click.
	 */
	private void enableButtons() {
		select1.setDisable(false);
		select2.setDisable(false);
		generate.setDisable(false);
	}

	private boolean checkValid() {
		return (file1 != null && file2 != null);
	}

	/**
	 * Method which generates the PDF.
	 */
	private void generatePDF() {
		PDFMergerUtility utility = new PDFMergerUtility();
		utility.addSource(file1);
		utility.addSource(file2);
		utility.setDestinationFileName(destinationFile.getPath());
		try {
			utility.mergeDocuments();
		}
		catch (IOException ie) {
			System.out.println("Caught IOException");
		}
		catch (COSVisitorException cve) {
			System.out.println("Caught COSVistorException");
		}
	}
	
	/**
	 * Create a {@link Task} which generates the merged PDF file.
	 * @return a {@link Boolean} value of true, if completed.
	 */
	public Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				while (running) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							disableButtons();
							generate.textProperty().setValue(Constants.PDF_IN_PROGRESS);
						}
					});
					generatePDF();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							progress.progressProperty().unbind();
							progress.setProgress(0.0);
							generate.textProperty().setValue("PDF Merged!");
							enableButtons();
						}
					});
					running = false;
					generated = true;
				}
				return true;
			}
		};
	}
}
