/**
 *  Custom exception class that descends from Java's Exception class.
 *  Handles all the invalid parameter exception, including the input number of index is not an integer number
 *  @author A0091684
 */
package echo.exception;

public class InvalidParameterException extends Exception {
	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;
	String mistake;
	
	// Default constructor - initializes instance variable to unknown
	public InvalidParameterException() {
		super(); // call superclass constructor
		mistake = "unknown";
	}
	
	// Constructor receives some kind of message that is saved in an instance variable
	public InvalidParameterException(String err) {
		super(err); // call superclass constructor
		mistake = err; // save message
	}
	
	// Public method, callable by exception catcher. 
	// It returns the error message.
	public String getError() {
		return mistake;
	}
}
