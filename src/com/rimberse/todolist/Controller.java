package com.rimberse.todolist;

import java.io.IOException;
import java.time.LocalDate;
//import java.time.LocalDate;
//import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.rimberse.todolist.datamodel.TodoData;
import com.rimberse.todolist.datamodel.TodoItem;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class Controller {
//	private List<TodoItem> todoItems;
	@FXML
	private ListView<TodoItem> todoListView;
	@FXML
	private TextArea itemDetailsTextArea;
	@FXML
	private Label deadlineLabel;
	@FXML
	private BorderPane mainBorderPane;
	@FXML
	private ContextMenu listContextMenu;
	@FXML
	private ToggleButton filterToggleButton;
	
	private FilteredList<TodoItem> filteredList;
	private Predicate<TodoItem> wantAllItems;
	private Predicate<TodoItem> wantTodaysItems;
	
	public void initialize() {
//		TodoItem item1 = new TodoItem("Mail Birthday card", "Buy a 30th birthday card for John", 
//				LocalDate.of(2016, Month.APRIL, 25));
//		
//		TodoItem item2 = new TodoItem("Doctor's Appointment", "See Dr. Smith at 123 Main Street. Bring paperwork", 
//				LocalDate.of(2016, Month.MAY, 23));
//		TodoItem item3 = new TodoItem("Finish design proposal for client", "I promised Mike I'd email website mockups by Friday 22nd April", 
//				LocalDate.of(2016, Month.APRIL, 22));
//		TodoItem item4 = new TodoItem("Pickup Doug at the train station", "Doug's arriving on March 23 on the 5:00 train", 
//				LocalDate.of(2016, Month.MARCH, 23));
//		TodoItem item5 = new TodoItem("Pickup dry cleaning", "The clothes should be ready by Wednesday", 
//				LocalDate.of(2016, Month.APRIL, 20));
//		
//		todoItems = new ArrayList<>();
//		todoItems.add(item1);
//		todoItems.add(item2);
//		todoItems.add(item3);
//		todoItems.add(item4);
//		todoItems.add(item5);
//		
//		TodoData.getInstance().setTodoItems(todoItems);
		
		listContextMenu = new ContextMenu();
		
		MenuItem editMenuItem = new MenuItem("Edit");
		editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				showEditDialog();
			}
		});
		listContextMenu.getItems().addAll(editMenuItem);
		
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				TodoItem item = todoListView.getSelectionModel().getSelectedItem();
				deleteItem(item);
			}
		});
		listContextMenu.getItems().addAll(deleteMenuItem);
		
		todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
			@Override
			public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
				if (newValue != null) {
					TodoItem item = todoListView.getSelectionModel().getSelectedItem();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy"); 	// or d/MM/uuuu
					String date = item.getDeadline().format(formatter);
					itemDetailsTextArea.setText(item.getDetails());
					deadlineLabel.setText(date);
				}
			}
		});
		
		wantAllItems = new Predicate<TodoItem>() {

			@Override
			public boolean test(TodoItem t) {
				return true;
			}
			
		};
		
		wantTodaysItems = new Predicate<TodoItem>() {

			@Override
			public boolean test(TodoItem t) {
				return t.getDeadline().equals(LocalDate.now());
			}
			
		};
		
		filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), wantAllItems);
		SortedList<TodoItem> sortedList = new SortedList<>(filteredList, new Comparator<TodoItem>() {

			@Override
			public int compare(TodoItem o1, TodoItem o2) {
				return o1.getDeadline().compareTo(o2.getDeadline());
			}
			
		});
		
		todoListView.setItems(sortedList);
