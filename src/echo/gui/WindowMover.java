package echo.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

/**
 * This class enables a JFrame object to be moved around
 * by dragging the mouse even though the JFrame object has
 * been set to be undecorated
 * 
 * @author David A0083545B
 */
public class WindowMover extends MouseAdapter {
	Point mouseDownCompCoords;
	JFrame window;
	
	public WindowMover(JFrame jFrame) {
		window = jFrame;
	}
	
	public void mousePressed(MouseEvent e) {
		mouseDownCompCoords = e.getPoint();
	}
	
	public void mouseDragged(MouseEvent e) {
		Point currCoords = e.getLocationOnScreen();
		window.setLocation(currCoords.x - mouseDownCompCoords.x, 
				currCoords.y - mouseDownCompCoords.y);
	}
}
