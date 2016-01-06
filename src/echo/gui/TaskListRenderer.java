package echo.gui;
import java.awt.Component;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;

import echo.logicHandler.EchoTask;

/**
 * This class is resposible for the rendering of EchoTask object inside the model
 * Tasks occuring on the same date are grouped under the same "date heading"
 * Similarly, all floating tasks are grouped under "floating tasks heading"
 * 
 * Strictly same date means two tasks only have the same date (time may be different)
 * Strictly same date and time means that two tasks have both same date and time
 * 
 * @author David A0083545B
 * 
 */

// I don't think we need to serialize this class becuse we don't really need to represent
// this object in binary format and store it in a persistent state or over a network
@SuppressWarnings("serial")
class TaskListRenderer extends DefaultListCellRenderer {

	private static final Font SEGOE_UI = new Font("Segoe UI", Font.PLAIN, 14);

	private ListModel<EchoTask> model;
	private EchoTask task;
	private String taskTitle;
	private Calendar startDate;
	private Calendar endDate;
	private int size;
	private int index;
	private String heading;

	// assume that getListCellRenderer provides the list that contains correct types
	@SuppressWarnings({ "rawtypes", "unchecked" })

	public Component getListCellRendererComponent(JList list, Object _task, int _index, 
			boolean isSelected, boolean cellHasFocus) {
		model = list.getModel();

		assert _task instanceof EchoTask;
		task = (EchoTask) _task;	
		taskTitle = task.getTitle();
		startDate = getCalendar(task.getStartDate());
		endDate = getCalendar(task.getEndDate());

		size = model.getSize();
		index = _index;
		heading = getHeading();

		String cellContent = formatCell(heading, taskTitle, startDate, endDate);
		// we use html formatting to make coloring easier and to allow multi-lines on JLabel
		JLabel label = new JLabel("<html>" + cellContent + "</html>");
		label.setFont(SEGOE_UI);;

		return label;
	}

	// return the formatted content of each cell in the list
	public String formatCell(String heading, String taskTitle, Calendar startDate, Calendar endDate) {
		String result = "";

		result += formatHeading(heading);
		result += formatTaskTitleAndDate(taskTitle, startDate, endDate);

		return result;		
	}

	// return the formatted heading of each cell
	// if the cell does not require any heading, it will return an empty string
	public String formatHeading(String heading) {
		String formattedHeading = "";
		if (heading != null) {
			formattedHeading = 
					"<br/>" + addSpace(4) + 
					"<span style = 'font-size: 22pt; color: #4F8CDB;'>" +
					heading + 
					"</span>" +
					"<br/>";
		}
		return formattedHeading;
	}

	// return the formatted task title and date
	public String formatTaskTitleAndDate(String taskTitle, Calendar startDate, Calendar endDate) {
		String formattedTaskTitleAndDate = "";

		// add line no
		formattedTaskTitleAndDate += formatLineNo();
		// add the task title and date
		formattedTaskTitleAndDate += formatTitleAndDate(taskTitle, startDate, endDate);			

		return formattedTaskTitleAndDate;
	}

	public String formatTitleAndDate(String taskTitle, Calendar startDate, Calendar endDate) {
		String formattedTitleAndDate = taskTitle;

		// if not floating task, we add date and/or time 
		if (!isFloating(task)) {
			formattedTitleAndDate += "<font color = #00BA16> (" +
					formatDateAndTime(startDate, endDate) +		
					")</font>";
		}

		// at extra bottom margin to the end of the list
		if (index == size - 1) {
			formattedTitleAndDate += "<br/> &nbsp";
		}

		return formattedTitleAndDate;
	}

	public String formatDateAndTime(Calendar startDate, Calendar endDate) {			
		String formattedDateAndTime = "";
		if (hasSameDateTime(startDate, endDate)) {
			// have strictly the same date and time -- we only show start time
			formattedDateAndTime += String.format("%1$tl:%1$tM %Tp", startDate);
		} else if (hasSameDate(startDate, endDate)) {
			
			if (isAllDay(startDate, endDate)) {		
				formattedDateAndTime += "all day";
			} else {
				// have strictly the same date only -- we show start time and end time
				formattedDateAndTime += String.format("%1$tl:%1$tM %Tp - %2$tl:%2$tM %Tp", startDate, endDate);
			}
		} else {
			// have strictly different date and time -- we show start time and end date and end time
			formattedDateAndTime += String.format("%1$tl:%1$tM %Tp - %2$td/%2$tm/%2$tY %2$tl:%2$tM %Tp", 
					startDate, endDate);
		}
		return formattedDateAndTime;
	}

	public String formatLineNo() {
		String formattedLineNo = "";

		formattedLineNo += addSpace(4);		// left margin
		formattedLineNo += "<font color = #A6A6A6>";

		// determine the no of digits to use for line no based on total no of tasks
		// i.e. if the total no of tasks is NN   we use 2 digit
		//		                            NNN  we use 3 digit
		// this is to ensure that tasks in the list are properly left aligned
		int nDigit = countDigit(size);			
		// determine no of space to add to task with insufficient digit
		int nSpace = nDigit - countDigit(index + 1);		
		while(nSpace-- > 0) {
			formattedLineNo += "&nbsp ";
		}

		formattedLineNo += (index + 1) +  
				"</font>" + 
				"&nbsp &nbsp ";

		return formattedLineNo;
	}

