package echo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileSystemView;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.TabbedPaneStyle;
import com.alee.utils.swing.Timer;
import com.bric.swing.TransitionTabbedPane;

import echo.commandHandler.EchoFeedback;
import echo.commandHandler.EchoCommandHandler;
import echo.logicHandler.EchoTask;

/**
 * This class defines the user interface of echo
 * The user interface consists of a tabbed interface that dispaly list of tasks
 * and a text input interface that access / modify the list of tasks
 * 
 * Uses pattern similar Model-View-Controller where the listModel represents the model.
 * The controller is somewhat represented by echoCommandHandler which manipulates the 
 * content of listModel.
 * 
 * An event will be fired every time there is a change in the listModel and this 
 * will update the view (represented by JList). The event firing mechanism is already
 * implemented by Java in the DefaultListModel class so we don't need to write any 
 * implementation code for it.
 *
 * @author David A0083545B
 *
 */
public class WindowManager {
	private static final int WINDOW_WIDTH = 420;
	private static final int WINDOW_HEIGHT = 680;
	
	//--------------------- Color constants -------------------------------------//
	private static final Color LIGHT_GRAY = new Color(240, 240, 240);
	private static final Color DARK_GRAY = new Color(70,70,70);
	private static final Color GRAY = new Color(195,195,195);
	private static final Color LIGHT_BLUE = new Color(237, 246, 255);
	private static final Color LIGHT_RED = new Color(242, 230, 235);


	//--------------------- Font constants --------------------------------------//

	private static final Font SEGOE_UI = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font SEGOE_UI_LARGE = new Font("Segoe UI", Font.PLAIN, 17);

	//--------------------- Search tab index ------------------------------------//
	private static final int TAB_SEARCH = 3;

	//--------------------- Help messages ---------------------------------------//
	private static final String helpMessage = 
			"\n" +
			"Adding a task ( date and time are optional ) :\n" +
					"<task name> <start date> <start time> to\n" +
				    "                      <end date>  <end time>\n\n" +
			"Deleting a task : del <task no>\n\n" +
			"Move completed task : done <task no>\n\n" +
			"Revert completed task : undone <task no>\n\n" +
			"Undo : [CTRL + Z]\n\n" +
			"Redo : [CTRL + Y]\n\n" +
			"Switch tabs : [CTRL + <tab no>]\n\n" +
			"Close program : exit\n\n";
				   
	// List models contains EchoTask objects
	private DefaultListModel<EchoTask> listModelForActiveTasks;
	private DefaultListModel<EchoTask> listModelForDueTasks;
	private DefaultListModel<EchoTask> listModelForCompletedTasks;
	private DefaultListModel<EchoTask> listModelForSearchedTasks;

	// List object displays tasks in the model
	private JList<EchoTask> listForActiveTasks;
	private JList<EchoTask> listForDueTasks;
	private JList<EchoTask> listForCompletedTasks;
	private JList<EchoTask> listForSearchedTasks;

	private JScrollPane scrollPaneForActiveTasks;
	private JScrollPane scrollPaneForDueTasks;
	private JScrollPane scrollPaneForCompletedTasks;
	private JScrollPane scrollPaneForSearchedTasks;	

	private TransitionTabbedPane tabs;
	private WebPanel tabPanel;
	private JTextField inputField;
	private JLabel labelEcho;

	private JPanel panel;	
	private JFrame frame;
	private JWindow popUpNotification;
	private JWindow popUpHelp;

	private EchoCommandHandler echoCommandHandler;

	private Timer popUpTimer;

	private Logger logger;

	public WindowManager() {
		initLogger();
		
		initCommandHandler();

		initListModels();
		initLists();
		initScrollPanes();	

		initTabs();
		initInputField();
		initTitle();

		initPanel();
		initFrame();		
		initPopUps();

		initShortcuts();
		initListeners();
	}

	public void initListeners() {
		setListListener();
		setCommandListener();
		setHelpListener();
		setMouseListener();
		setHistoryManager();		
	}
		
