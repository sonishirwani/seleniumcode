package com.eqtechnologic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.eqtechnologic.exceptions.ExcelConnectionException;
/**This class provides the utility to access the data stored in excel.
 * It works well with both the extension types XLS or XLSX.
 * @author mayank
 */

public class ExcelProcessor {
	private File file;
	public String sheetName;
	public int rowCount; /* The total number of rows in the excel sheet */
	private int extension; /* The extension of the workbook. XLS or XLSX*/
	private static final int XLSX=1;
	private static final int XLS=2;
	private Logger logger;
	/**
	 * This method identifies extension of provided excel.		
	 * @param path - Path of the file relative or absolute
	 * @return
	 */
	private int getExtension(String path){
		
		String sub = path.substring(path.length()-4);
		int extension;
		if(sub.equals("xlsx"))
			extension=1;
		else
			extension=2;
		return extension;
	}
	/**Default constructor
	 */
	public ExcelProcessor(){}
	/**
	 * This constructor will initialise all the class variables and store information in them. 
	 * @param filePath
	 * @param sheetName
	 * @throws ExcelConnectionException 
	 */
	public ExcelProcessor(String filePath, String sheetName, Logger logger) throws ExcelConnectionException{
		this.file =  new File(filePath);
		this.sheetName = sheetName;
		this.extension = getExtension(filePath);
		this.logger = logger;
		FileInputStream in=null;
		Workbook workbook = null;
		try {
			try{
				in = new FileInputStream(this.file);
							
			 }catch (Exception e){
				String message = "Some IOException occurred while trying to read the excel sheet: "+ this.sheetName+ " in directory: "+ this.file 
									+". Please make sure that the test suite/test cases are present at the right path";
				logger.error(message);
				throw new ExcelConnectionException(message,e);
			 }
			
			if(this.extension == XLSX){
				try {
					workbook = new XSSFWorkbook(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				try {
					workbook = new HSSFWorkbook(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Sheet sheet = workbook.getSheet(this.sheetName);
			this.rowCount = sheet.getLastRowNum();
			
		} finally {
			if(in !=null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//copy
		public ExcelProcessor(String filePath, String sheetName) throws ExcelConnectionException{
			this.file =  new File(filePath);
			this.sheetName = sheetName;
			this.extension = getExtension(filePath);
			FileInputStream in=null;
			Workbook workbook = null;
			try {
				try{
					in = new FileInputStream(this.file);
								
				 }catch (Exception e){
					String message = "Some IOException occurred while trying to read the excel sheet: "+ this.sheetName+ " in directory: "+ this.file 
										+". Please make sure that the test suite/test cases are present at the right path";
									throw new ExcelConnectionException(message,e);
				 }
				
				if(this.extension == XLSX){
					try {
						workbook = new XSSFWorkbook(in);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						workbook = new HSSFWorkbook(in);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Sheet sheet = workbook.getSheet(this.sheetName);
				if(sheet!=null)
				{
				this.rowCount = sheet.getLastRowNum();
				}
						} finally {
				if(in !=null){
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
	/**
	 * This method will create the sheet if it is not present in it,
	 * otherwise if the sheet is already present in the excel it will delete it and create a new sheet.
	 * @param filePath - Workbook Path
	 * @param sheetName - Name of the sheet
	 * @throws IOException
	 */
	public void createExcel(String filePath, String sheetName ) throws IOException{
		this.file =  new File(filePath);
		this.sheetName = sheetName;
		this.extension = getExtension(filePath);
		Workbook workbook = null;
		FileInputStream  fileIn=null;
		
		if(this.file.exists()){
			fileIn = new FileInputStream(this.file);
			if(this.extension == XLSX)
				workbook = new XSSFWorkbook(fileIn);
			else if(this.extension == XLS)	
				workbook = new HSSFWorkbook(fileIn);
		}else{
			if(this.extension == XLSX)
				workbook = new XSSFWorkbook();
			else if(this.extension == XLS)	
				workbook = new HSSFWorkbook();
		}
			if(workbook.getSheet(sheetName)!=null)
				workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
			
			workbook.createSheet(sheetName);
			FileOutputStream fileOut= new FileOutputStream(this.file);
			workbook.write(fileOut);
			
			fileOut.close();
			if(fileIn!=null)
				fileIn.close();
	}
	
	/**
	 * This method returns cell value based on the row and column.
	 * If the cell is null, it will return null. 
	 * Returns null if the row is null.
	 * @param rownum - Row number where the value is required
	 * @param colnum - Column number where the value is required
	 * @return
	 */
	public String getCellValue(int rownum, int colnum){
		FileInputStream in = null;
		try {
			in = new FileInputStream(this.file);
		} catch (FileNotFoundException e) {
			logger.error("Some error occurred while reading the value from the cell", e);
		}
		Workbook workbook = null;
		
		
		try {
			if(this.extension == XLSX)
				workbook = new XSSFWorkbook(in);
			else if(this.extension == XLS)	
				workbook = new HSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Sheet sheet = workbook.getSheet(this.sheetName);
		Row row = sheet.getRow(rownum);
		if(row == null){
			return null;
		}
		Cell cell = row.getCell(colnum,Row.CREATE_NULL_AS_BLANK);
		
		switch(cell.getCellType()){
		
		case Cell.CELL_TYPE_NUMERIC: return ((Integer)((Double)cell.getNumericCellValue()).intValue()).toString();
		case Cell.CELL_TYPE_BOOLEAN: return ((Boolean)cell.getBooleanCellValue()).toString();
		case Cell.CELL_TYPE_STRING: return cell.getStringCellValue();
		case Cell.CELL_TYPE_BLANK: return null;				
		}
		
		return null;
	}
	/**
	 * It updates the value, specified in the parameter, of a cell by providing its row and column numbers.
	 * @param rownum
	 * @param cellnum
	 * @param content
	 */
	public void updateCellValue(int rownum, int cellnum, String content){
		
		FileInputStream in = null;
		FileOutputStream out= null;
		
		try {
			try {
				in = new FileInputStream(this.file);
			} catch (FileNotFoundException e) {
				logger.error("Some error occurred while reading the value from the cell", e);
			}
			Workbook workbook = null;
			
			try {
				if(this.extension == XLSX)
					workbook = new XSSFWorkbook(in);
				else if(this.extension == XLS)	
					workbook = new HSSFWorkbook(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
					
			Sheet sheet = workbook.getSheet(this.sheetName);
			Row row = sheet.getRow(rownum);
			if(row == null)
				row = sheet.createRow(rownum);
			
			Cell cell = row.createCell(cellnum);
			cell.setCellValue(content);

			try {
				out = new FileOutputStream(this.file);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			if(in!= null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(out!= null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
				
	}
	
	//This method creates sheet with the given name
		public void createSheet(String sheetName)
		{
			FileInputStream in = null;
			FileOutputStream out= null;
			try {
				in = new FileInputStream(this.file);
			} catch (FileNotFoundException e) {
				logger.error("Some error occurred while reading the value from the cell", e);
			}
			Workbook workbook = null;
			
			
			try {
				if(this.extension == XLSX)
					workbook = new XSSFWorkbook(in);
				else if(this.extension == XLS)	
					workbook = new HSSFWorkbook(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		   workbook.createSheet(sheetName);
		   
			try {
				out = new FileOutputStream(this.file);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		finally {
			if(in!= null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(out!= null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
		//Returns the sheet object if provided sheet name is present in the excel file
		public Sheet GetSheet(String sheetName,Sheet sheet)
		{
			FileInputStream in = null;
			FileOutputStream out= null;
			try {
				in = new FileInputStream(this.file);
			} catch (FileNotFoundException e) {
				logger.error("Some error occurred while reading the value from the cell", e);
			}
			Workbook workbook = null;
			
			
			try {
				if(this.extension == XLSX)
					workbook = new XSSFWorkbook(in);
				else if(this.extension == XLS)	
					workbook = new HSSFWorkbook(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		   sheet=workbook.getSheet(sheetName);
			if(in!= null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			return sheet;

	}
		//This method deletes the given sheet from the workbook and writes to the excel file
		public void deleteSheet(String sheetName)
		{
			FileInputStream in = null;
			FileOutputStream out= null;
			try {
				in = new FileInputStream(this.file);
			} catch (FileNotFoundException e) {
				logger.error("Some error occurred while reading the value from the cell", e);
			}
			Workbook workbook = null;
			
			
			try {
				if(this.extension == XLSX)
					workbook = new XSSFWorkbook(in);
				else if(this.extension == XLS)	
					workbook = new HSSFWorkbook(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(workbook.getSheet(sheetName)!=null)
				workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
			try {
				out = new FileOutputStream(this.file);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		finally {
			if(in!= null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(out!= null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
		//This method returns the total count of sheets present in the excel workbook
			public int getNumberOfSheets()
			{
				FileInputStream in = null;
				int count;
				try {
					in = new FileInputStream(this.file);
				} catch (FileNotFoundException e) {
					logger.error("Some error occurred while reading the value from the cell", e);
				}
				Workbook workbook = null;
				
				
				try {
					if(this.extension == XLSX)
						workbook = new XSSFWorkbook(in);
					else if(this.extension == XLS)	
						workbook = new HSSFWorkbook(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
				 count = workbook.getNumberOfSheets();
				
				if(in!= null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				return count;
				
	}
			//This method writes map data to an excel
			public void writeTOExcelFromMap(Map<String, ArrayList<String>> map,String filePath,String testCase)
			{
				
				 Set<String> keyset = map.keySet();
				 if( keyset==null){
					 return;
				 }
						
				FileInputStream in = null;
				FileOutputStream out= null;
				Workbook workbook = null;
				try {
					in = new FileInputStream(filePath);
				} catch (FileNotFoundException e) {
					logger.error("Some error occurred while reading the value from the cell", e);
				}
				if(this.extension == XLSX)
					try {
						workbook = new XSSFWorkbook(in);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				else if(this.extension == XLS)
					try {
						workbook = new HSSFWorkbook(in);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			   
			    int rownum = 1;
	       
			    //System.out.println(rownum);
			    Sheet sheet = workbook.getSheet(testCase);
			     Row row = sheet.createRow(rownum);
			    for (String key : keyset) { //loop through the data and add them to the cell
			    	
				           // StringBuilder testcases=new StringBuilder();
			            ArrayList<String> objArr = map.get(key);     
		            	    for (Object obj : objArr) {
		            	    	 int cellnum = 0; 
		            	    	 Cell cell = row.createCell(cellnum++);
		     	            	cell.setCellValue(key);
		            	    	Cell cell1 = row.createCell(cellnum);                  
			                       cell1.setCellValue(obj.toString());
			                     row = sheet.createRow(rownum++);
			                    }
		            	    
		                    }
			    try {
					out = new FileOutputStream(filePath);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//create XLS file
			  
					try {
						workbook.write(out);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			finally {
			if(in!= null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(out!= null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
		
}
