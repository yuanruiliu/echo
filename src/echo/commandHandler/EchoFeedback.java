/**
 * 
 * @author A0091684W
 * @description This class is used to represent all Echo Command, 
 * with attributes of command type and task index involved
 * 
 */
package echo.commandHandler;

// TODO make an enum for commandType

public class EchoFeedback {
	private String commandType;
	private String taskToBeEdited;
	private String commandMessage;
	private boolean isSuccess;

	// This is a constructor for 'edit' command type
	public EchoFeedback(String type, String taskEdited, String message, boolean status) {
		commandType = type;
		taskToBeEdited = taskEdited;
		commandMessage = message;
		isSuccess = status;
	}

	// This is a constructor for all command types except 'edit'
	public EchoFeedback(String type, String message, boolean status) {
		commandType = type;
		taskToBeEdited = null;
		commandMessage = message;
		isSuccess = status;
	}
	
	public EchoFeedback(String type) {
		commandType = type;
		taskToBeEdited = null;
		commandMessage = null;
		isSuccess = false;
	}
	
	
	public String getCommandType() {
		return commandType;
	}

	public String getTaskToBeEdited() {
		return taskToBeEdited;
	}

	public String getMessage() {
		return commandMessage;
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}
}
