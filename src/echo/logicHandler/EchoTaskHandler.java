/**
 * 
 * @author Lokman Hakim A0087978Y, Yuanrui A0091752A
 * @description Back end task Handler
 * 
 */
package echo.logicHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.Timer;

public class EchoTaskHandler {
	/* Tab constants */
	private static final int TAB_ACTIVE = 0;
	private static final int TAB_DUE = 1;
	private static final int TAB_COMPLETED = 2;
	private static final int TAB_SEARCH = 3;
	private static final String ACTIVE = "Active";
	private static final String COMPLETED = "Completed";
	private static final String DUE = "Due";

	private DefaultListModel<EchoTask> listModelForActiveTasks = null;
	private DefaultListModel<EchoTask> listModelForDueTasks = null;
	private DefaultListModel<EchoTask> listModelForCompletedTasks = null;
	private DefaultListModel<EchoTask> listModelForSearchedTasks = null;

	private ArrayList<String> commandList = new ArrayList<String>();
	private DefaultListModel<EchoTask> temp_A = new DefaultListModel<EchoTask>();
	private DefaultListModel<EchoTask> temp_C = new DefaultListModel<EchoTask>();
	private Timer dueTaskTimer;
	Vector<DefaultListModel<EchoTask>> list_A = new Vector<DefaultListModel<EchoTask>>();
	Vector<DefaultListModel<EchoTask>> list_C = new Vector<DefaultListModel<EchoTask>>();

	private Vector<Integer> indexInActive;

	private EchoStorage echoDB_a;
	private EchoStorage echoDB_d;
	private EchoStorage echoDB_c;

	public EchoTaskHandler() {
		initListModels();
		initEchoStorage();

		// find the optimal updating frequency
		dueTaskTimer = new Timer(500, this.getActionListener());
		dueTaskTimer.start();
	}

