package echo.gui;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import echo.logicHandler.EchoTask;

/**
 * JUnit testing for TaskListRender class
 * This test is not exhastive, mostly concerned with the methods
 * used to check dates and times in TaskListRenderer
 * 
 * @author David A0083545B
 *
 */
public class TaskListRendererTest {
	private static TaskListRenderer renderer;

	@BeforeClass
	public static void setUpBeforeClass() {
		renderer = new TaskListRenderer();
	}

	@Test
	public void testIsAllDay() {
		assertTrue(renderer.isAllDay(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 11, 23, 59)));
		assertFalse(renderer.isAllDay(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 12, 23, 59)));
		assertFalse(renderer.isAllDay(
				new GregorianCalendar(2012, 11, 11, 0, 1), 
				new GregorianCalendar(2012, 11, 11, 23, 59)));
		assertFalse(renderer.isAllDay(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 11, 23, 58)));
		assertFalse(renderer.isAllDay(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 11, 0, 0)));
		assertFalse(renderer.isAllDay(
				new GregorianCalendar(2012, 11, 11, 23, 59), 
				new GregorianCalendar(2012, 11, 11, 23, 59)));
	}

	@Test
	public void testIsFloating() {		
		assertTrue(renderer.isFloating(
				new EchoTask("floating", null, null)
				));
		assertFalse(renderer.isFloating(
				new EchoTask("floating", new Date(), new Date())
				));
	}

	@Test
	public void testCountDigit() {
		assertEquals(1, renderer.countDigit(0));
		assertEquals(1, renderer.countDigit(1));
		assertEquals(1, renderer.countDigit(9));
		assertEquals(2, renderer.countDigit(10));
		assertEquals(2, renderer.countDigit(11));
		assertEquals(2, renderer.countDigit(19));
		assertEquals(3, renderer.countDigit(100));
		assertEquals(3, renderer.countDigit(999));
	}

	@Test
	public void testHasSameDate() {
		assertTrue(renderer.hasSameDate(
				new GregorianCalendar(2012, 11, 11), 
				new GregorianCalendar(2012, 11, 11)));
		assertTrue(renderer.hasSameDate(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 11)));
		assertTrue(renderer.hasSameDate(
				new GregorianCalendar(2012, 11, 11), 
				new GregorianCalendar(2012, 11, 11, 0, 0)));
		assertTrue(renderer.hasSameDate(
				new GregorianCalendar(2012, 11, 11, 10, 10), 
				new GregorianCalendar(2012, 11, 11, 15, 15)));
		assertFalse(renderer.hasSameDate(
				new GregorianCalendar(2012, 11, 11), 
				new GregorianCalendar(2012, 11, 12)));
		assertFalse(renderer.hasSameDate(
				new GregorianCalendar(2012, 11, 11), 
				new GregorianCalendar(2012, 12, 11)));
		assertFalse(renderer.hasSameDate(
				new GregorianCalendar(2012, 11, 11), 
				new GregorianCalendar(2013, 11, 11)));		
	}

	@Test
	public void testHasSameDateTime() {
		assertTrue(renderer.hasSameDateTime(
				new GregorianCalendar(2012, 11, 11), 
				new GregorianCalendar(2012, 11, 11)));
		assertTrue(renderer.hasSameDateTime(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 11, 0, 0)));
		assertFalse(renderer.hasSameDateTime(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 12, 0, 0)));
		assertTrue(renderer.hasSameDateTime(
				new GregorianCalendar(2012, 11, 11, 23, 59), 
				new GregorianCalendar(2012, 11, 11, 23, 59)));
		assertFalse(renderer.hasSameDateTime(
				new GregorianCalendar(2012, 11, 11, 23, 59), 
				new GregorianCalendar(2012, 11, 12, 23, 59)));
		assertFalse(renderer.hasSameDateTime(
				new GregorianCalendar(2012, 11, 11, 0, 0), 
				new GregorianCalendar(2012, 11, 11, 0, 1)));
		assertFalse(renderer.hasSameDateTime(
				new GregorianCalendar(2012, 11, 11, 23, 58), 
				new GregorianCalendar(2012, 11, 11, 23, 59)));
	}

}
