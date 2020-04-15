package com.rimberse.todolist.datamodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TodoData {
	public static TodoData getInstance() {
		return instance;
	}
	
	public ObservableList<TodoItem> getTodoItems() {
		return todoItems;
	}

//	public void setTodoItems(List<TodoItem> todoItems) {
//		this.todoItems = todoItems;
//	}
	
	public void addTodoItem(TodoItem item) {
		todoItems.add(item);
	}
	
	public String editTodoItem(TodoItem item, String shortDescription, String details, LocalDate deadline) {	
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy"); 		// added method for confirmation pop-up
		int index = todoItems.indexOf(item);
		String oldShortDescription = todoItems.get(index).getShortDescription();
		String oldDetails = todoItems.get(index).getDetails();
		String oldDeadline = todoItems.get(index).getDeadline().format(formatter);
		StringBuilder sb = new StringBuilder("Edited:\n\t");
		if (!shortDescription.isEmpty()) {
			todoItems.get(index).setShortDescription(shortDescription);
			sb.append("Short Description: <" + oldShortDescription + "> to <" + todoItems.get(index).getShortDescription() + ">\n\t");
		}
		if (!details.isEmpty()) {
			todoItems.get(index).setDetails(details);
			sb.append("Details: <" + oldDetails + "> to <" + todoItems.get(index).getDetails() + ">\n\t");
		}
		if (deadline != null) {
			todoItems.get(index).setDeadline(deadline);
			sb.append("Deadline: <" + oldDeadline + "> to <" + todoItems.get(index).getDeadline().format(formatter) + ">");
		}
		return sb.toString();
	}

	private static TodoData instance = new TodoData();
	private static String filename = "TodoListItems.txt";
	
	private ObservableList<TodoItem> todoItems;
	private DateTimeFormatter formatter;
	
	private TodoData() {
		formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	}
	
	public void loadTodoItems() throws IOException {
		todoItems = FXCollections.observableArrayList();
		Path path = Paths.get(filename);
		BufferedReader br = Files.newBufferedReader(path);
		
		String input;
		try {
			while ((input = br.readLine()) != null) {
				String[] itemPieces = input.split("\t");
				String shortDescription = itemPieces[0];
				String details = itemPieces[1];
				String dateString = itemPieces[2];
				
				LocalDate date = LocalDate.parse(dateString, formatter);
				TodoItem todoItem = new TodoItem(shortDescription, details, date);
				todoItems.add(todoItem);
			}
		} finally {
			if (br != null)
				br.close();
		}
	}
	
	public void storeTodoItems() throws IOException {
		Path path = Paths.get(filename);
		BufferedWriter bw = Files.newBufferedWriter(path);
		
		try {
			Iterator<TodoItem> iter = todoItems.iterator();
			while (iter.hasNext()) {
				TodoItem item = iter.next();
				bw.write(String.format("%s\t%s\t%s", item.getShortDescription(), item.getDetails(), item.getDeadline().format(formatter)));
				bw.newLine();
			}
		} finally {
			if (bw != null) 
				bw.close();
		}
	}
	
	public void deleteTodoItem(TodoItem item) {
		todoItems.remove(item);
	}
}