	public void initEchoStorage() {
		try {
			echoDB_a = new EchoStorage(ACTIVE);
			echoDB_d = new EchoStorage(DUE);
			echoDB_c = new EchoStorage(COMPLETED);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void initListModels() {
		listModelForActiveTasks = new DefaultListModel<EchoTask>();
		listModelForDueTasks = new DefaultListModel<EchoTask>();
		listModelForCompletedTasks = new DefaultListModel<EchoTask>();
		listModelForSearchedTasks = new DefaultListModel<EchoTask>();

		try {
			System.out.println(echoDB_a != null);
			listModelForActiveTasks = echoDB_a.readFromFile();
			listModelForDueTasks = echoDB_d.readFromFile();
			listModelForCompletedTasks = echoDB_c.readFromFile();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * This method is used only for testing.
	 * 
	 */
	public void clear() {
		listModelForActiveTasks.clear();
		listModelForDueTasks.clear();
		listModelForCompletedTasks.clear();
		listModelForSearchedTasks.clear();
	}

	public ActionListener getActionListener() {
		ActionListener dueTaskListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!listModelForActiveTasks.isEmpty()) {
					Date currentDate = new Date();

					// TODO dun check for every task, only check the current one
					// past
					for (int i = 0; i < listModelForActiveTasks.size(); i++) {
						if (listModelForActiveTasks.get(i).getStartDate() != null) {
							if (currentDate.compareTo(listModelForActiveTasks
									.get(i).getEndDate()) > 0) {
								EchoTask dueTask = listModelForActiveTasks
										.get(i);
								deleteActiveTask(i);
								listModelForDueTasks.addElement(dueTask);
							}
						}
					}
				}
			}
		};
		echoDB_d.writeToFile(listModelForDueTasks);
		return dueTaskListener;
	}

	public int addActiveTask(EchoTask newTask) {
		int index = -1;

		// when there is still nothing inside listModelForActiveTasks
		if (listModelForActiveTasks.size() == 0) {
			listModelForActiveTasks.addElement(newTask);
			index = 0;
			echoDB_a.writeToFile(listModelForActiveTasks);
			return index;
		}

		// FROM HERE ONWARDS, size > 0
		// newTask is a floating task
		if (newTask.getStartDate() == null && newTask.getEndDate() == null) {
			int lastIndex = listModelForActiveTasks.size();
			listModelForActiveTasks.add(lastIndex, newTask);
			echoDB_a.writeToFile(listModelForActiveTasks);

			return lastIndex;
		}

		// FROM HERE ONWARDS, newTask is not a floating task and size > 0

		// There is still no timed task in the list
		if (listModelForActiveTasks.get(0).getStartDate() == null
				&& listModelForActiveTasks.get(0).getEndDate() == null) {
			listModelForActiveTasks.add(0, newTask);
			index = 0;
			echoDB_a.writeToFile(listModelForActiveTasks);
			return index;
		}

		// FROM HERE ONWARDS, newTask is not a floating task and size > 0
		// first task int the list is not a floating task

		// pointer to check the dates
		Date curDayTaskDate;
		Date newDayTaskDate;
		int currentIndex;

		for (currentIndex = 0; currentIndex < listModelForActiveTasks.size(); currentIndex++) {
			EchoTask curDayTask = listModelForActiveTasks.get(currentIndex);

			curDayTaskDate = curDayTask.getStartDate();
			newDayTaskDate = newTask.getStartDate();

			if (newDayTaskDate.compareTo(curDayTaskDate) == 0) {

				if (newDayTaskDate.getTime() < curDayTaskDate.getTime()) {
					listModelForActiveTasks.add(currentIndex, newTask);
					index = currentIndex;
				} else {
					// curTask is the end of the list i.e. no floating task
					// afterwards
					if (currentIndex == listModelForActiveTasks.size() - 1) {
						listModelForActiveTasks.addElement(newTask);
						echoDB_a.writeToFile(listModelForActiveTasks);
						return currentIndex + 1;
					} else {
						// there is a floating task after the curTask
						currentIndex = currentIndex + 1;
						listModelForActiveTasks.add(currentIndex, newTask);
						index = currentIndex;
					}
				}
				echoDB_a.writeToFile(listModelForActiveTasks);
				return index;
			}

			if (newDayTaskDate.compareTo(curDayTaskDate) < 0) {
				listModelForActiveTasks.add(currentIndex, newTask);
				index = currentIndex;
				echoDB_a.writeToFile(listModelForActiveTasks);
				return index;
			}

			if (newDayTaskDate.compareTo(curDayTaskDate) > 0) {

				int nextIndex = currentIndex + 1;

				if (currentIndex < listModelForActiveTasks.size() - 1) {
					Date nextDayTaskDate = listModelForActiveTasks.get(
							nextIndex).getStartDate();

					if (nextDayTaskDate != null) {
						if (newDayTaskDate.compareTo(nextDayTaskDate) > 0) {
						} else {
							listModelForActiveTasks.add(nextIndex, newTask);
							index = nextIndex;
							echoDB_a.writeToFile(listModelForActiveTasks);
							return index;
						}
					} else {
						listModelForActiveTasks.add(nextIndex, newTask);
						index = nextIndex;
						echoDB_a.writeToFile(listModelForActiveTasks);
						return index;
					}
				} else {
					listModelForActiveTasks.addElement(newTask);
					echoDB_a.writeToFile(listModelForActiveTasks);
					return index + 1;
				}
			}
		}

		listModelForActiveTasks.add(currentIndex, newTask);
		index = currentIndex;
		echoDB_a.writeToFile(listModelForActiveTasks);
		return index;
	}

	public int addCompletedTask(EchoTask newTask) {
		int index = -1;

		// when there is still nothing inside listModelForCompletedTasks
		if (listModelForCompletedTasks.size() == 0) {
			listModelForCompletedTasks.addElement(newTask);
			index = 0;
			echoDB_a.writeToFile(listModelForCompletedTasks);
			return index;
		}

		// FROM HERE ONWARDS, size > 0
		// newTask is a floating task
		if (newTask.getStartDate() == null && newTask.getEndDate() == null) {
			int lastIndex = listModelForCompletedTasks.size();
			listModelForCompletedTasks.add(lastIndex, newTask);
			echoDB_a.writeToFile(listModelForCompletedTasks);
			return lastIndex;
		}

		// FROM HERE ONWARDS, newTask is not a floating task and size > 0

		// There is still no timed task in the list
		if (listModelForCompletedTasks.get(0).getStartDate() == null
				&& listModelForCompletedTasks.get(0).getEndDate() == null) {
			listModelForCompletedTasks.add(0, newTask);
			index = 0;
			echoDB_a.writeToFile(listModelForCompletedTasks);
			return index;
		}

		// FROM HERE ONWARDS, newTask is not a floating task and size > 0
		// first task int the list is not a floating task

		// pointer to check the dates
		Date curDayTaskDate;
		Date newDayTaskDate;
		int currentIndex;

		for (currentIndex = 0; currentIndex < listModelForCompletedTasks.size(); currentIndex++) {
			EchoTask curDayTask = listModelForCompletedTasks.get(currentIndex);

			curDayTaskDate = curDayTask.getStartDate();
			newDayTaskDate = newTask.getStartDate();

			// System.out.println(newDayTaskDate);
			// System.out.println(curDayTaskDate);

			if (newDayTaskDate.compareTo(curDayTaskDate) == 0) {

				if (newDayTaskDate.getTime() < curDayTaskDate.getTime()) {
					listModelForCompletedTasks.add(currentIndex, newTask);
					index = currentIndex;
				} else {
					// curTask is the end of the list i.e. no floating task
					// afterwards
					if (currentIndex == listModelForCompletedTasks.size() - 1) {
						listModelForCompletedTasks.addElement(newTask);
						echoDB_a.writeToFile(listModelForCompletedTasks);
						return currentIndex + 1;
					} else {
						// there is a floating task after the curTask
						currentIndex = currentIndex + 1;
						listModelForCompletedTasks.add(currentIndex, newTask);
						index = currentIndex;
					}
				}
				echoDB_a.writeToFile(listModelForCompletedTasks);
				return index;
			}

			if (newDayTaskDate.compareTo(curDayTaskDate) < 0) {
				listModelForCompletedTasks.add(currentIndex, newTask);
				index = currentIndex;
				echoDB_a.writeToFile(listModelForCompletedTasks);
				return index;
			}

			if (newDayTaskDate.compareTo(curDayTaskDate) > 0) {

				int nextIndex = currentIndex + 1;

				if (currentIndex < listModelForCompletedTasks.size() - 1) {
					Date nextDayTaskDate = listModelForCompletedTasks.get(
							nextIndex).getStartDate();

					if (nextDayTaskDate != null) {
						if (newDayTaskDate.compareTo(nextDayTaskDate) > 0) {
						} else {
							listModelForCompletedTasks.add(nextIndex, newTask);
							index = nextIndex;
							echoDB_a.writeToFile(listModelForCompletedTasks);
							return index;
						}
					} else {
						listModelForCompletedTasks.add(nextIndex, newTask);
						index = nextIndex;
						echoDB_a.writeToFile(listModelForCompletedTasks);
						return index;
					}
				} else {
					listModelForCompletedTasks.addElement(newTask);
					echoDB_a.writeToFile(listModelForCompletedTasks);
					return index + 1;
				}
			}
		}

		listModelForCompletedTasks.add(currentIndex, newTask);
		index = currentIndex;
		echoDB_a.writeToFile(listModelForCompletedTasks);
		return index;
	}

	/**
	 * The following five methods are used to delete tasks in different tabs
	 * 
	 * @param index
	 * @param tabIndex
	 */
	public void deleteTask(int index, int tabIndex) {
		if (tabIndex == TAB_ACTIVE) {
			deleteActiveTask(index);
		} else if (tabIndex == TAB_COMPLETED) {
			deleteCompletedTask(index);
		} else if (tabIndex == TAB_SEARCH) {
			deleteSearchTask(index);
		} else if (tabIndex == TAB_DUE) {
			deleteDueTask(index);
		}
	}

	public void deleteActiveTask(int index) {
		// System.out.println("index : "+ index);
		listModelForActiveTasks.remove(index);
		echoDB_a.writeToFile(listModelForActiveTasks);
	}

	public void deleteCompletedTask(int index) {
		listModelForCompletedTasks.remove(index);
		echoDB_c.writeToFile(listModelForCompletedTasks);
	}

	public void deleteDueTask(int index) {
		listModelForDueTasks.remove(index);
		echoDB_c.writeToFile(listModelForDueTasks);
	}

	public void deleteSearchTask(int index) {
		assert indexInActive != null;

		// remove both tasks in search and active list
		listModelForSearchedTasks.remove(index);
		listModelForActiveTasks.remove(indexInActive.get(index));

		updateIndexInActive(index);
		// remove the task in the table of mapping
		indexInActive.remove(indexInActive.lastElement());
		echoDB_a.writeToFile(listModelForActiveTasks);
	}

	// update the mapping of tasks index in search list to task index in active
	// list
	public void updateIndexInActive(int removedIndex) {
		if (removedIndex == indexInActive.size() - 1) {
			// if removedIndex is the last, the do nothing
			return;
		} else {
			// we need to update the mapping of tasks with index larger than
			// removedIndex
			for (int i = removedIndex; i < indexInActive.size() - 1; i++) {
				indexInActive.set(i, indexInActive.get(i + 1) - 1);
			}
		}
	}

	public String getActiveTaskAt(int index) {
		EchoTask taskAtIndex = listModelForActiveTasks.getElementAt(index);
		return taskAtIndex.toString();
	}

	public void doneTask(int index, int tabIndex) {
		if (tabIndex == TAB_ACTIVE) {
			doneActiveTask(index);
		} else if (tabIndex == TAB_DUE) {
			doneDueTask(index);
		} else {
			// code should not reach here;
			assert false;
			return;
		}
	}

	public void doneDueTask(int index) {
		EchoTask echoTask = listModelForDueTasks.get(index);
		deleteDueTask(index);
		addCompletedTask(echoTask);
		echoDB_a.writeToFile(listModelForActiveTasks);
		echoDB_c.writeToFile(listModelForCompletedTasks);
	}

	public void doneActiveTask(int index) {
		EchoTask echoTask = listModelForActiveTasks.get(index);
		deleteActiveTask(index);
		addCompletedTask(echoTask);
		echoDB_a.writeToFile(listModelForActiveTasks);
		echoDB_c.writeToFile(listModelForCompletedTasks);
	}

	public void undoneTask(int index) {
		int newIndex = -1;
		EchoTask taskToBeUndone = new EchoTask();

		if (!listModelForCompletedTasks.isEmpty()) {
			taskToBeUndone = listModelForCompletedTasks.get(index);
			// remove the task from completeted list
			listModelForCompletedTasks.remove(index);
		} else {
			return;
		}

		// when there is still nothing inside listModelForActiveTasks
		if (listModelForActiveTasks.size() == 0) {
			listModelForActiveTasks.addElement(taskToBeUndone);
			newIndex = 0;
			echoDB_a.writeToFile(listModelForActiveTasks);
			echoDB_c.writeToFile(listModelForCompletedTasks);
			return;
		}

		// floating tasks are always added at the back
		if (taskToBeUndone.getStartDate() == null
				&& taskToBeUndone.getEndDate() == null) {
			newIndex = listModelForActiveTasks.size();
			listModelForActiveTasks.add(newIndex, taskToBeUndone);
			echoDB_a.writeToFile(listModelForActiveTasks);
			echoDB_c.writeToFile(listModelForCompletedTasks);
			return;
		}

		if (listModelForActiveTasks.get(0).getStartDate() == null
				&& listModelForActiveTasks.get(0).getEndDate() == null) {
			listModelForActiveTasks.add(0, taskToBeUndone);
			newIndex = 0;
			echoDB_a.writeToFile(listModelForActiveTasks);
			echoDB_c.writeToFile(listModelForCompletedTasks);
			return;
		}

		// cursors to check the dates
		Date curDayTaskDate;
		Date newDayTaskDate;
		int currentIndex;

		for (currentIndex = 0; currentIndex < listModelForActiveTasks.size(); currentIndex++) {
			EchoTask curDayTask = listModelForActiveTasks.get(currentIndex);

			try {
				curDayTaskDate = curDayTask.getStartDate();
				newDayTaskDate = taskToBeUndone.getStartDate();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				return;
			}

			if (newDayTaskDate.compareTo(curDayTaskDate) == 0) {
				if (newDayTaskDate.getTime() < curDayTaskDate.getTime()) {
					listModelForActiveTasks.add(currentIndex, taskToBeUndone);
					newIndex = currentIndex;
				} else {
					currentIndex = currentIndex + 1;
					listModelForActiveTasks.add(currentIndex, taskToBeUndone);
					newIndex = currentIndex;
				}
				echoDB_a.writeToFile(listModelForActiveTasks);
				echoDB_c.writeToFile(listModelForCompletedTasks);
				return;
			}

			if (newDayTaskDate.compareTo(curDayTaskDate) < 0) {
				listModelForActiveTasks.add(currentIndex, taskToBeUndone);
				newIndex = currentIndex;
				echoDB_a.writeToFile(listModelForActiveTasks);
				echoDB_c.writeToFile(listModelForCompletedTasks);
				return;
			}
		}

		listModelForActiveTasks.add(currentIndex, taskToBeUndone);
		newIndex = currentIndex;
		echoDB_a.writeToFile(listModelForActiveTasks);
		echoDB_c.writeToFile(listModelForCompletedTasks);
		return;
	}

	public String editActiveTask(int index) {
		EchoTask taskToEdit = listModelForActiveTasks.remove(index);
		echoDB_a.writeToFile(listModelForActiveTasks);
		return taskToEdit.toString();
	}

	public int searchTask(int tabIndex, String str) {
		if (tabIndex == TAB_ACTIVE) {
			return searchActiveTask(str);
		} else if (tabIndex == TAB_COMPLETED) {
			return searchCompletedTask(str);
		} else if (tabIndex == TAB_DUE) {
			return searchDueTask(str);
		} else {
			assert false;
			return -1;
		}
	}

	public int searchActiveTask(String str) {
		if (!listModelForSearchedTasks.isEmpty()) {
			listModelForSearchedTasks.clear();
		}

		if (!listModelForActiveTasks.isEmpty()) {

			// mapping of task no in active list to task no in search list
			indexInActive = new Vector<Integer>();

			for (int i = 0; i < listModelForActiveTasks.size(); i++) {
				String title = listModelForActiveTasks.get(i).getTitle();

				if (title.contains(str)) {
					listModelForSearchedTasks
							.addElement(listModelForActiveTasks.get(i));
					indexInActive.addElement(i);
				}

			}
		}

		if (listModelForSearchedTasks.size() > 0) {
			return listModelForSearchedTasks.size();
		} else {
			return 0;
		}
	}

	public int searchDueTask(String str) {
		if (!listModelForSearchedTasks.isEmpty()) {
			listModelForSearchedTasks.clear();
		}

		if (!listModelForDueTasks.isEmpty()) {

			// mapping of task no in active list to task no in search list
			indexInActive = new Vector<Integer>();

			for (int i = 0; i < listModelForDueTasks.size(); i++) {
				String title = listModelForDueTasks.get(i).getTitle();

				if (title.contains(str)) {
					listModelForSearchedTasks.addElement(listModelForDueTasks
							.get(i));
					indexInActive.addElement(i);
				}

			}
		}

		if (listModelForSearchedTasks.size() > 0) {
			return listModelForSearchedTasks.size();
		} else {
			return 0;
		}
	}

	public int searchCompletedTask(String str) {
		if (!listModelForSearchedTasks.isEmpty()) {
			listModelForSearchedTasks.clear();
		}

		if (!listModelForCompletedTasks.isEmpty()) {

			// mapping of task no in active list to task no in search list
			indexInActive = new Vector<Integer>();

			for (int i = 0; i < listModelForCompletedTasks.size(); i++) {
				String title = listModelForCompletedTasks.get(i).getTitle();

				if (title.contains(str)) {
					listModelForSearchedTasks
							.addElement(listModelForCompletedTasks.get(i));
					indexInActive.addElement(i);
				}

			}
		}

		if (listModelForSearchedTasks.size() > 0) {
			return listModelForSearchedTasks.size();
		} else {
			return 0;
		}
	}

	public DefaultListModel<EchoTask> getListModelForActiveTasks() {
		return listModelForActiveTasks;
	}

	public DefaultListModel<EchoTask> getListModelForCompletedTasks() {
		return listModelForCompletedTasks;
	}

	public DefaultListModel<EchoTask> getListModelForDueTasks() {
		return listModelForDueTasks;
	}

	public DefaultListModel<EchoTask> getListModelForSearchedTasks() {
		return listModelForSearchedTasks;
	}

	public void setCommandList(String str) {
		commandList.add(str);
	}

	/**
	 * mainly for undo() and redo() record the status of listModelForAvtiveTasks
	 * or listModelForCompletedTasks before every execution made.
	 */
	public void updateCopy(char type) {
		DefaultListModel<EchoTask> temp = new DefaultListModel<EchoTask>();

		switch (type) {
		case 'a': // stands for "active tasks"
			for (int i = 0; i < listModelForActiveTasks.getSize(); i++) {
				temp.add(i, listModelForActiveTasks.elementAt(i));
			}
			if (!temp.isEmpty() || list_A.isEmpty()) {
				list_A.add(temp);
			}
			break;
		case 'c': // stands for "completed tasks"
			for (int i = 0; i < listModelForCompletedTasks.getSize(); i++) {
				temp.add(i, listModelForCompletedTasks.elementAt(i));

			}
			if (!temp.isEmpty() || list_C.isEmpty()) {
				list_C.add(temp);
			}
			break;
		default:
			break;
		}
	}

	public void getPreVersionForActiveTasks() {
		if (list_A.size() >= 1) {
			temp_A = list_A.get(list_A.size() - 1);

			if (!listModelForActiveTasks.equals(temp_A)) {
				updateCopy('a');
				listModelForActiveTasks.clear();
				for (int i = 0; i < temp_A.size(); i++) {
					listModelForActiveTasks.add(i, temp_A.elementAt(i));
				}
				echoDB_a.writeToFile(listModelForActiveTasks);
			}
		}
	}

	public void getPreVersionForCompletedTasks() {
		if (list_C.size() - 1 >= 0) {
			temp_C = list_C.get(list_C.size() - 1);

			if (!listModelForCompletedTasks.equals(temp_C)) {
				updateCopy('c');
				listModelForCompletedTasks.clear();
				for (int i = 0; i < temp_C.size(); i++) {
					listModelForCompletedTasks.add(i, temp_C.elementAt(i));
				}
				echoDB_c.writeToFile(listModelForCompletedTasks);
			}
		}
	}

	/**
	 * undo for add, delete, done, undone edit or redo cannot undo for undo or
	 * search
	 */
	public void undo() {

		commandList.remove(commandList.size() - 1);
		String lastCommand = commandList.get(commandList.size() - 1);

		if (!lastCommand.equalsIgnoreCase("undo")
				|| !lastCommand.equalsIgnoreCase("search")) {
			switch (lastCommand) {
			case "add":
				// if the last command is "add", the real last operation may be
				// either "addActiveTask" or "editActiveTask"
				// check and do different actions
				if (commandList.size() >= 2
						&& commandList.get(commandList.size() - 2)
								.equalsIgnoreCase("edit")) {
					// remove "add" in commandList and undo "edit"
					this.undo();

				} else {
					// undo "add"
					// only need to replace listModelForActiveTasks by its
					// previous version
					getPreVersionForActiveTasks();
				}
				break;

			// check both active tasks and completed tasks
			// if changed, get the previous version back
			case "undone":
			case "done":
			case "delete":
			case "redo":
				getPreVersionForActiveTasks();
				getPreVersionForCompletedTasks();
				break;
			case "edit":
				assert list_A.size() >= 2;
				temp_A = list_A.get(list_A.size() - 2);
				updateCopy('a');
				listModelForActiveTasks.clear();
				for (int i = 0; i < temp_A.size(); i++) {
					listModelForActiveTasks.add(i, temp_A.elementAt(i));
				}
				echoDB_a.writeToFile(listModelForActiveTasks);
				break;
			default:
				break;

			}
		}

	}

	public void redo() {
		String lastCommand = commandList.get(commandList.size() - 1);
		try {
			if (lastCommand.equalsIgnoreCase("undo")) {
				getPreVersionForActiveTasks();
				getPreVersionForCompletedTasks();
				commandList.add("redo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
