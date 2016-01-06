/**
 * This class carries out JUnit testing for EchoCommandHandler
 * 
 * @author A0091684W
 */
package echo.commandHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import echo.exception.InvalidDateException;
import echo.exception.InvalidParameterException;
import echo.exception.InvalidTitleException;
import echo.logicHandler.EchoTask;
import echo.logicHandler.EchoTaskHandler;

public class EchoCommandHandlerTest {

	EchoCommandHandler commandHandler = new EchoCommandHandler();

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void clearTasks() throws Exception {
		EchoTaskHandler taskHandler = new EchoTaskHandler();
		taskHandler.clear();
	}

	@Test
	public void testCheckEditParameters() throws InvalidParameterException {
		String input1 = "edit 2";
		String input2 = "edit 2.31";
		String input3 = "edit 0";
		ArrayList<String> array1 = commandHandler.tokenizeStrBySpace(input1);
		ArrayList<String> array2 = commandHandler.tokenizeStrBySpace(input2);
		ArrayList<String> array3 = commandHandler.tokenizeStrBySpace(input3);

		// Test Case 1 - Edit valid integer
		assertEquals("Test Case 1 - Edit valid integer", 1,
				commandHandler.getEditParameter(array1));

		// Test Case 2 - Edit non-integer, throw exception
		exception.expect(InvalidParameterException.class);
		commandHandler.getEditParameter(array2);
		
		// Test Case 3 - Edit invalid integer, throw exception
		exception.expect(InvalidParameterException.class);
		commandHandler.getEditParameter(array3);
	}

	@Test
	// @ExpectedException(class=WrapperException.class,
	// message="Exception Message", causeException)
	public void testCheckAddParameters() throws InvalidDateException,
			InvalidTitleException, NullPointerException {
		String title = "";
		Date startDate = null;
		Date endDate = null;
		Date expectedDate = null;
		Date expected = null;

		String input1 = "feed pusheen";
		String input2 = "feed pusheen 00:00";
		String input3 = "feed pusheen 12/12/2012";
		String input4 = "feed pusheen 12.12.2012 0000"; // different format of
														// date and time
		String input5 = "feed pusheen 00:00 - 13:20";
		String input6 = "feed pusheen 12/12/2012 - 13/12/2012";
		String input7 = "feed pusheen 12-12-12 03:00 - 13-12-12 13:20";
		String input8 = "";
		String input9 = "buy milk 20/1/12 10pm 11pm 12pm";

		ArrayList<String> array1 = commandHandler.tokenizeStrBySpace(input1);
		ArrayList<String> array2 = commandHandler.tokenizeStrBySpace(input2);
		ArrayList<String> array3 = commandHandler.tokenizeStrBySpace(input3);
		ArrayList<String> array4 = commandHandler.tokenizeStrBySpace(input4);
		ArrayList<String> array5 = commandHandler.tokenizeStrBySpace(input5);
		ArrayList<String> array6 = commandHandler.tokenizeStrBySpace(input6);
		ArrayList<String> array7 = commandHandler.tokenizeStrBySpace(input7);
		ArrayList<String> array8 = commandHandler.tokenizeStrBySpace(input8);
		ArrayList<String> array9 = commandHandler.tokenizeStrBySpace(input9);

		// Test Case 1 - Floating task
		commandHandler.getAddParameters(array1);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();
		assertEquals("Test Case 1 - Floating task, check title",
				"feed pusheen", title);
		assertEquals("Test Case 1 - Floating task, check start date", null,
				startDate);
		assertEquals("Test Case 1 - Floating task, check end date", null,
				endDate);

		// Test Case 2 - Deadline task with given time
		commandHandler.getAddParameters(array2);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();
		assertEquals(
				"Test Case 2 - Deadline task with given time, check title",
				"feed pusheen", title);

		expected = commandHandler.combineDateTimeStrToDate(
				commandHandler.getTomorrowDate(), "00:00");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 2 - Deadline task with given time, check start date",
				expectedDate, startDate);

		assertTrue(
				"Test Case 2 - Deadline task with given time, check whether start date equals end date",
				startDate.compareTo(endDate) == 0);

		// Test Case 3 - Whole day task
		commandHandler.getAddParameters(array3);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();
		assertEquals("Test Case 3 - Whole day task, check title",
				"feed pusheen", title);

		expectedDate = commandHandler.combineDateTimeStrToDate("12/12/2012",
				"00:00");
		assertEquals("Test Case 3 - Whole day task, check start date",
				expectedDate, startDate);

		assertEquals("Test Case 3 - Whole day task, check end date",
				commandHandler.combineDateTimeStrToDate("12/12/2012", "23:59"),
				endDate);

		// Test Case 4 - Deadline task with given date and time
		commandHandler.getAddParameters(array4);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();
		assertEquals(
				"Test Case 4 - Deadline task with given date and time, check title",
				"feed pusheen", title);

