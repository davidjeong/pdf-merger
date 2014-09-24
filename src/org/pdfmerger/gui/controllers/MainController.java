package org.pdfmerger.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.pdfmerger.gui.Constants;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Main controller for the main.fxml
 * @author David Jeong
 */
public class MainController implements Initializable {

	@FXML private AnchorPane gui;
	@FXML private GridPane gridPane;
	@FXML private ListView<File> listView;
	@FXML private Button generate;
	@FXML private Button selectButton;
	@FXML private Button up;
	@FXML private Button down;
	@FXML private ProgressBar progress;

	private FileChooser fc;

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
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(Constants.PDF, Constants.PDF_EXTENSION));
	}

	/**
	 * Method which creates the handlers needed by the controller.
	 * @param stage {@link Stage} for the main window.
	 */
	public void createHandlers(Stage stage) {
		
		listView.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
			@Override
			public ListCell<File> call(ListView<File> param) {
				return new ListCell<File>() {
					@Override
					protected void updateItem(File item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.getName());
						}
					}
				};
			}
		});
		
		gui.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasFiles()) {
					event.acceptTransferModes(TransferMode.ANY);
				} else {
					event.consume();
				}
			}
		});
		
		gui.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasFiles()) {
					success = true;
					listView.getItems().addAll(db.getFiles());
				}
				event.setDropCompleted(success);
				event.consume();
			}
		});
		
		selectButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				fc.setTitle(Constants.FILE_CHOOSER_OPEN);
				List<File> tempList = fc.showOpenMultipleDialog(stage);
				if (tempList != null) {
					listView.getItems().addAll(tempList);
					if (generated) {
						generated = false;
						generate.textProperty().setValue(Constants.PDF_GENERATE);
					}
				}
				event.consume();
			}
		});
		
		up.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int selected = listView.getSelectionModel().getSelectedIndex();
				if (selected != 0) {
					File tmp = listView.getItems().get(selected - 1);
					listView.getItems().set(selected - 1, listView.getSelectionModel().getSelectedItem());
					listView.getItems().set(selected, tmp);
					listView.getSelectionModel().select(selected - 1);
				}
				event.consume();
			}
		});
		
		down.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int selected = listView.getSelectionModel().getSelectedIndex();
				if (selected != listView.getItems().size() - 1) {
					File tmp = listView.getItems().get(selected + 1);
					listView.getItems().set(selected + 1, listView.getSelectionModel().getSelectedItem());
					listView.getItems().set(selected, tmp);
					listView.getSelectionModel().select(selected + 1);
				}
				event.consume();
			}
		});

		generate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (checkValid()) {
					fc.setTitle(Constants.FILE_CHOOSER_SAVE);
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
		selectButton.setDisable(true);
		generate.setDisable(true);
	}
	
	/**
	 * Enable all buttons, to allow user to click.
	 */
	private void enableButtons() {
		selectButton.setDisable(false);
		generate.setDisable(false);
	}

	private boolean checkValid() {
		return (!listView.getItems().isEmpty());
	}

	/**
	 * Method which generates the PDF.
	 */
	private void generatePDF() {
		PDFMergerUtility utility = new PDFMergerUtility();
		for (File file : listView.getItems()) {
			utility.addSource(file);
		}
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
							generate.textProperty().setValue(Constants.PDF_MERGED);
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
