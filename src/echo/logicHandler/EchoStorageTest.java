package echo.logicHandler;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import org.junit.Test;

/**
 * @author Liu Yuanrui A0091752A
 *
 */
public class EchoStorageTest {
	
	@Test
	
	public void test() throws Exception {
		EchoStorage es = new EchoStorage("Active");
		es.readFromFile();
		ArrayList<String> tt = es.getList();
		for (java.util.Iterator<String> itt = tt.iterator(); itt.hasNext();) {
			System.out.println(itt.next().toString() + "||");			
		}
		System.out.println(es.getRowNum());
		System.out.println(es.getColNum());
		System.out.println(es.getRow(0));
		System.out.println(es.getCol(0));
		System.out.println(es.getString(0, 0));
		
		assertEquals("Test Case 1 - Num of Rows",tt.size(),es.getRowNum());
		assertEquals("Test Case 2 - Num of Cols",tt.get(0).split("\\,").length,es.getColNum());
		assertEquals("Test Case 3 - First Row",tt.get(0),es.getRow(0));
		assertEquals("Test Case 4 - First Cell",tt.get(0).split("\\,")[0],es.getString(0, 0));
	}
}
