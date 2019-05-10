package com.eqtechnologic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import au.com.bytecode.opencsv.CSVReader;

public class csvToxlsConverter {
	static CSVReader reader;
public static void xls(File inputFile, String outputxlsFilePath, String testCaseName) throws IOException,FileNotFoundException
{
	  FileOutputStream output_file=null;
    /* Step -1 : Read input CSV file in Java */
     try {
		reader = new CSVReader(new FileReader(inputFile));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    /* Variables to loop through the CSV File */
    String [] nextLine; /* for every line in the file */            
    int lnNum = 0; /* line number */
    /* Step -2 : Define POI Spreadsheet objects */          
    HSSFWorkbook new_workbook = new HSSFWorkbook(); //create a blank workbook object
    HSSFSheet sheet = new_workbook.createSheet(testCaseName);  //create a worksheet with caption score_details
    /* Step -3: Define logical Map to consume CSV file data into excel */
    Map<String, Object[]> excel_data = new HashMap<String, Object[]>(); //create a map and define data
    /* Step -4: Populate data into logical Map */
  
		while ((nextLine = reader.readNext()) != null) {
		        lnNum++;                        
		        excel_data.put(Integer.toString(lnNum), new Object[] {nextLine[0],nextLine[1],nextLine[2],nextLine[8]});                        
		}
	
    /* Step -5: Create Excel Data from the map using POI */
    Set<String> keyset = excel_data.keySet();
    int rownum = 0;
    for (String key : keyset) { //loop through the data and add them to the cell
            Row row = sheet.createRow(rownum++);
            Object [] objArr = excel_data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if(obj instanceof Double)
                            cell.setCellValue((Double)obj);
                    else
                            cell.setCellValue((String)obj);
                    }
    }
    /* Write XLS converted CSV file to the output file */
  
		output_file = new FileOutputStream(new File(outputxlsFilePath));
	//create XLS file
  
		new_workbook.write(output_file);
	//write converted XLS file to output stream
    try {
		output_file.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} //close the file
}
 
 }

