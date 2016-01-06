package echo.logicHandler;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

/**
 * @author Liu Yuanrui A0091752A
 * 
 */
public class EchoTaskHandlerTest {

	static EchoTaskHandler taskHandler;

	@Test
	public void testAddActiveTask() throws Exception {
		taskHandler = new EchoTaskHandler();
		Date date1 = Calendar.getInstance().getTime();
		date1.setDate(date1.getDate() + 1);
		Date date2 = Calendar.getInstance().getTime();
		;
		date2.setDate(date2.getDate() + 2);
		String title;
		do {
			Random randGen = new Random();
			char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
					+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
			int length = 4;
			char[] randBuffer = new char[length];
			for (int i = 0; i < randBuffer.length; i++) {
				randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
			}
			title = new String(randBuffer);
		} while (taskHandler.searchActiveTask(title) != -1);
		EchoTask newTask = new EchoTask(title, date1, date2);
		taskHandler.addActiveTask(newTask);
		assertEquals("Test Case 1 - Add Avtive Task", 1,
				taskHandler.searchActiveTask(title));
	}


	@Test
	public void testDeleteActiveTask() throws Exception {
		taskHandler = new EchoTaskHandler();
		String str = taskHandler.getActiveTaskAt(1);
		int count1 = taskHandler.searchActiveTask(str);
		taskHandler.deleteActiveTask(1);
		int count2 = taskHandler.searchActiveTask(str);
		boolean b;
		if (count2 == -1) {
			b = true;
		} else if (count1 - 1 == count2) {
			b = true;
		} else {
			b = false;
		}

		assertTrue("Test Case 2 - Delete Active Task", b);
	}

	@Test
	public void testDoneTask() throws Exception {
		taskHandler = new EchoTaskHandler();
		assert !taskHandler.getListModelForActiveTasks().isEmpty();
		String str;
		int i = 0;
		int j;
		do {
			i++;
			str = taskHandler.getListModelForActiveTasks().getElementAt(i)
					.getTitle();

		} while ((j = taskHandler.searchCompletedTask(str)) != -1
				&& i <= taskHandler.getListModelForActiveTasks().getSize());
		if (j != -1) {
			taskHandler.doneActiveTask(i);
			assertEquals("Test Case 3 - Done Active Task", j + 1,
					taskHandler.searchCompletedTask(str));
		} else {
			taskHandler.doneActiveTask(i);
			assertEquals("Test Case 3 - Done Active Task", 1,
					taskHandler.searchCompletedTask(str));
		}

	}

	@Test
	public void testUndoneTask() throws Exception {
		taskHandler = new EchoTaskHandler();
		assert !taskHandler.getListModelForCompletedTasks().isEmpty();
		String str;
		int i = 0;
		int j;
		do {
			i++;
			str = taskHandler.getListModelForCompletedTasks().getElementAt(i)
					.getTitle();
		} while ((j = taskHandler.searchActiveTask(str)) != -1
				&& i <= taskHandler.getListModelForCompletedTasks().getSize());
		if (j != -1) {
			taskHandler.undoneTask(i);
			assertEquals("Test Case 4 - Undone Active Task", j + 1,
					taskHandler.searchActiveTask(str));
		} else {
			System.out.println(str);
			System.out.println(taskHandler.getListModelForCompletedTasks());
			System.out.print(i);
			taskHandler.undoneTask(i);
			
			assertEquals("Test Case 4 - Undone Active Task", 1,
					taskHandler.searchActiveTask(str));
		}
	}
	
	@Test
	public void testActionListener() throws Exception {
		taskHandler = new EchoTaskHandler();
		Date currentTime = Calendar.getInstance().getTime();
		currentTime.setSeconds(currentTime.getSeconds() + 20);
		EchoTask newTask = new EchoTask("Recent", currentTime, currentTime);
		taskHandler.addActiveTask(newTask);
		long t0, t1;
		t0 = System.currentTimeMillis();
		do {
			t1 = System.currentTimeMillis();
		} while ((t1 - t0) < (60 * 1000));
		assertEquals("Test Case 5 - Add Due Task", 1,
				taskHandler.searchDueTask("Recent"));

	}

}
