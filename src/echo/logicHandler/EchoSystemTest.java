/**
 * This class carries out System Testing for Echo
 * 
 * @author A0091684W
 *
 */
package echo.logicHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.JTextField;
import javax.swing.JWindow;

import org.junit.Before;
import org.junit.Test;

import echo.commandHandler.EchoCommandHandler;
import echo.commandHandler.EchoFeedback;
import echo.gui.WindowManager;

public class EchoSystemTest {
	/* Tab constants */
	private static final int TAB_ACTIVE = 0;
	private static final int TAB_DUE = 1;
	private static final int TAB_COMPLETED = 2;

	/* Error message constants */
	private static final String EXCEEDING_MAXIMUM_ERROR_MESSAGE = "The number you entered exceeds the total number of task";
	private static final String ERROR_EMPTY_STRING = "Please enter a command";
	private static final String INVALID_TITLE_ERROR_MESSAGE = "Please enter a valid task (enter '?' for help)";
	private static final String INVALID_DATE_ERROR_MESSAGE = "Please enter a valid date (enter '?' for help)";
	private static final String INVALID_COMMAND_ERROR_MESSAGE = "Please enter a valid command (enter '?' for help)";
	private static final String INVALID_INDEX_ERROR_MESSAGE = "Please enter a valid task no (enter '?' for help)";
	private static final String INVALID_COMPLETED_TAB_ERROR_MESSAGE = "Please switch to the completed tab";
	private static final String INVALID_ACTIVE_TAB_ERROR_MESSAGE = "Please switch to the active tab";

	/* Respond message constants */
	private static final String TASK_ADDED_MESSAGE = "Task %d is successfully added";
	private static final String TASK_DELETED_MESSAGE = "Task %d is successfully deleted";
	private static final String MULTIPLE_TASK_DELETED_MESSAGE = "The selected tasks are successfully deleted";
	private static final String TASK_EDIT_MESSAGE = "You can start editing the task below";
	private static final String TASK_DONE_MESSAGE = "Task %d is moved to completed task list";
	private static final String MULTIPLE_TASK_DONE_MESSAGE = "The selected tasks are successfully done";
	private static final String TASK_UNDONE_MESSAGE = "Task %d is moved to active task list";
	private static final String MULTIPLE_TASK_UNDONE_MESSAGE = "The selected tasks are successfully undone";
	private static final String TASK_UNDO_MESSAGE = "Undo is successful";
	private static final String TASK_REDO_MESSAGE = "Redo is successful";
	private static final String TASK_SEARCH_MESSAGE = "Found %d matches for '%s'";
	private static final String TASK_SEARCH_NO_MATCH_MESSAGE = "Found 0 matches for '%s'";

	WindowManager windowManager = new WindowManager();
	EchoTaskHandler taskHandler = new EchoTaskHandler();
	EchoCommandHandler commandHandler = new EchoCommandHandler();
	EchoFeedback feedback = null;

	@Before
	public void clearTasks() throws Exception {
		taskHandler.clear();
	}

	@Test
	public void testCommands() {

		// Test Case 1 - Add tasks
		feedback = commandHandler.parseCommand(
				"dinner with Dad 15/12/12 17:00", TAB_ACTIVE);
		String expectedMessage = String.format(TASK_ADDED_MESSAGE, 1);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());
		
