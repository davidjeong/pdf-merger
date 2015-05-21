package org.pdfmerger.gui.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.pdfmerger.gui.Constants;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
	@FXML private ObservableList<File> fileList;
	
	private FileChooser fc;

	private File destinationFile;
	private boolean generated;
	private Task<Boolean> service;
	private boolean running = false;

	private ContextMenu context;
	private MenuItem remove;
	private MenuItem properties;
	
	private BasicFileAttributes attr;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		generateFileChooser();
	}

	/**
	 * Method which generates and set up the file chooser.
	 */
	private void generateFileChooser() {
		fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(Constants.PDF, Constants.PDF_EXTENSION));
	}

	/**
	 * Method which creates the handlers needed by the controller.
	 * @param stage {@link Stage} for the main window.
	 */
	public void createHandlers(final Stage stage) {

		fileList.addListener(new ListChangeListener<File>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends File> c) {
				if (fileList.size() == 0) {
					generate.setDisable(true);
				}
				else {
					generate.setDisable(false);
				}
			}
		});
		
		listView.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
			@Override
			public ListCell<File> call(ListView<File> param) {
				return new ListCell<File>() {
					@Override
					protected void updateItem(File item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setText(null);
							setGraphic(null);
						}
						else {
							this.setOnMouseClicked(new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent event) {
									if (event.getButton().equals(MouseButton.SECONDARY)) {
										context = new ContextMenu();
										context.setX(event.getScreenX());
										context.setY(event.getScreenY());

										remove = new MenuItem();
										remove.textProperty().setValue(Constants.REMOVE);
										remove.setOnAction(new EventHandler<ActionEvent>() {
											@Override
											public void handle(ActionEvent event) {
												fileList.remove(listView.getSelectionModel().getSelectedIndex());
												listView.getSelectionModel().clearSelection();
												event.consume();
											}
										});

										properties = new MenuItem();
										properties.textProperty().setValue(Constants.PROPERTIES);
										properties.setOnAction(new EventHandler<ActionEvent>() {
											@Override
											public void handle(ActionEvent event) {
												FXMLLoader loader = null;
												Parent root = null;
												try {
													loader = new FXMLLoader(this.getClass().getResource("../views/properties.fxml"));
													root = (Parent)loader.load();

													PropertiesController controller = (PropertiesController)loader.getController();
													
													attr = Files.readAttributes(item.toPath(), BasicFileAttributes.class);
													
													String fileName = item.getName();
													String filePath = item.getPath();
													String fileSize = String.valueOf(item.length());
													String lastModified = attr.lastModifiedTime().toString();
													String created = attr.creationTime().toString();
													String accessed = attr.lastAccessTime().toString();
													
													controller.populateFields(fileName, filePath, fileSize, lastModified, created, accessed);

													Scene window = new Scene(root);
													Stage stage = new Stage();
													
													if (Constants.OPERATING_SYSTEM.startsWith("Windows")) {
														stage.initStyle(StageStyle.UTILITY);
													}
													
													stage.setResizable(false);
													stage.setScene(window);
													stage.show();

												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										});

										context.getItems().addAll(remove, properties);
										context.show(stage);
									}
								}
							});

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
					fileList.addAll(db.getFiles());
					if (generated) {
						generated = false;
						generate.textProperty().setValue(Constants.PDF_GENERATE);
					}
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
					fileList.addAll(tempList);
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
				if (selected != -1) {
					if (generated) {
						generated = false;
						generate.textProperty().setValue(Constants.PDF_GENERATE);
					}
					File tmp = fileList.get(selected - 1);
					fileList.set(selected - 1, listView.getSelectionModel().getSelectedItem());
					fileList.set(selected, tmp);
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
					if (generated) {
						generated = false;
						generate.textProperty().setValue(Constants.PDF_GENERATE);
					}
					File tmp = fileList.get(selected + 1);
					fileList.set(selected + 1, listView.getSelectionModel().getSelectedItem());
					fileList.set(selected, tmp);
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
		return (!fileList.isEmpty());
	}

	/**
	 * Method which generates the PDF.
	 */
	private void generatePDF() {
		PDFMergerUtility utility = new PDFMergerUtility();
		for (File file : fileList) {
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
	 * Method to open the PDF file in viewer.
	 */
	private void openPDF() {
		if (destinationFile.exists() && Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(destinationFile);
			} catch (IOException e) {
				System.out.println("Caught IOException");
			}
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
					openPDF();
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
