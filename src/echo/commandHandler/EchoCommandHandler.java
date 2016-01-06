/**
 * 
 * @author A0091684W Yuan Shuai
 * @description This class is a command handler, which takes in input string from GUI and passes parameters to TaskHandler
 * 
 */
package echo.commandHandler;

import echo.logicHandler.EchoTask;
import echo.logicHandler.EchoTaskHandler;

import echo.exception.InvalidDateException;
import echo.exception.InvalidParameterException;
import echo.exception.InvalidTitleException;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;

public class EchoCommandHandler {

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
	private static final String TASK_SEARCH_NO_MATCH_MESSAGE = "Found no matches for '%s'";

	/* Specific time constants */
	private static final String END_TIME_OF_DAY = "23:59";
	private static final String START_TIME_OF_DAY = "00:00";

	/* Tab constants */
	private static final int TAB_ACTIVE = 0;
	private static final int TAB_DUE = 1;
	private static final int TAB_COMPLETED = 2;

	private static Logger logger = Logger.getLogger("EchoCommandHandler");
	
	private EchoTaskHandler taskHandler;
	private EchoTask newTask;
	private int index = -1;
	private String title = "";
	private Date startDate = null;
	private Date endDate = null;

	/* All allowed time and date formats */
	private static String[] dateFormats = new String[] { "dd/M/yyyy",
			"dd/M/yy", "dd/MM/yyyy", "dd/MM/yy", "d/M/yyyy", "d/M/yy",
			"d/MM/yyyy", "d/MM/yy", "dd-M-yyyy", "dd-M-yy", "dd-MM-yyyy",
			"dd-MM-yy", "d-M-yyyy", "d-M-yy", "d-MM-yyyy", "d-MM-yy",
			"dd.M.yyyy", "dd.M.yy", "dd.MM.yyyy", "dd.MM.yy", "d.M.yyyy",
			"d.M.yy", "d.MM.yyyy", "d.MM.yy" };
	private static String[] timeFormats = new String[] { "HHmm", "HH.mm",
			"HH:mm", "h:mma", "h.mma", "ha", "hha", "hhmma" };
	private String[] dateAndTimeFormats = getDateAndTimeFormats();
	private Format formatterDate = new SimpleDateFormat("dd-MM-yyyy");
	private Format formatterTime = new SimpleDateFormat("HH:mm");

	/**
	 * These are all types of command that are supported by Echo
	 * 
	 */
	enum CommandType {
		EXIT, ADD, DELETE, EDIT, UNDO, REDO, DONE, UNDONE, SEARCH
	};

