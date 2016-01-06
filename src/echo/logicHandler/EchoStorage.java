package echo.logicHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 * @author Liu Yuanrui A0091752A
 * @description to connect EchoTaskHandler and the storage. Echo could read from
 *              the storage when launched and write to storage every time when
 *              change is made. The storage is recorded as .csv files.
 */
public class EchoStorage {

	private ArrayList<String> tempList = new ArrayList<String>();

	private static String Directory = getDirectory();

	private static File ActiveTasks = new File(Directory + "\\EchoDB_A.csv");

	private static File DueTasks = new File(Directory + "\\EchoDB_D.csv");

	private static File CompletedTasks = new File(Directory + "\\EchoDB_C.csv");

	private File file;
	
	private Logger logger=Logger.getLogger(EchoStorage.class.getName());
	
	private FileHandler handler = new FileHandler(Directory + "\\Echo.log");
	
	private static String READING_MESSAGE="Reading From File";
	
	private static String WRITING_MESSAGE="Writing To File";

	// get the directory under "Documents"
	private static String getDirectory() {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		File directory=new File(fw.getDefaultDirectory().toString()+"\\Echo");
		if(!directory.exists()){
			directory.mkdir();
		}
		return directory.toString();
	}

	public static void main(String args[]) {
		File directory = new File(".");
		try {
			System.out.println("Current directory's canonical path: "
					+ directory.getCanonicalPath());
			System.out.println("Current directory's absolute  path: "
					+ directory.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Exceptione is =" + e.getMessage());
		}
	}

	public EchoStorage(String type) throws IOException {
		assert type != null;

		if (!ActiveTasks.exists()) {
			ActiveTasks.createNewFile();
		}
		if (!DueTasks.exists()) {
			DueTasks.createNewFile();
		}
		if (!CompletedTasks.exists()) {
			CompletedTasks.createNewFile();
		}

		if (type.equalsIgnoreCase("Active")) {

			file = ActiveTasks;

		} else if (type.equalsIgnoreCase("Due")) {

			file = DueTasks;

		} else if (type.equalsIgnoreCase("Completed")) {

			file = CompletedTasks;
		}
	}

	/*
	 * Data in csv file is recorded as format: 
	 * title1/,start_date1/,end_date1
	 * title2/,start_date2/,end_date2 
	 * title3/,start_date3/,end_date3 
	 * ... 
	 * (for floating task, start_date and end_date
	 *  are both recorded as "null")
	 */
	public DefaultListModel<EchoTask> readFromFile() throws Exception {
		try {
			
			logger.addHandler(handler);		
			
			logger.finest(READING_MESSAGE);
			
			BufferedReader bufferedreader = new BufferedReader(new FileReader(
					file));
			String stemp = bufferedreader.readLine(); // read the empty line in
														// the beginning of csv
														// file
			while ((stemp = bufferedreader.readLine()) != null) {
				tempList.add(stemp);
			}
			bufferedreader.close();
			return convert(tempList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DefaultListModel<EchoTask> convert(ArrayList<String> list)
			throws Exception {
		DefaultListModel<EchoTask> taskList = new DefaultListModel<EchoTask>();
		for (int i = 0; i < list.size(); i++) {
			String temps = list.get(i);
			String title = null;
			String start_date = null;
			String end_date = null;
			Date startDate = null;
			Date endDate = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			StringTokenizer st = new StringTokenizer(temps, "\\,");
			if (st.hasMoreTokens()) {
				title = st.nextToken();
			}
			start_date = st.nextToken();
			if (st.hasMoreTokens() && (!start_date.equals("null null"))
					&& st.hasMoreTokens()
					&& (!(end_date = st.nextToken()).equals("null null"))) {
				startDate = sdf.parse(start_date);
				endDate = sdf.parse(end_date);
				taskList.addElement(new EchoTask(title, startDate, endDate));
			} else {
				taskList.addElement(new EchoTask(title, null, null)); // floating
																		// task
			}
		}
		return taskList;
	}

	public void writeToFile(DefaultListModel<EchoTask> taskList) {
		try {
			
			logger.finest(WRITING_MESSAGE);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
			
			for (int j = 0; j < taskList.size(); j++) {
				bw.newLine();
				String temp = convertToString(taskList.get(j));
				bw.write(temp);
			}
			
			bw.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String convertToString(EchoTask et) {
		String line = "%1$s\\,%2$s\\,%3$s";
		String title = et.getTitle();
		String start_date = et.getStartDateInString() + " "
				+ et.getStartTimeInString();
		String end_date = et.getEndDateInString() + " "
				+ et.getEndTimeInString();
		return String.format(line, title, start_date, end_date);
	}

	public ArrayList<String> getList() throws IOException {
		return tempList;
	}

	public int getRowNum() {
		return tempList.size();
	}

	public int getColNum() {
		if (!tempList.toString().equals(" ")) {
			if (tempList.get(0).toString().contains("\\,")) {
				return tempList.get(0).toString().split("\\,").length;
			} else if (tempList.get(0).toString().trim().length() != 0) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public String getRow(int index) {
		if (this.tempList.size() != 0)
			return (String) tempList.get(index);
		else
			return null;
	}

	public String getCol(int index) {
		if (this.getColNum() == 0) {
			return null;
		}
		StringBuffer scol = new StringBuffer();
		String temp = null;
		int colnum = this.getColNum();
		if (colnum > 1) {
			for (java.util.Iterator<String> it = tempList.iterator(); it
					.hasNext();) {
				temp = it.next().toString();

				scol = scol.append(temp.split("\\,")[index] + ",");
			}
		} else {
			for (java.util.Iterator<String> it = tempList.iterator(); it
					.hasNext();) {
				temp = it.next().toString();
				scol = scol.append(temp + ",");
			}
		}
		String str = new String(scol.toString());
		str = str.substring(0, str.length() - 1);
		return str;
	}

	public String getString(int row, int col) {
		String temp = null;
		int colnum = this.getColNum();
		if (colnum > 1) {
			temp = tempList.get(row).toString().split("\\,")[col];
		} else if (colnum == 1) {
			temp = tempList.get(row).toString();
		} else {
			temp = null;
		}
		return temp;
	}

}
