/**
 * 
 */
/**
 * @author Kerim
 *
 */
module TodoList {
	exports com.rimberse.todolist;
	exports com.rimberse.todolist.datamodel;

	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	
	opens com.rimberse.todolist;
}