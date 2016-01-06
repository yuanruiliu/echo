/**
 * 
 * @author Lokman Hakim A0087978Y, Liu Yuanrui A0091752A
 * @description This class handles all the information present for each individual task.
 * 
 */
package echo.logicHandler;

import java.text.*;
import java.util.*;

public class EchoTask {
	private String title;
	private Date startDate;
	private Date endDate;

	// empty default constructor
	public EchoTask() {
		startDate = new Date();
		endDate = new Date();
	}

	public EchoTask(String title) {
		this.setTitle(title);
		this.setEndTimeInString(null);
		this.setEndDate(null);
	}

	public EchoTask(String title, Date startDate, Date endDate) {
		this.setTitle(title);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	// getStartDateInString
	public String getStartDateInString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder startDateBuilder = null;
		String startDate = "";

		try {
			if(getStartDate()==null){
				return null;
			}
			startDateBuilder = new StringBuilder(
					sdf.format(this.getStartDate()));
			startDate = startDateBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return startDate;
	}

	// setStartDateInString
	@SuppressWarnings("deprecation")
	public void setStartDateInString(String startDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date newStartDate = sdf.parse(startDate);
			this.getStartDate().setDate(newStartDate.getDate());
			this.getStartDate().setMonth(newStartDate.getMonth());
			this.getStartDate().setYear(newStartDate.getYear());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// getStartTimeInString
	public String getStartTimeInString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		StringBuilder startTimeBuilder = null;
		String startTime = "";
		
		if(getStartDate()==null){
			return null;
		}
		startTimeBuilder = new StringBuilder(sdf.format(this.getStartDate()));
		startTime = startTimeBuilder.toString();

		return startTime;
	}

	// setStartTimeInString
	public void setStartTimeInString(String startTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

		try {
			Date newStartTime = sdf.parse(startTime);
			this.getStartDate().setTime(newStartTime.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// getEndDateInString
	public String getEndDateInString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder endDateBuilder = null;
		String endDate = "";
		if(getEndDate()==null){
			return null;
		}
		endDateBuilder = new StringBuilder(sdf.format(this.getEndDate()));
		endDate = endDateBuilder.toString();

		return endDate;
	}

	// setEndDateInString
	@SuppressWarnings("deprecation")
	public void setEndDateInString(String endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		try {
			Date newEndDate = sdf.parse(endDate);
			this.getEndDate().setDate(newEndDate.getDate());
			this.getEndDate().setMonth(newEndDate.getMonth());
			this.getEndDate().setYear(newEndDate.getYear());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// getEndTimeInString
	public String getEndTimeInString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		StringBuilder endTimeBuilder = null;
		String endTime = "";
		
		if(getEndDate()==null){
			return null;
		}
		endTimeBuilder = new StringBuilder(sdf.format(this.getEndDate()));
		endTime = endTimeBuilder.toString();

		return endTime;
	}

	// setEndTimeInString
	public void setEndTimeInString(String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

		try {
			Date newEndTime = sdf.parse(endTime);
			this.getEndDate().setTime(newEndTime.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	// toString
	public String toString() {
		String display = "";

		if (this.getStartDate() != null && this.getEndDate() != null) {
			display = this.getTitle() + " " + this.getStartDateInString() + " "
					+ this.getStartTimeInString() + " to" + " "
					+ this.getEndDateInString() + " "
					+ this.getEndTimeInString();
		} else {
			display = this.getTitle();
		}

		return display;
	}

}
