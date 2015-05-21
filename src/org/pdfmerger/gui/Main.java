package org.pdfmerger.gui;
	
import org.pdfmerger.gui.controllers.MainController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * Main method which application loads the main.fxml
 * @author David Jeong
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		Parent root = null;
		FXMLLoader loader = null;
		
		if (Constants.OPERATING_SYSTEM.isEmpty()) {
			Constants.OPERATING_SYSTEM = System.getProperty("os.name");
		}
		
		try {
			loader = new FXMLLoader(this.getClass().getResource("views/main.fxml"));
			root = (Parent)loader.load();
			
			MainController controller = (MainController)loader.getController();
			controller.createHandlers(primaryStage);
			
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			
			// Check if operating system is Windows. If true, set the style to Utility.
			if (Constants.OPERATING_SYSTEM.startsWith("Windows")) {
				primaryStage.initStyle(StageStyle.UTILITY);
			}
			
			primaryStage.setTitle(Constants.APPLICATION_NAME);
			primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("styles/icons/" + Constants.APPLICATION_ICON)));
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					Platform.exit();
				}
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