	public EchoCommandHandler() {
		try {
			taskHandler = new EchoTaskHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The next three methods are called for testing class to get private
	 * attributes from commandHandler class
	 * 
	 * @return startDate, endDate and title
	 */
	public Date getStartDate() {
		if (startDate != null) {
			Date newDate = new Date(startDate.getTime());
			return newDate;
		}
		return null;
	}

	public Date getEndDate() {
		if (endDate != null) {
			Date newDate = new Date(endDate.getTime());
			return newDate;
		}
		return null;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * The next four methods are called by user interface class. They get List
	 * Model from EchoTaskHandler class, then return each List Model to GUI
	 * class. They act as a Facade Pattern between user interface and task
	 * handler classes
	 * 
	 * @return list model for different kinds of tasks
	 */
	public DefaultListModel<EchoTask> getListModelForActiveTasks() {
		DefaultListModel<EchoTask> listModelForActiveTasks = taskHandler
				.getListModelForActiveTasks();

		return listModelForActiveTasks;
	}

	public DefaultListModel<EchoTask> getListModelForCompletedTasks() {
		DefaultListModel<EchoTask> listModelForCompletedTasks = taskHandler
				.getListModelForCompletedTasks();

		return listModelForCompletedTasks;
	}

	public DefaultListModel<EchoTask> getListModelForDueTasks() {
		DefaultListModel<EchoTask> listModelForDueTasks = taskHandler
				.getListModelForDueTasks();

		return listModelForDueTasks;
	}

	public DefaultListModel<EchoTask> getListModelForSearchedTasks() {
		DefaultListModel<EchoTask> listModelForSearchedTasks = taskHandler
				.getListModelForSearchedTasks();

		return listModelForSearchedTasks;
	}

	/**
	 * This method is called by GUI in order to pass the input string, separates
	 * parameters for TaskHandler and calls methods like addTask(Object task),
	 * deleteTask(int index), editTask(int index, string title), editTask(int
	 * index, int hour, int minute) and so on in TaskHandler
	 * 
	 * @param inputString
	 *            the input that user entered in command bar
	 * 
	 * @throws ParseException
	 */
	public EchoFeedback parseCommand(String inputString, int tabIndex) {

		EchoFeedback echoFeedback = null;
		// added a check for empty string, 11/7/2012
		if (inputString.length() < 1) {
			echoFeedback = new EchoFeedback("noCommand", ERROR_EMPTY_STRING,
					false);
			return echoFeedback;
		}

		assert (tabIndex >= 0 && tabIndex <= 4);
		ArrayList<String> inputArray = tokenizeStrBySpace(inputString);
		CommandType commandType = determineCommandType(inputArray.get(0));
		
		// log a message at INFO level
		logger.log(Level.INFO, "going to start parsing");
		try {
			switch (commandType) {
			case EXIT:
				executeExitCommand();
				break;
			case ADD:
				echoFeedback = executeAddCommand(inputArray);
				break;
			case DELETE:
				echoFeedback = executeDeleteCommand(tabIndex, inputArray);
				break;
			case EDIT:
				echoFeedback = executeEditCommand(tabIndex, inputArray);
				break;
			case UNDO:
				echoFeedback = executeUndoCommand();
				break;
			case REDO:
				echoFeedback = executeRedoCommand();
				break;
			case DONE:
				echoFeedback = executeDoneCommand(tabIndex, inputArray);
				break;
			case UNDONE:
				echoFeedback = executeUndoneCommand(tabIndex, inputArray);
				break;
			case SEARCH:
				echoFeedback = executeSearchCommand(tabIndex, inputArray);
				break;
			default:
				echoFeedback = new EchoFeedback("",
						INVALID_COMMAND_ERROR_MESSAGE, false);
			}
		} catch (Exception e) {
			// Log a message at WARNING level
			logger.log(Level.WARNING, "parsing error", e);
		}
		logger.log(Level.INFO, "end of parsing");
		return echoFeedback;
	}

	// When user types "exit" in command bar, exit the program
	public void executeExitCommand() {
		System.exit(0);
	}

	/**
	 * When executing "add" command, first update the commandList for "undo",
	 * then get the parameters for "add", then pass EchoTaskHandler an object of
	 * EchoTask to add task under active tab, in the end if no error, return a
	 * success feedback.
	 * 
	 * @param inputArray
	 * @return echoFeedback
	 */
	public EchoFeedback executeAddCommand(ArrayList<String> inputArray) {
		taskHandler.updateCopy('a');
		taskHandler.setCommandList("add");

		try {
			getAddParameters(inputArray);
		} catch (InvalidDateException e5) {
			EchoFeedback echoFeedback = new EchoFeedback("add",
					INVALID_DATE_ERROR_MESSAGE, false);
			return echoFeedback;
		} catch (NullPointerException e5) {
			EchoFeedback echoFeedback = new EchoFeedback("add",
					INVALID_TITLE_ERROR_MESSAGE, false);
			return echoFeedback;
		}

		newTask = new EchoTask(title, startDate, endDate);
		int returnIndex = taskHandler.addActiveTask(newTask);

		assert (returnIndex > -1);
		String message = String.format(TASK_ADDED_MESSAGE, returnIndex + 1);
		EchoFeedback echoFeedback = new EchoFeedback("add", message, true);

		return echoFeedback;
	}

	/**
	 * When executing "delete" command, first update commandList for "undo",
	 * then get an array of indexes for "delete", then calls delete method in
	 * EchoTaskHandler, if no error then return success feedback
	 * 
	 * @param tabIndex
	 * @param inputArray
	 * @return echoFeedback
	 */
	public EchoFeedback executeDeleteCommand(int tabIndex,
			ArrayList<String> inputArray) {
		taskHandler.updateCopy('a');
		taskHandler.setCommandList("delete");

		EchoFeedback echoFeedback = null;
		int returnIndex = 0;
		String message = "";
		ArrayList<Integer> indexArray = new ArrayList<Integer>();

		try {
			indexArray = getTaskIndices(inputArray);
		} catch (InvalidParameterException e4) {
			echoFeedback = new EchoFeedback("delete",
					INVALID_INDEX_ERROR_MESSAGE, false);
			return echoFeedback;
		} catch (NullPointerException e4) {
			echoFeedback = new EchoFeedback("delete",
					INVALID_TITLE_ERROR_MESSAGE, false);
		}

		for (int i = indexArray.size() - 1; i >= 0; i--) {
			if (indexArray.get(i) > -1) {
				taskHandler.deleteTask(indexArray.get(i), tabIndex);
			} else {
				echoFeedback = new EchoFeedback("delete",
						INVALID_INDEX_ERROR_MESSAGE, false);
				return echoFeedback;
			}
		}

		if (indexArray.size() == 1) {
			returnIndex = indexArray.get(0);
			assert (returnIndex > -1);
			message = String.format(TASK_DELETED_MESSAGE, returnIndex + 1);
		} else {
			message = MULTIPLE_TASK_DELETED_MESSAGE;
		}
		echoFeedback = new EchoFeedback("delete", message, true);
		return echoFeedback;
	}

	/**
	 * "edit" command can only be used for active tasks. When executing "edit"
	 * command, first update commandList for "undo", then get the index for
	 * "edit", then calls edit method in EchoTaskHandler to delete the current
	 * task and get the original string to be put in command bar for edit, if no
	 * error then return success feedback
	 * 
	 * @param tabIndex
	 * @param inputArray
	 * @return echoFeedback
	 */
	public EchoFeedback executeEditCommand(int tabIndex,
			ArrayList<String> inputArray) {
		EchoFeedback echoFeedback = null;

		if (tabIndex != TAB_ACTIVE) {
			echoFeedback = new EchoFeedback("edit",
					INVALID_ACTIVE_TAB_ERROR_MESSAGE, false);
			return echoFeedback;
		}
		taskHandler.updateCopy('a');
		taskHandler.setCommandList("edit");
		try {
			index = getEditParameter(inputArray);
		} catch (InvalidParameterException e4) {
			echoFeedback = new EchoFeedback("edit",
					INVALID_INDEX_ERROR_MESSAGE, false);
			return echoFeedback;
		}

		String originalString = taskHandler.editActiveTask(index);
		assert (originalString != null);
		echoFeedback = new EchoFeedback("edit", originalString,
				TASK_EDIT_MESSAGE, true);
		return echoFeedback;
	}

	// Execute "undo" command
	public EchoFeedback executeUndoCommand() {
		taskHandler.setCommandList("undo");
		taskHandler.undo();
		EchoFeedback echoFeedback = new EchoFeedback("undo", TASK_UNDO_MESSAGE,
				true);
		return echoFeedback;
	}

	// Execute "redo" command
	public EchoFeedback executeRedoCommand() {
		taskHandler.setCommandList("undo");
		taskHandler.redo();
		EchoFeedback echoFeedback = new EchoFeedback("redo", TASK_REDO_MESSAGE,
				true);
		return echoFeedback;
	}

	/**
	 * "done" command can only be used for active tasks. When executing "done"
	 * command, first update commandList for "undo", then get an array of
	 * indexes for "done", then call the done method in EchoTaskHandler. If no
	 * error, return success feedback.
	 * 
	 * @param tabIndex
	 * @param inputArray
	 * @return echoFeedback
	 */
	public EchoFeedback executeDoneCommand(int tabIndex,
			ArrayList<String> inputArray) {
		EchoFeedback echoFeedback = null;

		if ((tabIndex != TAB_ACTIVE) && (tabIndex != TAB_DUE)) {
			echoFeedback = new EchoFeedback("done",
					INVALID_ACTIVE_TAB_ERROR_MESSAGE, false);
			return echoFeedback;
		}
		taskHandler.updateCopy('a');
		taskHandler.updateCopy('c');
		taskHandler.setCommandList("done");
		ArrayList<Integer> indexArray = new ArrayList<Integer>();
		int returnIndex = 0;
		String message = "";

		try {
			indexArray = getTaskIndices(inputArray);
		} catch (InvalidParameterException e4) {
			echoFeedback = new EchoFeedback("done",
					INVALID_INDEX_ERROR_MESSAGE, false);
			return echoFeedback;
		} catch (NullPointerException e5) {
			echoFeedback = new EchoFeedback("done",
					EXCEEDING_MAXIMUM_ERROR_MESSAGE, false);
			return echoFeedback;
		}
		for (int i = indexArray.size() - 1; i >= 0; i--) {
			if (indexArray.get(i) > -1) {
				taskHandler.doneTask(indexArray.get(i), tabIndex);
			} else {
				echoFeedback = new EchoFeedback("done",
						INVALID_INDEX_ERROR_MESSAGE, false);
				return echoFeedback;
			}
		}

		if (indexArray.size() == 1) {
			returnIndex = indexArray.get(0);
			assert (returnIndex > -1);
			message = String.format(TASK_DONE_MESSAGE, returnIndex + 1);
		} else {
			message = MULTIPLE_TASK_DONE_MESSAGE;
		}
		echoFeedback = new EchoFeedback("done", message, true);
		return echoFeedback;
	}

	/**
	 * "undone" command can only be used for completed tasks. When executing
	 * "undone" command, first update commandList for "undo", then get an array
	 * of indexes for "undone", then call the undone method in EchoTaskHandler.
	 * If no error, return success feedback.
	 * 
	 * @param tabIndex
	 * @param inputArray
	 * @return echoFeedback
	 */
	public EchoFeedback executeUndoneCommand(int tabIndex,
			ArrayList<String> inputArray) {
		EchoFeedback echoFeedback = null;
		if (tabIndex != TAB_COMPLETED) {
			echoFeedback = new EchoFeedback("undone",
					INVALID_COMPLETED_TAB_ERROR_MESSAGE, false);
			return echoFeedback;
		}
		taskHandler.updateCopy('a');
		taskHandler.updateCopy('c');
		taskHandler.setCommandList("undone");
		ArrayList<Integer> indexArray = new ArrayList<Integer>();
		int returnIndex = 0;
		String message = "";

		try {
			indexArray = getTaskIndices(inputArray);
		} catch (InvalidParameterException e4) {
			echoFeedback = new EchoFeedback("undone",
					INVALID_INDEX_ERROR_MESSAGE, false);
			return echoFeedback;
		} catch (NullPointerException e5) {
			echoFeedback = new EchoFeedback("undone",
					EXCEEDING_MAXIMUM_ERROR_MESSAGE, false);
			return echoFeedback;
		}

		for (int i = indexArray.size() - 1; i >= 0; i--) {
			if (indexArray.get(i) > -1) {
				taskHandler.undoneTask(indexArray.get(i));
			} else {
				echoFeedback = new EchoFeedback("undone",
						INVALID_INDEX_ERROR_MESSAGE, false);
				return echoFeedback;
			}
		}

		if (indexArray.size() == 1) {
			returnIndex = indexArray.get(0);
			assert (returnIndex > -1);
			message = String.format(TASK_UNDONE_MESSAGE, returnIndex + 1);
		} else {
			message = MULTIPLE_TASK_UNDONE_MESSAGE;
		}
		echoFeedback = new EchoFeedback("undone", message, true);
		return echoFeedback;
	}

	/**
	 * "search" command can only be used for active tasks currently. When
	 * executing "search" command, first update commandList for "undo", then get
	 * the parameter for "search", then call the search method in
	 * EchoTaskHandler. If no error, return success feedback.
	 * 
	 * @param tabIndex
	 * @param inputArray
	 * @return echoFeedback
	 */
	public EchoFeedback executeSearchCommand(int tabIndex,
			ArrayList<String> inputArray) {
		int returnIndex = 0;
		String message = "";
		EchoFeedback echoFeedback = null;

		taskHandler.setCommandList("search");
		String str = getSearchParameters(inputArray);
		returnIndex = taskHandler.searchTask(tabIndex, str);

		if (returnIndex > 0) {
			message = String.format(TASK_SEARCH_MESSAGE, returnIndex, str);
			echoFeedback = new EchoFeedback("search", message, true);
		} else {
			// no matches
			message = String.format(TASK_SEARCH_NO_MATCH_MESSAGE, str);
			echoFeedback = new EchoFeedback("search", message, false);
		}

		message = String.format(TASK_SEARCH_MESSAGE, returnIndex, str);
		echoFeedback = new EchoFeedback("search", message, true);
		return echoFeedback;
	}

	/**
	 * This method determines command type.
	 * 
	 * @param commandTypeString
	 * @return commandType
	 */
	public CommandType determineCommandType(String commandTypeString) {
		if ((commandTypeString.equalsIgnoreCase("delete"))
				|| (commandTypeString.equalsIgnoreCase("del"))) {
			return CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return CommandType.EDIT;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return CommandType.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return CommandType.REDO;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return CommandType.DONE;
		} else if (commandTypeString.equalsIgnoreCase("undone")) {
			return CommandType.UNDONE;
		} else if ((commandTypeString.equalsIgnoreCase("search"))
				|| (commandTypeString.equalsIgnoreCase("src"))) {
			return CommandType.SEARCH;
		} else if ((commandTypeString.equalsIgnoreCase("exit"))
				|| (commandTypeString.equalsIgnoreCase("quit"))) {
			return CommandType.EXIT;
		} else {
			return CommandType.ADD;
		}
	}

	/**
	 * This method gets the parameters of title, start date and end date in
	 * different types of task inputs
	 * 
	 * @param inputArray
	 * @throws InvalidDateException
	 * @throws NullTitleException
	 * @throws InvalidTitleException
	 */
	public void getAddParameters(ArrayList<String> inputArray)
			throws InvalidDateException, NullPointerException {
		int titleEndIndex = getTitleEndIndex(inputArray);
		ArrayList<String> dateArray = new ArrayList<String>();

		if (titleEndIndex < 0) {
			throw new NullPointerException();
		} else {
			title = getTitle(titleEndIndex, inputArray);
		}
		dateArray = removeTitle(titleEndIndex, inputArray);
		
		if (isFloatingTask(dateArray)) {
			startDate = null;
			endDate = null;
		} else if (isDeadlineTask(dateArray)) {
			if (dateArray.size() == 1) {
				String element = dateArray.get(0).toUpperCase();
				if (isDate(element)) {
					startDate = combineDateTimeStrToDate(element,
							START_TIME_OF_DAY);
					endDate = combineDateTimeStrToDate(element, END_TIME_OF_DAY);
					return;
				} else if (isTime(element)) {
					Date inputTime = combineDateTimeStrToDate(getTodayDate(),
							element);
					if (isPassedTime(inputTime)) {
						dateArray.add(0, getTomorrowDate());
					} else {
						dateArray.add(0, getTodayDate());
					}
				}
			}
			startDate = getDate(dateArray);
			endDate = startDate;
		} else if (isWholeDayTask(dateArray)) {
			String dateString = dateArray.get(0).toUpperCase();
			Date inputTime = combineDateTimeStrToDate(dateString, getNowTime());
			if (isPassedTime(inputTime)) {
				throw new InvalidDateException();
			} else {
				startDate = combineDateTimeStrToDate(dateString,
						START_TIME_OF_DAY);
				endDate = combineDateTimeStrToDate(dateString, END_TIME_OF_DAY);
			}
		} else if (hasMoreThanTwoInputDates(dateArray)) {
			throw new InvalidDateException();
		} else {
			ArrayList<String> firstDateArray = getFirstDateArrayList(
					titleEndIndex, inputArray);
			ArrayList<String> secondDateArray = getSecondDateArrayList(
					titleEndIndex, inputArray);

			// For the case that user input is "title startDate - endDate"
			if ((firstDateArray.size() == 1) && (secondDateArray.size() == 1)) {
				String elementInFirstArray = firstDateArray.get(0)
						.toUpperCase();
				String elementInSecondArray = secondDateArray.get(0)
						.toUpperCase();
				if ((isDate(elementInFirstArray) && isDate(elementInSecondArray))) {
					startDate = combineDateTimeStrToDate(elementInFirstArray,
							START_TIME_OF_DAY);
					endDate = combineDateTimeStrToDate(elementInSecondArray,
							END_TIME_OF_DAY);
					return;
				}
			}

			// For the case that user input is "title startTime - endTime"
			// Add the appropriate date to both of the dateArray
			if ((firstDateArray.size() == 1) && (secondDateArray.size() == 1)) {
				String elementInFirstArray = firstDateArray.get(0)
						.toUpperCase();
				String elementInSecondArray = secondDateArray.get(0)
						.toUpperCase();
				if (isTime(elementInFirstArray) && isTime(elementInSecondArray)) {
					Date inputTime = combineDateTimeStrToDate(getTodayDate(),
							elementInFirstArray);
					if (isPassedTime(inputTime)) {
						firstDateArray.add(0, getTomorrowDate());
						secondDateArray.add(0, getTomorrowDate());
					} else {
						firstDateArray.add(0, getTodayDate());
						secondDateArray.add(0, getTodayDate());
					}
				}
			}

			// For the case that user input is "title date startTime - endTime"
			// Add the date to the dateArray that lack of the date
			if ((firstDateArray.size() == 2) && (secondDateArray.size() == 1)) {
				if (isDate(firstDateArray.get(0).toUpperCase())) {
					secondDateArray.add(0, firstDateArray.get(0));
				} else if (isDate(firstDateArray.get(1).toUpperCase())) {
					secondDateArray.add(0, firstDateArray.get(1));
				}
			} else if ((firstDateArray.size() == 1)
					&& (secondDateArray.size() == 2)) {
				if (isDate(secondDateArray.get(0).toUpperCase())) {
					firstDateArray.add(0, firstDateArray.get(0));
				} else if (isDate(secondDateArray.get(1).toUpperCase())) {
					firstDateArray.add(0, firstDateArray.get(1));
				}
			}

			// For the case that user input is
			// "title startDate startTime - endDate endTime"
			// For the case that user input is
			// "title startTime startDate - endTime endDate"
			// And the two cases above which have been changed to have both date
			// and time in both dateArrays
			startDate = getDate(firstDateArray);
			endDate = getDate(secondDateArray);
		}
	}

	/**
	 * This method checks in the dateArray, whether there exists certain format
	 * of time or date, then gets the valid date including date and time from
	 * the input
	 * 
	 * @param inputArray
	 *            part of input string, in the format of ArrayList<String>
	 * @return the Date
	 * @throws InvalidDateException
	 */
	public Date getDate(ArrayList<String> dateArray)
			throws InvalidDateException {
		assert (dateArray.size() == 2);

		String firstElement = dateArray.get(0).toUpperCase();
		String secondElement = dateArray.get(1).toUpperCase();

		if (isDate(firstElement) && isTime(secondElement)) {
			// For the case that the time array contains date and then time
			Date inputTime = combineDateTimeStrToDate(firstElement,
					secondElement);
			if (isPassedTime(inputTime)) {
				throw new InvalidDateException();
			} else {
				return inputTime;
			}
		} else if (isTime(firstElement) && isDate(secondElement)) {
			// For the case that the time array contains time and then date
			Date inputTime = combineDateTimeStrToDate(secondElement,
					firstElement);
			if (isPassedTime(inputTime)) {
				throw new InvalidDateException();
			} else {
				return inputTime;
			}
		} else {
			throw new InvalidDateException();
		}
	}

	/**
	 * This method returns the arrayList of integer indexes user enters in each
	 * command, like "DELETE", "DONE" and "UNDONE"
	 * 
	 * @param inputArray
	 * @return sorted integer array of command parameters
	 * @throws InvalidParameterException
	 */
	public ArrayList<Integer> getTaskIndices(ArrayList<String> inputArray)
			throws InvalidParameterException {
		int size = getListModelForActiveTasks().size();
		ArrayList<Integer> indexArray = new ArrayList<Integer>();

		if (inputArray.size() - 1 > size) {
			throw new NullPointerException();
		} 

		for (int i = 0; i < inputArray.size() - 1; i++) {
			String parameter = inputArray.get(i + 1);

			if (isInt(parameter) && (Integer.parseInt(parameter) <= size)) {
				indexArray.add(Integer.parseInt(parameter) - 1);
			} else {
				throw new InvalidParameterException();
			}
		}

		// Get rid of duplicates
		HashSet<Integer> hs = new HashSet<Integer>();
		hs.addAll(indexArray);
		indexArray.clear();
		indexArray.addAll(hs);
		Collections.sort(indexArray);
		return indexArray;
	}

	/**
	 * This method returns the parameter user enters in edit command
	 * 
	 * @param inputArray
	 * @return parameter integer of edit command
	 * @throws InvalidParameterException
	 */
	public int getEditParameter(ArrayList<String> inputArray)
			throws InvalidParameterException {
		int index = -1;
		String parameter = inputArray.get(1);
		if (isInt(parameter)) {
			index = Integer.parseInt(parameter) - 1;
		} else {
			throw new InvalidParameterException();
		}
		return index;
	}

	/**
	 * This method returns the parameter user enters in search command
	 * 
	 * @param inputArray
	 * @return parameter string of search command
	 */
	public String getSearchParameters(ArrayList<String> inputArray) {
		String parameter = inputArray.get(1);
		return parameter;
	}

	/**
	 * This method combines the specific date string and the specific time
	 * string to a Date
	 * 
	 * @param date
	 *            string
	 * @param time
	 *            string
	 * @return Date
	 */
	public Date combineDateTimeStrToDate(String date, String time) {
		Date test = null;
		String testDate = date + " " + time;

		for (String format : dateAndTimeFormats) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setLenient(false); // ensure that the date is valid
			try {
				test = dateFormat.parse(testDate.trim());
			} catch (ParseException pe) {
				// do nothing
			}
		}
		return test;
	}

	/**
	 * This method checks the position of the first date and the first time
	 * appears in the input array, since task title will always come before the
	 * task date, this method will return the end index of the task title
	 * 
	 * @param inputArray
	 * @return end index of title
	 * @throws NullPointerException
	 */
	public int getTitleEndIndex(ArrayList<String> inputArray) {
		if (inputArray.size() == 0) {
			throw new NullPointerException();
		} else if (inputArray.get(0) == "") {
			throw new NullPointerException();
		}

		int dateStartIndex = 0;
		int timeStartIndex = 0;
		boolean hasDate = false;
		boolean hasTime = false;
		for (int i = 0; i < inputArray.size(); i++) {
			if (isDate(inputArray.get(i).toUpperCase())) {
				dateStartIndex = i - 1;
				hasDate = true;
				break;
			}
			if (isTime(inputArray.get(i).toUpperCase())) {
				timeStartIndex = i - 1;
				hasTime = true;
				break;
			}
		}

		boolean userEntersDateBeforeTime = hasDate && hasTime
				&& (dateStartIndex <= timeStartIndex);
		boolean userEntersTimeBeforeDate = hasDate && hasTime
				&& (timeStartIndex < dateStartIndex);
		boolean userEntersDateOnly = hasDate && (!hasTime);
		boolean userEntersTimeOnly = hasTime && (!hasDate);

		if (userEntersDateBeforeTime) {
			return dateStartIndex;
		} else if (userEntersTimeBeforeDate) {
			return timeStartIndex;
		} else if (userEntersDateOnly) {
			return dateStartIndex;
		} else if (userEntersTimeOnly) {
			return timeStartIndex;
		} else {
			return inputArray.size() - 1;
		}
	}

	/**
	 * This method gets called when there are two specific dates in the input,
	 * and this method gets the first arrayList that contains the first date and
	 * time associated
	 * 
	 * @param testArray
	 * @return the first arrayList that contains the first date
	 */
	public ArrayList<String> getFirstDateArrayList(int titleEndIndex,
			ArrayList<String> inputArray) {
		ArrayList<String> cloneArray = new ArrayList<String>(inputArray);
		int startDateEndIndex = -1;

		for (int i = titleEndIndex + 1; i < cloneArray.size(); i++) {
			String element = cloneArray.get(i).toUpperCase();
			String nextElement = cloneArray.get(i - 1).toUpperCase();
			if (isTimeIntervalPrep(element)) {
				if (isDate(nextElement) || isTime(nextElement)) {
					startDateEndIndex = i - 1;
				}
			}
		}

		for (int i = cloneArray.size() - 1; i > startDateEndIndex; i--) {
			cloneArray.remove(i);
		}
		for (int i = titleEndIndex; i >= 0; i--) {
			cloneArray.remove(i);
		} // to get only the first date string

		return cloneArray;
	}

	/**
	 * This method gets called when there are two specific dates in the input,
	 * and this method gets the second arrayList that contains the second date
	 * and time associated
	 * 
	 * @param testArray
	 * @return the second arrayList that contains the second date
	 */
	public ArrayList<String> getSecondDateArrayList(int titleEndIndex,
			ArrayList<String> inputArray) {
		ArrayList<String> cloneArray = new ArrayList<String>(inputArray);
		int endDateStartIndex = -1;

		for (int i = titleEndIndex + 1; i < cloneArray.size(); i++) {
			String element = cloneArray.get(i).toUpperCase();
			if (isTimeIntervalPrep(element)) {
				endDateStartIndex = i + 1;
			}
		}

		for (int i = endDateStartIndex - 1; i >= 0; i--) {
			cloneArray.remove(i);
		} // to get only the second string
		return cloneArray;
	}

	public ArrayList<String> removeTitle(int titleEndIndex,
			ArrayList<String> inputArray) {
		ArrayList<String> cloneArray = new ArrayList<String>(inputArray);
		for (int i = titleEndIndex; i >= 0; i--) {
			cloneArray.remove(i);
		}
		return cloneArray;
	}

	/**
	 * This method gets the system date String of today
	 * 
	 * @return today the system date of today in date format of "dd-MM-yyyy"
	 */
	public String getTodayDate() {
		String today = "";
		Calendar calendar = Calendar.getInstance();

		Date date = calendar.getTime();
		today = formatterDate.format(date);
		return today.toUpperCase();
	}

	/**
	 * This method gets the system date String of tomorrow
	 * 
	 * @return tomorrow the system date of tomorrow in date format of
	 *         "dd-MM-yyyy"
	 */
	public String getTomorrowDate() {
		String tomorrow = "";
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DATE, 1);
		Date date = calendar.getTime();
		tomorrow = formatterDate.format(date);
		return tomorrow.toUpperCase();
	}

	/**
	 * This method gets the system time String by now
	 * 
	 * @return time string in the time format of "HH:mm"
	 */
	public String getNowTime() {
		String now = "";
		Calendar calendar = Calendar.getInstance();

		Date time = calendar.getTime();
		now = formatterTime.format(time);
		return now.toUpperCase();
	}

	/**
	 * This method gets the String that represents the title in the inputArray
	 * 
	 * @param titleEndIndex
	 *            : the end index of title
	 * @param inputArray
	 * @return String that represents title
	 */
	public String getTitle(int titleEndIndex, ArrayList<String> inputArray) {
		String title = "";
		int i = 0;

		do {
			if (i != titleEndIndex) {
				title = title + inputArray.get(i) + " ";
				i++;
			} else {
				title = title + inputArray.get(i);
				i++;
			}
		} while (i <= titleEndIndex);
		return title;
	}

	/**
	 * This method is used to generate the dateAndTimeFormats array from
	 * dateFormats and timeFormats
	 * 
	 * @return dateAndTimeFormats array
	 */
	private String[] getDateAndTimeFormats() {
		String[] dateTimeFormats = new String[dateFormats.length
				* timeFormats.length];
		int current = 0;
		for (int i = 0; i < dateFormats.length; i++) {
			for (int j = 0; j < timeFormats.length; j++) {
				dateTimeFormats[current] = dateFormats[i] + " "
						+ timeFormats[j];
				current++;
			}
		}
		return dateTimeFormats;
	}

	/**
	 * This method checks whether the task inside the inputArray is a whole day
	 * task or not, i.e. contains only one specific date, without any time
	 * 
	 * @param dateArray
	 * @return boolean
	 */
	public boolean isWholeDayTask(ArrayList<String> dateArray) {
		if (dateArray.size() == 1) {
			if (isDate(dateArray.get(0).toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method checks whether the task in the inputArray is a deadline task
	 * or not, i.e. contains only one specific time
	 * 
	 * @param dateArray
	 * @return boolean
	 */
	public boolean isDeadlineTask(ArrayList<String> dateArray) {
		boolean check = false;

		// if there are Time associated with the task
		if (!dateArray.isEmpty()) {
			String firstParameter = dateArray.get(0).toUpperCase();
			if (dateArray.size() == 1) {
				if (isTime(firstParameter)) {
					check = true;
				}
			} else if (dateArray.size() == 2) {
				String secondParameter = dateArray.get(1).toUpperCase();

				if ((isTime(firstParameter) && isDate(secondParameter) || (isDate(firstParameter) && isTime(secondParameter)))) {
					check = true;
				}
			}
		}
		return check;
	}

	/**
	 * This method checks whether the task inside the inputArray is a floating
	 * task, i.e. has no time or date indicated
	 * 
	 * @param dateArray
	 * @return boolean
	 */
	public boolean isFloatingTask(ArrayList<String> dateArray) {
		if (dateArray.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method checks whether the string of parameter is date in a certain
	 * format
	 * 
	 * @param string
	 * @return boolean isDate
	 */
	public boolean isDate(String str) {
		boolean isdate = false;
		if (str == null) {
			return false;
		}
		Date testDate = null;

		for (String format : dateFormats) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format,
					Locale.UK);
			dateFormat.setLenient(false); // ensure that the date is valid
			try {
				testDate = dateFormat.parse(str.trim());
				if (testDate != null) {
					if (dateFormat.format(testDate).equals(str)) {
						isdate = true;
					}
				}
			} catch (ParseException pe) {
				// do nothing
			}
		}
		return isdate;
	}

	/**
	 * This method checks whether the string of parameter is time in a certain
	 * format
	 * 
	 * @param string
	 * @return boolean isTime
	 */
	public boolean isTime(String str) {
		boolean istime = false;

		if (str == null) {
			return false;
		}
		Date testTime = null;

		for (String format : timeFormats) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format,
					Locale.UK);
			dateFormat.setLenient(false); // ensure that the date is valid
			try {
				testTime = dateFormat.parse(str.trim());
				if (testTime != null) {
					istime = true;
					if (!dateFormat.format(testTime).equals(str)) {
						istime = false;
					}
				}
			} catch (ParseException pe) {
				// do nothing
			}
		}
		return istime;
	}

	/**
	 * This method checks whether a certain string is an integer number
	 * 
	 * @param string
	 * @return boolean true or false
	 */
	public boolean isInt(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * This method checks whether a certain time has passed
	 * 
	 * @param element
	 * @return true or false
	 */
	public boolean isPassedTime(Date date) {
		Date nowTime = combineDateTimeStrToDate(getTodayDate(), getNowTime());
		if (date.compareTo(nowTime) > 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method checks whether a string is "to" or "-" 
	 * 
	 * @param inputArray
	 * @return the remaining inputArray
	 */
	public boolean isTimeIntervalPrep(String token) {
		if ((token.equals("TO")) || (token.equals("-"))) {
			return true;
		}
		return false;
	}

	/**
	 * This method checks whether the user input contains more than two dates,
	 * which makes the task becomes invalid
	 * 
	 * @param inputArray
	 * @return true or false
	 */
	public boolean hasMoreThanTwoInputDates(ArrayList<String> inputArray) {
		int countDate = 0;
		int countTime = 0;
		for (int i = 0; i < inputArray.size(); i++) {
			String element = inputArray.get(i).toUpperCase();
			if (isDate(element)) {
				countDate++;
			} else if (isTime(element)) {
				countTime++;
			}
		}

		if ((countDate > 2) || (countTime > 2)) {
			return true;
		}
		return false;
	}

	/**
	 * This method separates each token in a string and put them into an
	 * ArrayList
	 * 
	 * @param inputString
	 * @return the array list
	 */
	public ArrayList<String> tokenizeStrBySpace(String inputString) {
		String[] array = inputString.split(" ");
		ArrayList<String> aList = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			aList.add(array[i]);
		}
		return aList;
	}

	/**
	 * This method converts a time string into a Date
	 * 
	 * @param time
	 *            String
	 * @return Date
	 */
	public Date convertTimeStrToDate(String time) {
		if (time == null) {
			assert false;
		}
		Date testDate = null;

		for (String format : timeFormats) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format,
					Locale.UK);
			dateFormat.setLenient(false); // ensure that the date is valid
			try {
				testDate = dateFormat.parse(time.trim());
			} catch (ParseException pe) {
				// do nothing
			}
		}
		return testDate;
	}

	/**
	 * This method converts a date String to a Date
	 * 
	 * @param date
	 *            String
	 * @return Date
	 */
	public Date convertDateStrToDate(String date) {
		if (date == null) {
			assert false;
		}
		Date testDate = null;

		for (String format : dateFormats) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format,
					Locale.UK);
			dateFormat.setLenient(false); // ensure that the date is valid
			try {
				testDate = dateFormat.parse(date.trim());
			} catch (ParseException pe) {
				// do nothing
			}
		}
		return testDate;
	}
}