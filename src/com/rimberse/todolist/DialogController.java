package com.rimberse.todolist;

import java.time.LocalDate;

import com.rimberse.todolist.datamodel.TodoData;
import com.rimberse.todolist.datamodel.TodoItem;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DialogController {
	@FXML
	private TextField shortDescriptionField;
	@FXML
	private TextArea detailsArea;
	@FXML
	private DatePicker deadlinePicker;
	
	public TodoItem processResults() {
		String shortDescription = shortDescriptionField.getText().trim();
		String details = detailsArea.getText().trim();
		LocalDate deadlineValue = deadlinePicker.getValue();
		
		TodoItem newItem = new TodoItem(shortDescription, details, deadlineValue);
		TodoData.getInstance().addTodoItem(newItem);
		return newItem;
	}
	
	public String editExistingTodoItem(TodoItem item) { 					// added additional method 
		String shortDescription = shortDescriptionField.getText().trim();
		String details = detailsArea.getText().trim();
		LocalDate deadlineValue = deadlinePicker.getValue();
		
		return TodoData.getInstance().editTodoItem(item, shortDescription, details, deadlineValue);
	}
}