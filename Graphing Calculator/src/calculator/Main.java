package calculator;

import java.io.IOException;

import calculator.view.CalculatorController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

	/**
	 * Primary stage of JavaFX app
	 */
	private Stage primaryStage;
	/**
	 * Root Pane of application
	 */
	private AnchorPane rootLayout;
	
	/**
	 * Default constructor
	 */
	public Main() {}
	
	
	/**
	 * Starts JavaFX application
	 * @param primaryStage primaryStage of JavaFX application
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Calculator");
		initCalculatorLayout();
		
	}
	
	
	/**
	 * Initializes the calculator UI layout from FXML file 
	 * that was build using SceneBuider
	 */
	public void initCalculatorLayout() {
		try {
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/CalculatorLayout.fxml"));
			rootLayout = (AnchorPane) loader.load();
			
			CalculatorController controller = loader.getController();
			controller.setMain(this);
			
			// Show the scene
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns primary stage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Main entry point of JavaFX application
	 * @param args args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
