package echo.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * This class manages keyboard shortcuts. Currently implemented shortcuts are:
 * 
 * [CTRL] + [TAB NO] 	:= switch to the respective tab no
 * [CTRL] + [Z]			:= undo 1 state
 * [CTRL] + [Y]			:= redo 1 state, only if undo has been done before
 * 
 * @author David A0083545B
 *
 */
public class ShortcutManager {
	// for singleton purpose
	private static ShortcutManager instance = null;
	
	private static final KeyStroke control1 = KeyStroke.getKeyStroke("control 1");
	private static final KeyStroke control2 = KeyStroke.getKeyStroke("control 2");
	private static final KeyStroke control3 = KeyStroke.getKeyStroke("control 3");
	private static final KeyStroke control4 = KeyStroke.getKeyStroke("control 4");
	private static final KeyStroke controlZ = KeyStroke.getKeyStroke("control Z");
	private static final KeyStroke controlY = KeyStroke.getKeyStroke("control Y");
	
	private Action switchToTab1;
	private Action switchToTab2;
	private Action switchToTab3;
	private Action switchToTab4;
	private Action undo;
	private Action redo;
	
	private JTabbedPane tabbedPane;
	private JTextField textField;
	private InputMap inputMap;
	private ActionMap actionMap;
	
	// ShortcutManager is a singleton, hence we use private constructor
	private ShortcutManager(JTabbedPane _tabbedPane, JTextField _textField,
			InputMap _inputMap, ActionMap _actionMap) {
		tabbedPane = _tabbedPane;
		textField = _textField;
		inputMap = _inputMap;
		actionMap = _actionMap;
		
		initActions();
		fillInputMap();
		initActionMap();
	}
	
	public static void setShortcutManager(JTabbedPane _tabbedPane, JTextField _textField,
			InputMap _inputMap, ActionMap _actionMap) {
		if (instance == null) {
			instance = new ShortcutManager(_tabbedPane, _textField, _inputMap, _actionMap);
		}
	}
	
	@SuppressWarnings("serial")
	private void initActions() {
		switchToTab1 = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTab(1);
			}
		};
		switchToTab2 = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTab(2);
			}
		};
		switchToTab3 = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTab(3);
			}
		};
		switchToTab4 = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTab(4);
			}
		};
		undo = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// emulate the user typing "undo" into the textField
				textField.setText("undo");
				textField.postActionEvent();
			}
		};
		redo = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// emulate the user typing "redo" into the textField
				textField.setText("redo");
				textField.postActionEvent();
			}
		};
	}

	// Add bindings for various keyStrokes to actionMapKey
	private void fillInputMap() {
		inputMap.put(control1, "switchToTab1");
		inputMap.put(control2, "switchToTab2");
		inputMap.put(control3, "switchToTab3");
		inputMap.put(control4, "switchToTab4");
		inputMap.put(controlZ, "undo");
		inputMap.put(controlY, "redo");
	}

	// Adds bindings for actionMapKey to action.
	private void initActionMap() {
		actionMap.put("switchToTab1", switchToTab1);
		actionMap.put("switchToTab2", switchToTab2);
		actionMap.put("switchToTab3", switchToTab3);
		actionMap.put("switchToTab4", switchToTab4);
		actionMap.put("undo", undo);
		actionMap.put("redo", redo);
	}

	private void selectTab(int i) {
		int tabIndex = i - 1;
		if (tabbedPane.isEnabledAt(tabIndex)) {
			tabbedPane.setSelectedIndex(tabIndex);
		}
	}
}