	public void setHelpListener() {
		DocumentListener helpListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				hideHelp();
				logger.finest("inputField content changed");
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (inputField.getText().equals("?")) {
					showHelp();
				}
				logger.finest("inputField content inserted");
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				hideHelp();
				logger.finest("inputField content removed");
			}
		};

		assert helpListener != null;
		inputField.getDocument().addDocumentListener(helpListener);
	}

	public void showHelp() {
		JTextArea helpContent = new JTextArea(helpMessage);
		helpContent.setEditable(false);		
		helpContent.setFocusable(false);
		helpContent.setFont(SEGOE_UI);		
		helpContent.setBackground(LIGHT_BLUE);
		helpContent.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(new Color(0,0,0), 1), 			
				BorderFactory.createEmptyBorder(5, 25, 0, 0)));	
		popUpHelp.setContentPane(helpContent);
		popUpHelp.setVisible(true);	
	}

	public void hideHelp() {
		popUpHelp.setVisible(false);
	}

	/**
	 * Since we set JFrame to undecorated, there's no title bar on top that user can 
	 * click on and drag to move jFrame.
	 * 
	 * So we set a mouse listener to re-enable this capability
	 */
	public void setMouseListener() {
		WindowMover windowMover = new WindowMover(frame);
		ComponentAdapter popUpMover = new ComponentAdapter() {
			// update the position of popUp windows when the frame object moves
			public void componentMoved(ComponentEvent e) {
				logger.finest("window frame moved");
				
				popUpNotification.setVisible(false);
				popUpHelp.setVisible(false);
				initPopUps();				
			}
		};
		frame.addMouseListener(windowMover);
		frame.addMouseMotionListener(windowMover);
		frame.addComponentListener(popUpMover);
	}

	/**
	 * remembers the user input history and sets up listeners so users can navigate 
	 * between previous commands by using [UP] and [DOWN] arrow key
	 */
	public void setHistoryManager() {
		HistoryManager historyManager = new HistoryManager();
		inputField.addKeyListener(historyManager);
		inputField.addActionListener(historyManager);
	}

	// set a listener to execute the command when the user 
	// press <Enter> key in the inputField
	public void setCommandListener() {
		ActionListener commandListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				logger.finest("enter key pressed in inputField");
				
				String command = inputField.getText();
				EchoFeedback feedback = executeCommand(command);
				showNotification(feedback);
				updateTabs(feedback);
				updateInputField(feedback);
			}
		};

		assert commandListener != null;
		inputField.addActionListener(commandListener);
	}

	/**
	 * Executes the command based on user input by:
	 * calling the parseCommand() method of echoCommandHandler, which
	 * returns an EchoFeedback object which represents the result of the parsing
	 */
	public EchoFeedback executeCommand(String input) {
		/*
		 * currentTab is for echoCommandHandler to decide on the appropriate actions
		 * e.g. if user is currently viewing 'completed' tab, then 'done' command 
		 * is not really appropriate
		 */
		int currentTab = tabs.getSelectedIndex();
		EchoFeedback feedback = echoCommandHandler.parseCommand(input, currentTab);
		return feedback;
	}
		
	public void updateTabs(EchoFeedback feedback) {
		// switch tab to 'search' tab if user enter 'search' command
		// and the no of matching tasks is more than zero
		if (feedback.getCommandType().equals("search") &&
				feedback.isSuccess()) {			
			tabs.setSelectedIndex(TAB_SEARCH);
		}
		updateTabTitles();
	}

	// update the no of tasks in each tabs
	public void updateTabTitles() {
		setTabTitle(0, "active", listModelForActiveTasks.size());
		setTabTitle(1, "due", listModelForDueTasks.size());
		setTabTitle(2, "completed", listModelForCompletedTasks.size());
		setTabTitle(3, "search", listModelForSearchedTasks.size());
	}

	/**
	 * Returns an ActionListener object that reduces the opacity of 
	 * popUpNotification by 5% every time the Timer object fires an event
	 * 
	 * @return ActionListener object that fades out popUp window
	 */
	public ActionListener getTimerListener() {
		ActionListener timerListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				logger.finest("fading out the popUp");
				
				float popUpOpacity = popUpNotification.getOpacity();
				if (popUpOpacity > 0.05) {
					popUpNotification.setOpacity(popUpOpacity - (float) 0.05);
				} else {
					popUpNotification.setVisible(false);
					popUpTimer.stop();
				}
			}
		};
		return timerListener;
	}

	/**
	 * When the content of the list changes, this listener updates the 
	 * tab title i.e. the no of tasks in the corresponding tab
	 */
	public void setListListener() {
		ListDataListener listDataListener = new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				updateTabTitles();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				updateTabTitles();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				updateTabTitles();
			}
		};

		listModelForDueTasks.addListDataListener(listDataListener);
		listModelForActiveTasks.addListDataListener(listDataListener);
	}

	/**
	 * handles keyboard shortcuts for:
	 * 1. switching between tabs [CTRL] + [TAB NO]
	 * 2. undo [CTRL] + [Z]
	 * 3. redo [CTRL] + [Y]
	 */
	public void initShortcuts() {
		InputMap inputFieldInputMap = inputField.getInputMap();
		ActionMap inputFieldActionMap = inputField.getActionMap();

		ShortcutManager.setShortcutManager(tabs, inputField, 
				inputFieldInputMap,inputFieldActionMap);
	}

	public void initLogger() {
		logger = Logger.getLogger(WindowManager.class.getName());
		
		// get the Documents folder path
		JFileChooser fileChooser = new JFileChooser();
		FileSystemView fileSystemView = fileChooser.getFileSystemView();
		String myDocumentDirectory = fileSystemView.getDefaultDirectory().toString();
		String logDirectory = myDocumentDirectory + "\\Echo";
		
		try {
			File logDir = new File(logDirectory);
			// create Echo directory in Documents folder, if it is non-existent
			logDir.mkdirs();
			// write log data to Echo.log inside the Echo directory
			Handler handler = new FileHandler(logDirectory + "\\gui.log", true);
			logger.addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace(System.err);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void initCommandHandler() {
		echoCommandHandler = new EchoCommandHandler();
	}

	public void initPopUps() {
		// getWidth() is subtracted by 4 for better alignment
		int notificationWidth = inputField.getWidth() - 4;
		int notificationHeight = inputField.getHeight();
		
		int helpWidth = inputField.getWidth() - 4;
		int helpHeight = inputField.getHeight() * 11;
		
		popUpNotification = getPopUp(notificationWidth, notificationHeight);
		popUpHelp = getPopUp(helpWidth, helpHeight);
	}
	
	public JWindow getPopUp(int width, int height) {
		// popUp window location relative to frame component
		JWindow popUp = new JWindow(frame);
		
		int margin = 2;
		Point bottomLeft = inputField.getLocationOnScreen();
		Point aboveInputField = new Point(
				(int) bottomLeft.getX() + margin, 
				(int) bottomLeft.getY() - height - margin);
		
		popUp.setSize(width, height);
		popUp.setLocation(aboveInputField);
		return popUp;
	}

	public void initFrame() {
		frame = new JFrame("Echo");
		frame.setContentPane(panel);
		frame.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		frame.setUndecorated(true);

		URL echoIconURL = WindowManager.class.getResource("/echo.png");
		Image echoIcon = new ImageIcon(echoIconURL).getImage();
		assert echoIcon != null;
		frame.setIconImage(echoIcon);	

		// set frame to center of screen		
		int avgTaskbarHeight = 10;
		Dimension dim = frame.getToolkit().getScreenSize();		
		int topLeftX = dim.width/2 - frame.getWidth()/2;
		int topLeftY = dim.height/2 - frame.getHeight()/2 - avgTaskbarHeight;
		frame.setLocation(topLeftX, topLeftY);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void initPanel() {
		// assign border layout to manages the position of components in the panel
		panel = new JPanel(new BorderLayout());
		panel.add(labelEcho, BorderLayout.NORTH);
		panel.add(tabPanel, BorderLayout.CENTER);
		panel.add(inputField, BorderLayout.SOUTH);

		// set outer and inner margins
		panel.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(GRAY, 1),
				BorderFactory.createMatteBorder(10, 10, 15, 10, LIGHT_GRAY)));
	}

	public void initTitle() {
		labelEcho = new JLabel("Echo Task Manager", JLabel.CENTER);
		labelEcho.setFont(SEGOE_UI_LARGE);
		labelEcho.setForeground(DARK_GRAY);		
		labelEcho.setBackground(LIGHT_GRAY);	
		labelEcho.setOpaque(true);

		// set margin
		labelEcho.setBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, new Color(0,0,0,0))); // set margin
	}

	public void initTabs() {
		tabs = new TransitionTabbedPane();
		tabs.setTabbedPaneStyle(TabbedPaneStyle.attached);
		tabs.setFont(SEGOE_UI);
		tabs.setFocusable(false);

		tabs.add("active (0)", scrollPaneForActiveTasks);
		tabs.add("due (0)", scrollPaneForDueTasks);
		tabs.add("completed (0)", scrollPaneForCompletedTasks);
		tabs.add("search (0)", scrollPaneForSearchedTasks);

		tabPanel = new WebPanel(true, tabs);
	}

	public void initInputField() {
		inputField = new JTextField();
		inputField.setFont(SEGOE_UI);
		inputField.setMargin(new Insets(3,3,3,3));
	}

	public void initScrollPanes() {
		scrollPaneForActiveTasks = new JScrollPane(listForActiveTasks);
		scrollPaneForDueTasks = new JScrollPane(listForDueTasks);
		scrollPaneForCompletedTasks = new JScrollPane(listForCompletedTasks);
		scrollPaneForSearchedTasks = new JScrollPane(listForSearchedTasks);

		scrollPaneForActiveTasks.setBorder(BorderFactory.createEmptyBorder());
		scrollPaneForDueTasks.setBorder(BorderFactory.createEmptyBorder());
		scrollPaneForCompletedTasks.setBorder(BorderFactory.createEmptyBorder());	
		scrollPaneForSearchedTasks.setBorder(BorderFactory.createEmptyBorder());
	}

	public void initLists() {
		// ensure that echoCommandHandler returns the correct models
		assert listModelForActiveTasks != null;
		assert listModelForDueTasks != null;
		assert listModelForCompletedTasks != null;
		assert listModelForSearchedTasks != null;

		listForActiveTasks = new JList<>(listModelForActiveTasks);
		listForDueTasks	= new JList<>(listModelForDueTasks);
		listForCompletedTasks = new JList<>(listModelForCompletedTasks);
		listForSearchedTasks = new JList<>(listModelForSearchedTasks);

		// manages how the tasks are displayed in the list
		TaskListRenderer taskListRenderer = new TaskListRenderer();

		listForActiveTasks.setCellRenderer(taskListRenderer);
		listForDueTasks.setCellRenderer(taskListRenderer);
		listForCompletedTasks.setCellRenderer(taskListRenderer);
		listForSearchedTasks.setCellRenderer(taskListRenderer);

		// ensure that textField is always focused by disabling focus on other components
		listForActiveTasks.setFocusable(false);
		listForDueTasks.setFocusable(false);
		listForCompletedTasks.setFocusable(false);
		listForSearchedTasks.setFocusable(false);
	}

	public void initListModels() {
		listModelForActiveTasks = echoCommandHandler.getListModelForActiveTasks();
		listModelForDueTasks = echoCommandHandler.getListModelForDueTasks();
		listModelForCompletedTasks = echoCommandHandler.getListModelForCompletedTasks();
		listModelForSearchedTasks = echoCommandHandler.getListModelForSearchedTasks();
	}

	public void updateInputField(EchoFeedback feedback) {
		if (feedback.getCommandType().equals("edit")) {
			logger.finest("edit feedback : " + feedback.getTaskToBeEdited());
			inputField.setText(feedback.getTaskToBeEdited());
		} else {
			inputField.setText("");
		}
	}	

	// update the total no of items in the corresponding tab
	public void setTabTitle(int index, String title, int size) {
		tabs.setTitleAt(index, title + " (" + size + ")");
	}

	public void fadepopUpNotification() {
		if (popUpTimer == null) {
			popUpTimer = new Timer(8, getTimerListener());
			popUpTimer.setInitialDelay(2250);
		}
		popUpTimer.restart();
	}

	public void showNotification(EchoFeedback feedback) {
		JLabel label = new JLabel(feedback.getMessage());
		label.setFont(SEGOE_UI);				
		label.setOpaque(true);		
		label.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(new Color(0,0,0), 1), 			// set black outer border
				BorderFactory.createEmptyBorder(0, 10, 0, 0)));	// set left padding
		if (feedback.isSuccess()) {
			label.setBackground(LIGHT_BLUE);			
		} else {
			label.setBackground(LIGHT_RED);
		}

		popUpNotification.setContentPane(label);				
		popUpNotification.setOpacity(1);
		popUpNotification.setVisible(true);		
		
		fadepopUpNotification();
	}

	//-------------------- getter methods for JUnit Testing ------------------------------//
	public DefaultListModel<EchoTask> getListModelForActiveTasks() {
		return listModelForActiveTasks;
	}

	public DefaultListModel<EchoTask> getListModelForDueTasks() {
		return listModelForDueTasks;
	}

	public DefaultListModel<EchoTask> getListModelForCompletedTasks() {
		return listModelForCompletedTasks;
	}

	public DefaultListModel<EchoTask> getListModelForSearchedTasks() {
		return listModelForSearchedTasks;
	}

	public JScrollPane getScrollPaneForActiveTasks() {
		return scrollPaneForActiveTasks;
	}

	public JScrollPane getScrollPaneForDueTasks() {
		return scrollPaneForDueTasks;
	}

	public JScrollPane getScrollPaneForCompletedTasks() {
		return scrollPaneForCompletedTasks;
	}

	public JScrollPane getScrollPaneForSearchedTasks() {
		return scrollPaneForSearchedTasks;
	}

	public TransitionTabbedPane getTabs() {
		return tabs;
	}

	public JTextField getInputField() {
		return inputField;
	}

	public JLabel getLabelEcho() {
		return labelEcho;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JFrame getFrame() {
		return frame;
	}

	public JWindow getPopUpNotification() {
		return popUpNotification;
	}

	public JWindow getPopUpHelp() {
		return popUpHelp;
	}

	public EchoCommandHandler getEchoCommandHandler() {
		return echoCommandHandler;
	}

	public Timer getPopUpTimer() {
		return popUpTimer;
	}
	//-------------------- end getter methods --------------------------------------------//
	
	public static void main(String[] args) {
		// initialise WebLookAndFeel (skin for echo)
		WebLookAndFeel.install();
		WindowManager window = new WindowManager();
		
		// update tab titles after querying tasks from local .csv files	(if exist)	
		window.updateTabTitles();
		
		// set focus to the command bar so user can type immediately
		window.inputField.grabFocus();
	}
}
