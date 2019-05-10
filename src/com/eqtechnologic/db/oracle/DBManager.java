package com.eqtechnologic.db.oracle;

/* This class is used to create a connection on Oracle DB and then run the query 
 * given as parameter on the opened connection.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;



import org.apache.log4j.Logger;
import org.testng.Assert;

public class DBManager {
	Properties dbProperties;

	/*
	 * Constructor taking parameters from the system property. These properties
	 * will be added in dbProperties object
	 */
	public DBManager() {
		String DATABASE = System.getProperty("db.oracle.ip") + ":" + System.getProperty("db.oracle.port") + ":"
				+ System.getProperty("db.oracle.sid");
		String DBUSER = System.getProperty("db.oracle.user");
		String PASSWORD = System.getProperty("db.oracle.password");
		dbProperties = new Properties();

		dbProperties.put("user", DBUSER);
		dbProperties.put("password", PASSWORD);
		dbProperties.put("database", DATABASE);
	}

	/*
	 * This method simply runs the query on the database made by the connection
	 * from the dbProperties object's detail
	 */
	public void runQuery(String query, Logger logger)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.OracleDriver").newInstance();

		Connection connection = null;
		Statement stmt = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
			connection.commit();
		} catch (Throwable e) {
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.rollback();
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public int addSuiteInfo(String suiteName, String testcase, Logger logger)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.OracleDriver").newInstance();

		Connection connection = null;
		Statement stmt = null;
		ResultSet result = null;
		int id = -1;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			stmt.executeQuery("LOCK TABLE testcase_table IN EXCLUSIVE MODE");
			String noRunStr = "insert into testcase_table (id,RUN_ID,SUITE_NAME,TESTCASE,STATUS,TC_TIMESTAMP,BROWSER,TC_RUN_ID ) "
					+ "values(ID.nextval," + "(SELECT MAX(ID) FROM URL_TABLE WHERE CONFIG_ID='"
					+ System.getProperty("CONFIGID") + "')" + ",'" + suiteName + "','" + testcase + "'," + "'NOT RUN',"
					+ "sysdate," + "UPPER('" + System.getProperty("browser") + "')," + "'0'" + ")";
			stmt.executeUpdate(noRunStr);
			String getId = "SELECT MAX(id) FROM testcase_table where TESTCASE='" + testcase + "'";
			result = stmt.executeQuery(getId);
			result.next();
			id = result.getInt(1);
			connection.commit();
		} catch (Throwable e) {
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.rollback();
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.close();
			}
			if (result != null) {
				result.close();
			}
		}
		return id;
	}
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		Connection connection = null;
		int retryCount = 0;
		while (connection == null && retryCount < 10) {
			try {
				connection = DriverManager.getConnection("jdbc:oracle:thin:@", this.dbProperties);
				return connection;
			} catch (SQLException e) {
				System.out.println("Retying count : " + (retryCount + 1) + "...");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
			}
			retryCount++;
		}
		throw new Exception("Connection cannot be created.");

	}
}
	
