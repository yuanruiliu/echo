/**
 *  Custom exception class that descends from Java's Exception class.
 *  Handles all the invalid command exception
 *  @author A0091684
 */

package echo.exception;

public class InvalidCommandException extends Exception {
	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;
	String mistake;
	
	// Default constructor - initializes instance variable to unknown
	public InvalidCommandException() {
		super(); // call superclass constructor
		mistake = "unknown";
	}
	
	// Constructor receives some kind of message that is saved in an instance variable
	public InvalidCommandException(String err) {
		super(err); // call superclass constructor
		mistake = err; // save message
	}
	
	// Public method, callable by exception catcher. 
	// It returns the error message.
	public String getError() {
		return mistake;
	}
}
