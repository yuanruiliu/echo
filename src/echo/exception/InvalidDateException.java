/**
 *  Custom exception class that descends from Java's Exception class.
 *  Handles all the invalid date exception, including the input date has already passed, and invalid date format
 *  @author A0091684
 */
package echo.exception;

public class InvalidDateException extends Exception {
	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;
	String mistake;
	
	// Default constructor - initializes instance variable to unknown
	public InvalidDateException() {
		super(); // call superclass constructor
		mistake = "unknown";
	}
	
	// Constructor receives some kind of message that is saved in an instance variable
	public InvalidDateException(String err) {
		super(err); // call superclass constructor
		mistake = err; // save message
	}
	
	// Public method, callable by exception catcher. 
	// It returns the error message.
	public String getError() {
		return mistake;
	}
}
