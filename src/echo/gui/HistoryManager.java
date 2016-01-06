package echo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JTextField;

/**
 * This class manages the history of commands entered by the user
 * The behaviour is similar to how command line interfaces
 * handle command history
 * 
 * by pressing up and down arrow key, users can navigate
 * between previously typed commands
 * 
 * to use this, assign the HistoryManager object as the 
 * KeyListener and ActionListener to the events fired by JTextField
 * in WindowManager
 * 
 * @author David A0083545B
 */

public class HistoryManager implements KeyListener, ActionListener {
	
	Vector<String> history;
	int	currentIndex;
	JTextField textField;
	
	public HistoryManager() {
		history = new Vector<>();
		currentIndex = 0;
	}

	@Override
	public void actionPerformed(ActionEvent e) {	
		String command = e.getActionCommand(); 	// get the text inside JTextField
		history.addElement(command);
		currentIndex = history.size();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// up arrow key is pressed
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_KP_UP) {
			textField = (JTextField) e.getSource();
			if (currentIndex > 0) {
				textField.setText(history.get(--currentIndex));
			}
		}

		// down arrow key is pressed
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_KP_DOWN) {
			textField = (JTextField) e.getSource();
			if (currentIndex  < history.size() - 1) {
				textField.setText(history.get(++currentIndex));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// do nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// do nothing
	}
}
