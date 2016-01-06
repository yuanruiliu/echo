/**
 *  Custom exception class that descends from Java's Exception class.
 *  Handles all the invalid title exception, for example the title is null
 *  @author A0091684
 */
package echo.exception;

public class InvalidTitleException extends Exception{

	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;
	String mistake;

	// Default constructor - initializes instance variable to unknown
	public InvalidTitleException() {
		super(); // call superclass constructor
		mistake = "unknown";
	}

	// Constructor receives some kind of message that is saved in an instance
	// variable
	public InvalidTitleException(String err) {
		super(err); // call superclass constructor
		mistake = err; // save message
	}

	// Public method, callable by exception catcher.
	// It returns the error message.
	public String getError() {
		return mistake;
	}
}
