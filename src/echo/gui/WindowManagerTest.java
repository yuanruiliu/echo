package echo.gui;

import static org.junit.Assert.*;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import org.junit.BeforeClass;
import org.junit.Test;

import echo.commandHandler.EchoFeedback;

/**
 * JUnit testing for WindowManager
 * This test mostly cover the initialisation of the objects in WindowManager,
 * making sure that they are properly constructed.
 * 
 * Testing the various interactions is not exhaustive because it requires testing
 * a lot of listeners (with their respective events) which may not be feasible
 * 
 * A proposed way to reasonably test the interaction is using the Robot class
 * which can emulate keyboard and mouse interaction but during our testing
 * the Robot object tend to behave in an erratic manner (guess that's the reason
 * why we are afraid that some day robots can dominate human race :P)
 * 
 * @author David A0083545B
 *
 */
public class WindowManagerTest {
	private static WindowManager window;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		window = new WindowManager();
	}	

	@Test
	public void testWindowManager() {
		assertNotNull(window);
	}	

	// test that the documentListener is properly added to the textField
	// by testing if popUpHelp is visible after "?" is entered into the textField
	@Test
	public void testSetHelpListener() {
		window.setHelpListener();				
		
		JWindow popUpHelp = window.getPopUpHelp();
		JTextField textField = window.getInputField();
		
		textField.setText("?");
		assert(popUpHelp.isVisible());
		
	}

	@Test
	public void testShowHelp() {
		JWindow popUpHelp = window.getPopUpHelp();
		popUpHelp.setVisible(false);	// to test that showHelp makes popUpHelp visible
		window.showHelp();
		assertTrue(popUpHelp.isVisible());
	}

	@Test
	public void testHideHelp() {
		JWindow popUpHelp = window.getPopUpHelp();
		popUpHelp.setVisible(true);		// to test that showHelp makes popUpHelp not visible
		window.hideHelp();
		assertFalse(popUpHelp.isVisible());
	}

	@Test(expected=NullPointerException.class)
	public void testExecuteInvalidCommand() {
		window.executeCommand(null);
	}

	@Test
	public void testExecuteValidCommand() {
		EchoFeedback feedback;

		feedback = window.executeCommand("");
		assertNotNull(feedback);

		feedback = window.executeCommand("command");
		assertNotNull(feedback);
	}

	@Test
	public void testGetTimerListener() {
		assertNotNull(window.getTimerListener());
	}

	@Test
	public void testInitCommandHandler() {
		window.initCommandHandler();
		assertNotNull(window.getEchoCommandHandler());
	}		

	@Test
	public void testInitFrame() {
		window.initFrame();
		assertNotNull(window.getFrame());
	}

	@Test
	public void testInitPanel() {
		window.initPanel();
		assertNotNull(window.getPanel());
	}

	@Test
	public void testInitTitle() {
		window.initTitle();
		assertNotNull(window.getLabelEcho());
	}

	@Test
	public void testInitTabs() {
		window.initTabs();
		assertNotNull(window.getTabs());
	}

	@Test
	public void testInitInputField() {
		window.initInputField();
		assertNotNull(window.getInputField());
	}

	@Test
	public void testInitScrollPanes() {
		window.initScrollPanes();
		assertNotNull(window.getScrollPaneForActiveTasks());
		assertNotNull(window.getScrollPaneForDueTasks());
		assertNotNull(window.getScrollPaneForCompletedTasks());
		assertNotNull(window.getScrollPaneForSearchedTasks());
	}

	@Test
	public void testInitListModels() {
		window.initListModels();
		assertNotNull(window.getListModelForActiveTasks());
		assertNotNull(window.getListModelForDueTasks());
		assertNotNull(window.getListModelForCompletedTasks());
		assertNotNull(window.getListModelForSearchedTasks());
	}

	@Test
	public void testUpdateInputField() {
		JTextField textField = window.getInputField();
		
		EchoFeedback editFeedback = new EchoFeedback("edit", "task", "message", true);
		window.updateInputField(editFeedback);
		assertEquals("task", textField.getText());
		
		EchoFeedback otherFeedback = new EchoFeedback("other");
		window.updateInputField(otherFeedback);
		assertEquals("", textField.getText());
	}

	@Test
	public void testSetTabTitle() {
		JTabbedPane tabs = window.getTabs();
		
		window.setTabTitle(0, "title", 1000);
		assertEquals("title (1000)", tabs.getTitleAt(0));
		
		window.setTabTitle(3, "", 0);
		assertEquals(" (0)", tabs.getTitleAt(3));
		
		window.setTabTitle(0, "title", -1000);
		assertEquals("title (-1000)", tabs.getTitleAt(0));
		
		window.setTabTitle(3, "title", 1000);
		assertNotSame("title (1000)", tabs.getTitleAt(2));
	}

	@Test
	public void testFadepopUpNotification() {
		window.fadepopUpNotification();
		assertNotNull(window.getPopUpTimer());
	}

	@Test
	public void testShowNotification() {
		EchoFeedback feedback = new EchoFeedback("command type");
		JWindow popUpNotification = window.getPopUpNotification();
		
		window.showNotification(feedback);
		assertNotNull(popUpNotification.getContentPane());
		assertTrue(popUpNotification.isVisible());
	}	
}