		expected = commandHandler
				.combineDateTimeStrToDate("12.12.2012", "0000");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 4 - Deadline task with given date and time, check start date",
				expectedDate, startDate);

		assertTrue(
				"Test Case 4 - Deadline task with given date and time, check whether start date equals end date",
				startDate.compareTo(endDate) == 0);

		// Test Case 5 - Task with given time interval
		commandHandler.getAddParameters(array5);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();

		assertEquals(
				"Test Case 5 - Task with given time interval, check title",
				"feed pusheen", title);

		expected = commandHandler.combineDateTimeStrToDate(
				commandHandler.getTomorrowDate(), "00:00");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 5 - Task with given time interval, check start date",
				expectedDate, startDate);

		expected = commandHandler.combineDateTimeStrToDate(
				commandHandler.getTomorrowDate(), "13:20");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 5 - Task with given time interval, check end date",
				expectedDate, endDate);

		// Test Case 6 - Task with given date interval
		commandHandler.getAddParameters(array6);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();
		assertEquals(
				"Test Case 6 - Task with given date interval, check title",
				"feed pusheen", title);

		expected = commandHandler
				.combineDateTimeStrToDate("12.12.2012", "0000");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 6 - Task with given date interval, check start date",
				expectedDate, startDate);

		expected = commandHandler
				.combineDateTimeStrToDate("13.12.2012", "2359");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 6 - Task with given date interval, check end date",
				expectedDate, endDate);

		// Test Case 7 - Task with given date and time interval
		commandHandler.getAddParameters(array7);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();
		assertEquals(
				"Test Case 7 - Task with given date and time interval, check title",
				"feed pusheen", title);

		expected = commandHandler.combineDateTimeStrToDate("12-12-12", "03:00");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 7 - Task with given date and time interval, check start date",
				expectedDate, startDate);

		expected = commandHandler.combineDateTimeStrToDate("13-12-12", "13:20");
		expectedDate = new Date(expected.getTime());
		assertEquals(
				"Test Case 7 - Task with given date and time interval, check end date",
				expectedDate, endDate);

		// Test Case 8 - Invalid task title
		exception.expect(NullPointerException.class);
		commandHandler.getAddParameters(array8);
		
		// Test Case 9 - Task with more than two input dates, throw exception
		exception.expect(InvalidDateException.class);
		commandHandler.getAddParameters(array9);
	}

	@Test
	/*
	 * Since the delete parameters are the same with done and undone, there is
	 * no need to test done and undone parameters
	 */
	public void testCheckDeleteParameters() throws Exception {

		String input1 = "feed pusheen";
		String input2 = "feed pusheen";
		String input3 = "feed pusheen";
		String input4 = "delete 2";
		String input5 = "delete 2 4 3";
		String input6 = "delete 2.31";
		String input7 = "delete 2 2 1";
		String input8 = "delete 0";
		ArrayList<String> array1 = commandHandler.tokenizeStrBySpace(input1);
		ArrayList<String> array2 = commandHandler.tokenizeStrBySpace(input2);
		ArrayList<String> array3 = commandHandler.tokenizeStrBySpace(input3);
		ArrayList<String> array4 = commandHandler.tokenizeStrBySpace(input4);
		ArrayList<String> array5 = commandHandler.tokenizeStrBySpace(input5);
		ArrayList<String> array6 = commandHandler.tokenizeStrBySpace(input6);
		ArrayList<String> array7 = commandHandler.tokenizeStrBySpace(input7);
		ArrayList<String> array8 = commandHandler.tokenizeStrBySpace(input8);

		// Test Case 1 - Delete valid integer when there is nothing added
		int size = commandHandler.getListModelForActiveTasks().size();
		if (size == 0) {
			exception.expect(NullPointerException.class);
			commandHandler.getTaskIndices(array4);
		}

		// Test Case 2 - Delete a valid task after adding some tasks
		try {
			addTask(array1);
			addTask(array2);
			addTask(array3);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Integer expected = 1;
		ArrayList<Integer> testIndex = commandHandler.getTaskIndices(array4);
		assertEquals("Test Case 2 - Delete valid integer", expected, testIndex.get(0));

		// Test Case 3 - Delete some tasks after adding some tasks
		try {
			addTask(array1);
			addTask(array2);
			addTask(array3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testIndex = commandHandler.getTaskIndices(array5);
		Integer expectedIndex1 = 1;
		Integer expectedIndex2 = 2;
		Integer expectedIndex3 = 3;
		assertEquals("Test Case 3 - Delete valid integer", expectedIndex1, testIndex.get(0));
		assertEquals("Test Case 3 - Delete valid integer", expectedIndex2, testIndex.get(1));
		assertEquals("Test Case 3 - Delete valid integer", expectedIndex3, testIndex.get(2));

		// Test Case 4 - Delete non-integer, throw exception
		exception.expect(InvalidParameterException.class);
		commandHandler.getTaskIndices(array6);
		
		// Test Case 5 - Delete duplicated integers
		try {
			addTask(array1);
			addTask(array2);
			addTask(array3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		testIndex = commandHandler.getTaskIndices(array7);
		expectedIndex1 = 1;
		expectedIndex2 = 2;
		assertEquals("Test Case 5 - Delete duplicate integers", expectedIndex1, testIndex.get(0));
		assertEquals("Test Case 5 - Delete duplicate integers", expectedIndex2, testIndex.get(1));
		
		// Test Case 6 - Delete invalid integer, throw exception
		exception.expect(InvalidParameterException.class);
		commandHandler.getTaskIndices(array8);
	}

	public void addTask(ArrayList<String> array1) throws Exception {
		String title = "";
		Date startDate = null;
		Date endDate = null;
		EchoTask newTask;
		EchoTaskHandler taskHandler = new EchoTaskHandler();

		commandHandler.getAddParameters(array1);
		title = commandHandler.getTitle();
		startDate = commandHandler.getStartDate();
		endDate = commandHandler.getEndDate();
		newTask = new EchoTask(title, startDate, endDate);
		taskHandler.addActiveTask(newTask);
	}

	@Test
	public void testCheckSearchParameters() {
		String input1 = "search meeting";
		ArrayList<String> array1 = commandHandler.tokenizeStrBySpace(input1);

		assertEquals("Test Case 1 - Search by title", "meeting",
				commandHandler.getSearchParameters(array1));
	}

}
