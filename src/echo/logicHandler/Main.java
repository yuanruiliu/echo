/**
 * 
 * @author Lokman Hakim A0087978Y
 * @description
 * 
 */
package echo.logicHandler;
import java.io.IOException;
import java.util.*;
import echo.commandHandler.EchoCommandHandler;

public class Main {

	public static void main(String[] args) throws Exception {
		//Testing grounds, to see if the methods really work
	
		EchoTask taskOne = new EchoTask();
		EchoTask taskTwo = new EchoTask();
		EchoTask taskThree = new EchoTask();
		
		taskOne.setStartTimeInString("19:05");
		taskOne.setStartDateInString("17-12-2012");
		
		taskTwo.setStartTimeInString("19:00");
		taskTwo.setStartDateInString("17-12-2012");
		
		taskThree.setStartTimeInString("23:00");
		taskThree.setStartDateInString("18-12-2012");
		
		/*System.out.println(taskOne.getStartDateInString());
		System.out.println(taskTwo.getStartDateInString());
		System.out.println(taskThree.getStartDateInString());
		
		System.out.println(taskOne.getStartTimeInString());
		System.out.println(taskTwo.getStartTimeInString());
		System.out.println(taskThree.getStartTimeInString());*/
		
		EchoTaskHandler taskHandler = new EchoTaskHandler();
//		taskHandler.addActiveTask(taskOne);
//		taskHandler.addActiveTask(taskThree);
//		taskHandler.addActiveTask(taskTwo);
		
		String title = "title";
		Date start = new Date(2012,12,10,10,10);
		Date end = new Date(2012,12,12,10,10);
		taskHandler.addActiveTask(new EchoTask(title, start, end));
		
//		for (int i = 0; i < taskHandler.getListModelForActiveTasks().size(); i ++) {
//			System.out.println(taskHandler.getListModelForActiveTasks().get(i));
//		}
	}
}
