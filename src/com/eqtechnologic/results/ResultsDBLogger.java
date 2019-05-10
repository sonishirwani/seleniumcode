package com.eqtechnologic.results;
/**
 * This class was created to log the status of snapshots comparison result into the DB.
 */
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.eqtechnologic.db.oracle.DBManager;

public class ResultsDBLogger {

	/**
	 * This method take the following inputs and run insert query to mark the snapshot comparison result to database.
	 * @param confID
	 * @param suiteName
	 * @param tcName
	 * @param snapName
	 * @param path
	 * @param logger
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void updateStatus(String confID, String suiteName, String tcName,String snapName, String path,String result,String sourceResolution,String baseResolution,Logger logger) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String noRunStr = "INSERT INTO TC_RESULT_TABLE (ID,RUN_ID,SUITE_NAME,SNAPSHOT_TESTCASE_ID,TC_NAME,TC_SNAP_NAME,SNAP_COMP_RESULT,PATH,TC_RUN_ID,SOURCERESOLUTION,BASERESOLUTION) " +
	   			"values(RESULT_ID.nextval,"
					+"(SELECT MAX(ID) FROM URL_TABLE WHERE CONFIG_ID='"+confID+"')"+",'"+
					suiteName+"'," +
					"(SELECT MAX(ID) FROM TESTCASE_TABLE WHERE TESTCASE='"+tcName+"')"+" ,'"+   //line added by suhel
					tcName+"','" +
					snapName+"','" +
					result+"','" +
				path+"',"+"(SELECT MAX(TC_RUN_ID) FROM TESTCASE_TABLE WHERE TESTCASE='"+tcName+"' "
				+ " AND RUN_ID = (SELECT MAX(ID) FROM URL_TABLE WHERE CONFIG_ID='"+confID+"'group by TESTCASE))"+",'"+
				sourceResolution+"','" +
				baseResolution+"' "+")";
 
    	String dbLogging = System.getProperty("db.logging");
    	if(dbLogging == null || dbLogging.equalsIgnoreCase("FALSE"))
    		return;
    	//System.out.println(noRunStr);
		
		DBManager db = new DBManager();
    	try {
			db.runQuery(noRunStr,logger);
		}catch (SQLException e) {
			if(e.getMessage().contains("ORA-01400: cannot insert NULL into"))
				logger.fatal("No ID could be found corresponding to CONFIG_ID: "+System.getProperty("CONFIGID")+" in the URL_TABLE !! Hence skipping all Tests !",e);
			Assert.fail(e.getMessage());
		}
	    
    }
/**********************************************************Code added by suhel for db loging of Excel Comparartor*********************************************/
	public static void updateExcelStatus(String confID, String suiteName, String tcName, String result, String snapName, String path,Logger logger) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
    	String noRunStr = "INSERT INTO EXCEL_RESULT_TABLE(EXCEL_ID,EXCEL_RUN_ID,EXCEL_SUITE_NAME,EXCEL_TESTCASE_ID,EXCEL_TC_NAME,EXCEL_COMPARISION_RESULT,EXCEL_TC_EXCEL_NAME,EXCEL_PATH,TC_RUN_ID) " +
    			"values(RESULT_ID.nextval,"
				+"(SELECT MAX(ID) FROM URL_TABLE WHERE CONFIG_ID='"+confID+"')"+",'"+
				suiteName+"'," +
				"(SELECT MAX(ID) FROM TESTCASE_TABLE WHERE TESTCASE='"+tcName+"')"+",'"+   //line added by suhel
				tcName+"','" +
				result+"','" +
				snapName+"','" +
				path+"',"+"(SELECT MAX(TC_RUN_ID) FROM TESTCASE_TABLE WHERE TESTCASE='"+tcName+"' "
						+ " AND RUN_ID = (SELECT MAX(ID) FROM URL_TABLE WHERE CONFIG_ID='"+confID+"'group by TESTCASE)))";
  
    	String dbLogging = System.getProperty("db.logging");
    	if(dbLogging == null || dbLogging.equalsIgnoreCase("FALSE"))
    		return;
    	//System.out.println(noRunStr);  /*commented by suhel as it was getting displayed in Output.xls file */
		
		DBManager db = new DBManager();
    	try {
			db.runQuery(noRunStr,logger);
		}catch (SQLException e) {
			if(e.getMessage().contains("ORA-01400: cannot insert NULL into"))
				logger.fatal("No ID could be found corresponding to CONFIG_ID: "+System.getProperty("CONFIGID")+" in the URL_TABLE !! Hence skipping all Tests !",e);
			Assert.fail(e.getMessage());
		}
	    
    }
 
 /************************************************************************************************************************************************************/
}