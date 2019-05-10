package com.eqtechnologic.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author shantanud.
 * 
 * @Comments
 * This Class is used for loading an Object repository file into a HashMap. This hashmap can be used later on to get Locator values on providing locator keys.
 */
public class ObjectRepository {

	private Logger logger;
	final static int LOCATOR_KEY_COL_INDEX = 1;
	final static int LOCATOR_VALUE_COL_INDEX = 3;

	public ObjectRepository(Logger logger2) {
		// TODO Auto-generated constructor stub
		this.logger = logger2;
	}

	public HashMap<String, String> createLocatorMap() {

		HashMap<String, String> repositHashMap = new HashMap<String, String>();
		FileInputStream inputStream ;
		String excelFilePath = "Resources\\Excel Object Repo.xls";
		String locatorKey = null, locatorValue = null;
		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook outPutWorkBookx = getWorkbook(inputStream, excelFilePath);
			int noOfSheets = outPutWorkBookx.getNumberOfSheets();
			logger.fatal("*** Number of sheets in Object Repository Excel = "+noOfSheets);
			for(int i = 0 ; i < noOfSheets ; i++)
			{
				
				logger.fatal("*** Iterating over Sheet Name = "+outPutWorkBookx.getSheetName(i));
				Sheet excelSheet = outPutWorkBookx.getSheetAt(i);
				Iterator<Row> iterator = excelSheet.iterator();

				while (iterator.hasNext()) {
					locatorKey = null;
					locatorValue = null;
					Row nextRow = iterator.next();
					Iterator<Cell> cellIterator = nextRow.cellIterator();

					while (cellIterator.hasNext()) {

						Cell nextCell = cellIterator.next();
						int columnIndex = nextCell.getColumnIndex();
						switch (columnIndex) {
						case LOCATOR_KEY_COL_INDEX:
							locatorKey =(String) getCellValue(nextCell);
							break;
						case LOCATOR_VALUE_COL_INDEX:
							locatorValue =(String) getCellValue(nextCell);
							break;
						}
					}
					if(locatorValue == null || locatorKey == null)
						logger.fatal("Locator value or Locator key is NULL. Please check object repository file. Row Number = "+nextRow.getRowNum());
					else
						repositHashMap.put(locatorKey.trim(), locatorValue.trim());	
				}
			}
			inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NullPointerException e) {
			// TODO: handle exception
			System.out.println("Locator value not found in Map. Kindly check Object Repository file. For Key = "+locatorKey);
		}

		System.out.println("HashMap Size = "+repositHashMap.size());
		return repositHashMap;

	}

	private static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();

		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();

		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		}

		return null;
	}

	public static Workbook getWorkbook(FileInputStream inputStream, String excelFilePath)
			throws IOException {
		Workbook workbook = null;

		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}

}