	public boolean isAllDay(Calendar startDate, Calendar endDate) {
		if (hasSameDate(startDate, endDate) &&
				startDate.get(Calendar.HOUR_OF_DAY) == 0 &&
				startDate.get(Calendar.MINUTE) == 0 &&
				endDate.get(Calendar.HOUR_OF_DAY) == 23 &&
				endDate.get(Calendar.MINUTE) == 59) {
			return true;
		} else {
			return false;
		}
	}

	public String getHeading() {
		String heading;

		// task is the first element in the model
		if (index == 0) {
			if (isFloating(task)) {
				heading = "other";
			} else {
				heading = getDateHeading(startDate);
			}
		} else {
			EchoTask prevTask = model.getElementAt(index - 1);
			Calendar prevTaskStartDate = getCalendar(prevTask.getStartDate());

			if (isFloating(task)) {
				if (!isFloating(prevTask)) {
					// if prevTask is not a floating task
					// then task must be the first of floating tasks
					heading = "other";
				} else {
					heading = null;
				}
			} else {
				// not a floating task and not the first element
				// hence must have a previous task with date
				assert prevTaskStartDate != null;				

				if (!hasSameDate(startDate, prevTaskStartDate)) {
					// current and prev tasks have strictly different date (i.e. time may be different)
					heading = getDateHeading(startDate);
				} else {
					heading = null;
				}
			}
		}
		return heading;
	}

	public String getDateHeading(Calendar date) {
		String dateHeading;

		Calendar curDateTime = Calendar.getInstance();		
		int nWeekDiff = date.get(Calendar.WEEK_OF_YEAR) - 
				curDateTime.get(Calendar.WEEK_OF_YEAR);
		int nDayDiff = date.get(Calendar.DAY_OF_YEAR) - 
				curDateTime.get(Calendar.DAY_OF_YEAR);


		if (nDayDiff == 0) {
			dateHeading = "today";
		} else if (nDayDiff == 1) {
			dateHeading = "tomorrow";
		} else if (nDayDiff == -1) {
			dateHeading = "yesterday";
		} else if (nWeekDiff == 0) {	// same week
			
			if (nDayDiff < 1) {
				String day = String.format("%tA", date).toLowerCase();
				dateHeading = "last " + day;
			} else {	// nDayDiff > 1 (but still the same week)
				String day = String.format("%tA", date).toLowerCase();
				dateHeading = "this " + day;
			}			
		} else if (nWeekDiff == 1) {
			String day = String.format("%tA", date).toLowerCase();
			if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				// if the day is Sunday still use "this" (even though it is one week ahead)
				dateHeading = "this " + day;
			} else {
				dateHeading = "next " + day;
			}		
		} else {
			// if the difference between current time and time of task is 2 weeks or more
			// we show the date (dd/mm/yyyy) as the heading
			dateHeading = date.get(Calendar.DATE) + "/" +
					(date.get(Calendar.MONTH) + 1) + "/" +	// Calendar use 0-based index for month
					date.get(Calendar.YEAR);
		}
		return dateHeading;
	}

	// return a Calendar object from a Date object
	public Calendar getCalendar(Date date) {
		if (date == null) {
			return null;
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		}
	}

	public boolean isFloating(EchoTask task) {
		if (task.getStartDate() == null && task.getEndDate() == null) {		
			return true;
		} else {
			return false;
		}
	}

	public String addSpace(int n) {
		String space = "";
		while (n-- > 0) {
			space += "&nbsp ";
		}
		return space;
	}


	public int countDigit(int n) {
		int nDigit = 1;
		while(n / 10 > 0) {
			nDigit++;
			n = n / 10;
		}

		return nDigit;
	}

	/**
	 * this method compare start and end date strictly based on the date (the time is ignored)
	 * @param startDate
	 * @param endDate
	 * @return true if startDate and endDate have strictly same date, false otherwise
	 */
	public boolean hasSameDate(Calendar startDate, Calendar endDate) {
		if (startDate.get(Calendar.YEAR) != endDate.get(Calendar.YEAR) ||
				startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH) ||
				startDate.get(Calendar.DATE) != endDate.get(Calendar.DATE)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * this method compare start date and time  with
	 * 					   end   date and time
	 * @param startDate
	 * @param endDate
	 * @return true if startDate and endDate have both same date and time, false otherwise
	 */
	public boolean hasSameDateTime(Calendar startDate, Calendar endDate) {
		if (!hasSameDate(startDate, endDate) ||
				startDate.get(Calendar.HOUR_OF_DAY) != endDate.get(Calendar.HOUR_OF_DAY) ||
				startDate.get(Calendar.MINUTE) != endDate.get(Calendar.MINUTE)) {
			return false;			
		} else {	
			return true;
		}
	}

}