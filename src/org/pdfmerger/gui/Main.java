package org.pdfmerger.gui;
	
import org.pdfmerger.gui.controllers.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
		try {
			loader = new FXMLLoader(this.getClass().getResource("views/main.fxml"));
			root = (Parent)loader.load();
			
			MainController controller = (MainController)loader.getController();
			controller.createHandlers(primaryStage);
			
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			
			primaryStage.setResizable(false);
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