//		todoListView.setItems(TodoData.getInstance().getTodoItems());
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.getSelectionModel().selectFirst();
		
		todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
			
			@Override
			public ListCell<TodoItem> call(ListView<TodoItem> param) {
				ListCell<TodoItem> cell = new ListCell<TodoItem>() {
					@Override
					protected void updateItem(TodoItem item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
						} else {
							setText(item.getShortDescription());
							if (item.getDeadline().equals(LocalDate.now())) {
								setTextFill(Color.RED);
							}
							else if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
								setTextFill(Color.BLUE);
							} else if (item.getDeadline().isAfter(LocalDate.now())) {
								setTextFill(Color.GREEN);
							} 
						}	
					}
				};
				cell.emptyProperty().addListener(
						(obs, wasEmpty, isNowEmpty) -> {
							if (isNowEmpty) {
								cell.setContextMenu(null);
							} else {
								cell.setContextMenu(listContextMenu);
							}
						});
				return cell;
			}
		});
	}
	
	@FXML
	public void showNewItemDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Add New Todo Item");
		dialog.setHeaderText("Use this dialog to create a new Todo item");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getClassLoader().getResource("TodoItemDialog.fxml"));
		
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch(IOException exception) {
			System.out.println("Couldn't load the dialog");
			exception.printStackTrace();
			return;
		}
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			DialogController controller = fxmlLoader.getController();
			TodoItem newItem = controller.processResults();
//			todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
			todoListView.getSelectionModel().select(newItem);
		}
	}
	
	public void showEditDialog() {										// my implementation of edit functionality
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Edit existing Todo Item");
		dialog.setHeaderText("Use this dialog to edit existing Todo item");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getClassLoader().getResource("TodoItemDialog.fxml"));
		
		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch(IOException exception) {
			System.out.println("Couldn't load the dialog");
			exception.printStackTrace();
			return;
		}
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		
		Optional<ButtonType> result = dialog.showAndWait();
		String information = null;
		if (result.isPresent() && result.get() == ButtonType.OK) {
			DialogController controller = fxmlLoader.getController();
			TodoItem existingItem = todoListView.getSelectionModel().getSelectedItem();
			information = controller.editExistingTodoItem(existingItem);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Edit Todo Item");
			alert.setHeaderText("Todo item was succesfully updated");
			alert.setContentText(information);
			Optional<ButtonType> confirmation = alert.showAndWait();
			if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
				todoListView.getSelectionModel().selectPrevious();
				todoListView.getSelectionModel().select(existingItem);
			}
		}
	}
	
	@FXML
	public void handleKeyPressed(KeyEvent keyEvent) {
		TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null)
			if (keyEvent.getCode().equals(KeyCode.DELETE))
				deleteItem(selectedItem);
	}
	
	@FXML
	public void handleClickListView(MouseEvent event) {
		TodoItem item = todoListView.getSelectionModel().getSelectedItem();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/uuuu");
		String date = item.getDeadline().format(formatter);
		itemDetailsTextArea.setText(item.getDetails());
		deadlineLabel.setText(date);
//		StringBuilder sb = new StringBuilder(item.getDetails());
//		sb.append("\n\n\n\n");
//		sb.append("Due: ");
//		sb.append(date);
//		itemDetailsTextArea.setText(sb.toString());
	}
	
	public void deleteItem(TodoItem item) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Todo Item");
		alert.setHeaderText("Detele item: " + item.getShortDescription());
		alert.setContentText("Are you sure? Press OK to confirm, or Cancel to back out.");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			TodoData.getInstance().deleteTodoItem(item);
			
		}
	}
	
	@FXML
	public void handleFilterButton() {
		TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
		if (filterToggleButton.isSelected()) {
			filteredList.setPredicate(wantTodaysItems);
			if (filteredList.isEmpty()) {
				itemDetailsTextArea.clear();
				deadlineLabel.setText("");
			} else if (filteredList.contains(selectedItem)) {
				todoListView.getSelectionModel().select(selectedItem);
			} else {
				todoListView.getSelectionModel().selectFirst();
			}
		} else {
			filteredList.setPredicate(wantAllItems);
			todoListView.getSelectionModel().select(selectedItem);
		}
	}
	
	@FXML 
	public void handleExit() {
		Platform.exit();
	}
}