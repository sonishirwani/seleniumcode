package com.eqtechnologic.results;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.eqtechnologic.util.PropertiesToSystem;

public class DatabaseUtility {

	private static final String DATA_EXTRACTION_QUERY = "select * from EXCEL_RESULT_TABLE where EXCEL_RUN_ID =(SELECT MAX(RUN_ID) FROM TESTCASE_TABLE)";

	public static List<RowContents> getAllRowData() {
		List<RowContents> rowContents = new ArrayList<RowContents>();
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			PropertiesToSystem systemProperties = new PropertiesToSystem("Configuration/config.properties");
			systemProperties.load();

			String connectionIP = "jdbc:oracle:thin:@" + System.getProperty("db.oracle.ip") + ":" + String.valueOf(1521)
					+ ":" + System.getProperty("db.oracle.sid");
			String userName = System.getProperty("db.oracle.user");
			String password = System.getProperty("db.oracle.password");
			con = DriverManager.getConnection(connectionIP, userName, password);

			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery(DATA_EXTRACTION_QUERY);
			while (rs.next()) {
				String runId = rs.getString(2);
				String suitName = rs.getString(3);
				String tcName = rs.getString(4);
				String result = rs.getString(5);
				RowContents rowContent = new RowContents(runId, suitName, tcName, result);
				rowContents.add(rowContent);
			}
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return rowContents;
	}
	/*
	 * public static void main(String[] args) { DatabaseUtility dbobject=new
	 * DatabaseUtility(); dbobject.getAllRowData(); }
	 */
}