		feedback = commandHandler.parseCommand("buy groceries 21/12/2012",
				TAB_ACTIVE);
		expectedMessage = String.format(TASK_ADDED_MESSAGE, 1);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());
		
		feedback = commandHandler.parseCommand("sports training 12/12/2012 1000 - 1200",
				TAB_ACTIVE);
		expectedMessage = String.format(TASK_ADDED_MESSAGE, 1);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());
		
		feedback = commandHandler.parseCommand(
				"cs2102 assignment 13/11/2012 - 16/11/2012", TAB_ACTIVE);
		expectedMessage = String.format(TASK_ADDED_MESSAGE, 1);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());
		
		feedback = commandHandler
				.parseCommand(
						"book overview 12/12/2012 05:00 - 13/12/2012 13:20",
						TAB_ACTIVE);
		expectedMessage = String.format(TASK_ADDED_MESSAGE, 2);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());
		
		feedback = commandHandler.parseCommand(
				"write project code 20/11/2012 1000 - 1700", TAB_ACTIVE);
		expectedMessage = String.format(TASK_ADDED_MESSAGE, 2);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());
		
		feedback = commandHandler.parseCommand("lunch with mom 15/12/12 12:00",
				TAB_ACTIVE);
		expectedMessage = String.format(TASK_ADDED_MESSAGE, 5);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());
		
		feedback = commandHandler.parseCommand(
				"dinner with jim 17/12/12 17:00", TAB_ACTIVE);
		expectedMessage = String.format(TASK_ADDED_MESSAGE, 7);
		assertEquals("Test Case 1 - Add tasks", expectedMessage,
				feedback.getMessage());

		// Test Case 2 - Edit tasks
		feedback = commandHandler.parseCommand("edit 1", TAB_ACTIVE);
		assertEquals("Test Case 2 - Edit task", TASK_EDIT_MESSAGE,
				feedback.getMessage());
		assertEquals("Test Case 2 - Edit task",
				"cs2102 assignment 13/11/2012 00:00 to 16/11/2012 23:59",
				feedback.getTaskToBeEdited());

		// Test Case 3 - Help
		JTextField textField = windowManager.getInputField();
		JWindow helpWindow = windowManager.getPopUpHelp();
		textField.setText("?");
		assertTrue("Test Case 3 - Help", helpWindow.isVisible());

		// Test Case 4 - Delete tasks under active tab
		feedback = commandHandler.parseCommand("delete 1", TAB_ACTIVE);
		expectedMessage = String.format(TASK_DELETED_MESSAGE, 1);
		assertEquals("Test Case 4 - Delete task", expectedMessage,
				feedback.getMessage());
		feedback = commandHandler.parseCommand("delete 2 1", TAB_ACTIVE);
		assertEquals("Test Case 4 - Delete multipletasks",
				MULTIPLE_TASK_DELETED_MESSAGE, feedback.getMessage());
		feedback = commandHandler.parseCommand("delete 3 3 1", TAB_ACTIVE);
		assertEquals("Test Case 4 - Delete duplecated tasks",
				MULTIPLE_TASK_DELETED_MESSAGE, feedback.getMessage());
		
		// undo the previous deletion
		commandHandler.parseCommand("undo", TAB_ACTIVE);

		// Test Case 5 - Search tasks
		feedback = commandHandler.parseCommand("search dinner", TAB_ACTIVE);
		expectedMessage = String.format(TASK_SEARCH_MESSAGE, 2, "dinner");
		assertEquals("Test Case 5 - Search tasks found", expectedMessage,
				feedback.getMessage());
		feedback = commandHandler.parseCommand("search amy", TAB_ACTIVE);
		expectedMessage = String.format(TASK_SEARCH_NO_MATCH_MESSAGE, "amy");
		assertEquals("Test Case 5 - Search tasks not found", expectedMessage,
				feedback.getMessage());

		// Test Case 6 - Done tasks
		feedback = commandHandler.parseCommand("done 1", TAB_ACTIVE);
		expectedMessage = String.format(TASK_DONE_MESSAGE, 1);
		assertEquals("Test Case 6 - Done task", expectedMessage, feedback.getMessage());
		feedback = commandHandler.parseCommand("done 2 1", TAB_ACTIVE);
		assertEquals("Test Case 6 - Done multiple tasks", MULTIPLE_TASK_DONE_MESSAGE, feedback.getMessage());

		// Test Case 7 - Undone tasks
		feedback = commandHandler.parseCommand("undone 1", TAB_COMPLETED);
		expectedMessage = String.format(TASK_UNDONE_MESSAGE, 1);
		assertEquals("Test Case 7 - Undone task", expectedMessage, feedback.getMessage());
		feedback = commandHandler.parseCommand("undone 2 1", TAB_COMPLETED);
		assertEquals("Test Case 7 - Undone multiple tasks", MULTIPLE_TASK_UNDONE_MESSAGE, feedback.getMessage());
		feedback = commandHandler.parseCommand("undone 2 1", TAB_DUE);
		assertEquals("Test Case 7 - Undone tasks in wrong tab", INVALID_COMPLETED_TAB_ERROR_MESSAGE, feedback.getMessage());

		// Test Case 8 - Undo
		feedback = commandHandler.parseCommand("undo", TAB_ACTIVE);
		assertEquals("Test Case 8 - Undo", TASK_UNDO_MESSAGE, feedback.getMessage());
	}
}
