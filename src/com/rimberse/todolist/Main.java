package com.rimberse.todolist;
	
import java.io.IOException;

import com.rimberse.todolist.datamodel.TodoData;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void init() throws Exception {
		try {
			TodoData.getInstance().loadTodoItems();
		} catch (IOException exception) {
			System.out.println(exception.getMessage());
		}
	}

	@Override
	public void stop() throws Exception {
		try {
			TodoData.getInstance().storeTodoItems();
		} catch (IOException exception) {
			System.out.println(exception.getMessage());
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainWindow.fxml"));
			Scene scene = new Scene(root,900,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Todo List");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}