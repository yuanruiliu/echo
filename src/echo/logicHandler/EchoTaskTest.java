package echo.logicHandler;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

/**
 * @author Liu Yuanrui A0091752A
 * 
 */
public class EchoTaskTest {

	public String getRandomTitle() {
		Random randGen = new Random();
		char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
				+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		int length = 4;
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		String title = new String(randBuffer);
		return title;

	}

	@Test
	public void testFloatingTask() {
		String title = this.getRandomTitle();
		EchoTask ft = new EchoTask(title, null, null);
		assertEquals("Test Case 1 - Floating Task: title", title, ft.getTitle());
		assertEquals("Test Case 1 - Floating Task: start time", "null null",
				ft.getStartDateInString() + " " + ft.getStartTimeInString());
		assertEquals("Test Case 1 - Floating Task: end time", "null null",
				ft.getEndDateInString() + " " + ft.getEndTimeInString());
	}

	@Test
	public void testWholeDayTask() {
		String title = this.getRandomTitle();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Date dateWithoutTime = new Date(sdf.format(Calendar.getInstance()
				.getTime()));
		EchoTask wt = new EchoTask(title, dateWithoutTime, dateWithoutTime);
		assertEquals("Test Case 2 - Whole Day Task:title", title, wt.getTitle());
		assertEquals("Test Case 2 - Whole Day Task:start date",
				dateWithoutTime, wt.getStartDate());
		assertEquals("Test Case 2 - Whole Day Task:end date", dateWithoutTime,
				wt.getEndDate());

	}

	@Test
	public void testTaskWithStartTimeAndEndTask() {
		String title = this.getRandomTitle();
		Date startDate = Calendar.getInstance().getTime();
		startDate.setHours(startDate.getHours() + 1);
		Date endDate = Calendar.getInstance().getTime();
		endDate.setHours(endDate.getHours() + 3);
		EchoTask nt = new EchoTask(title, startDate, endDate);
		assertEquals("Test Case 3 - Normal Task:title", title, nt.getTitle());
		assertEquals("Test Case 3 - Normal Task:start date", startDate,
				nt.getStartDate());
		assertEquals("Test Case 3 - Normal Task:end date", endDate,
				nt.getEndDate());
	}

}
