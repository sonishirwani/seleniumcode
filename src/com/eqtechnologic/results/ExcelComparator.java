package com.eqtechnologic.results;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.eqtechnologic.util.ExcelProcessor;
import com.eqtechnologic.util.FrameworkLogger;
import com.eqtechnologic.util.PropertiesToSystem;

public class ExcelComparator {

	private static final int IMAGEEQUAL = 0;
	private static final int IMAGENOTEQUAL = 1;
	private static final int IMAGENOTFOUND = 2;
	private static final int SUITECELL = 0;
	private static final int TCCELL = 1;
	private static final int SNAPCELL = 2;
	public static String sourceTC = null; // line added by suhel for file not
											// found in source directory
	public static String exportTCName = null; // line added by suhel for file
												// not found in source directory
												// source
	private static Logger logger = new FrameworkLogger("Results.log").getInstance(); // line
																						// added
																						// by
																						// suhel
																						// for
																						// db
																						// login
	private static String path = null;
	private static List<RowContents> rowContents = DatabaseUtility.getAllRowData();

	/**
	 * @param args
	 *            args[0] = Base Directory args[1] = Destination Directory
	 *            args[2] = Configuration ID
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExcelConnectionException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String args[]) throws IOException, InterruptedException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		try{
		PropertiesToSystem properties = new PropertiesToSystem("Configuration/config.properties");
		properties.load();

		String Sy = System.getProperty("excelBaseDirectory");
		File F5 = new File(Sy);
		String Sy1 = F5.getAbsolutePath();
		int index = Sy1.indexOf(Sy);
		String ExcelResultPath = Sy1.substring(0, index - 1);
		String srcDirectory1 = ExcelResultPath + "/" + "ExcelResult";

		new File(srcDirectory1).mkdir();
		// Above Code is for Creating ExcelResult Folder

		Thread.sleep(15000);


		File srcDirectory = new File(args[0]);
		File baseDirectory = new File(args[1]);
		File destDirectory = new File(args[2]);
		String confID = args[3];
		//String confID = "araut";
		
		 /*File srcDirectory = new File(
		 "E:\\For Siddharth EndTo_End_All\\Installation\\Excels"); File
		 baseDirectory = new File(
		 "E:\\For Siddharth EndTo_End_All\\Installation\\BaselineExcel"); File
		 destDirectory = new File(
		 "E:\\For Siddharth EndTo_End_All\\Installation\\ExcelResult");*/
		 

		System.setOut(new PrintStream(new FileOutputStream(destDirectory + "/Output.xls")));

		ExcelProcessor excel = null;

		excel = new ExcelProcessor();
		excel.createExcel(destDirectory + "/Results.xls", "comparison");
		File[] suites = getSubDirectories(srcDirectory);
		File[] baseTCS = getAllSubDirectories(baseDirectory);
		/************************************
		 * code added by suhel
		 **********************************************/
		File[] baseSuites = getSubDirectories(baseDirectory);
		/*****************************************************************************************************/
		int row = 0;
		excel.updateCellValue(row, 0, "Suite Name");
		excel.updateCellValue(row, 1, "Test Case Name");
		excel.updateCellValue(row, 2, "Excel Name");
		excel.updateCellValue(row, 3, "Result"); // line added by suhel for
													// adding one more result
													// column
		row++;
		for (File suite : suites) {
			int compareStatus = -1;
			File[] testCases = getSubDirectories(suite);
			String suiteName = suite.getName();
			excel.updateCellValue(row, SUITECELL, suiteName);
			row++;
			for (File tc : testCases) {
				String tcName = tc.getName();
				File[] snapshots = getLeafNodes(tc);
				File baseTC = null;
				boolean baseTCExist = false;

				for (File TC : baseTCS) {
					if (TC.getName().equals(tcName)) {

						baseTCExist = true;
						baseTC = TC;
						sourceTC = tcName;
						break;
					} else {
						baseTCExist = false;
					}
				}

				if (baseTCExist) {
					boolean firstRun = true;
					String snapshotName = null;
					if (snapshots.length > 0) {
						for (File srcSnapshot : snapshots) {

							snapshotName = srcSnapshot.getName();
							// Get First Excel File Name
							path = srcSnapshot.getAbsolutePath();
							// Get Excel Path
							File baseSnapshot = new File(baseTC + "/" + snapshotName);
							// Get Second Excel File Name
							compareStatus = compareSnapshots(srcSnapshot, baseSnapshot,
									new File(destDirectory + "/" + suiteName + "/" + tcName + "/" + snapshotName));
									// Calls Compare Snapshots method in which
									// actual comparison of excels will be done

							/*******************
							 * Code added by suhel for the pass cases in case of
							 * excel comparision
							 **********/

							if (compareStatus == IMAGEEQUAL) {
								String result = null; // line added for db
														// loging
								if (firstRun) {
									excel.updateCellValue(row, TCCELL, tcName);
									row++;
									firstRun = false;
								}
								result = "PASS"; // line added for status pass
								ResultsDBLogger.updateExcelStatus(confID, suiteName, tcName, result, snapshotName, path,
										logger);
								// line added for db loging
								excel.updateCellValue(row, SNAPCELL, snapshotName);
								excel.updateCellValue(row, 3, "PASS");
								row++;
							}

							/************************************************************************************************/
							if (compareStatus == IMAGENOTEQUAL) {
								String result = null; // line added for db
														// loging
								if (firstRun) {
									excel.updateCellValue(row, TCCELL, tcName);
									row++;
									firstRun = false;
								}
								result = "FAIL"; // line added for status pass
								ResultsDBLogger.updateExcelStatus(confID, suiteName, tcName, result, snapshotName, path,
										logger);
								// line added for db loging
								excel.updateCellValue(row, SNAPCELL, snapshotName);
								excel.updateCellValue(row, 3, "FAIL");
								row++;

							}
						}
						/****************
						 * Code added by suhel if excel export fails
						 **********************/
					} else {
						String result = null; // line added for db loging
						if (firstRun) {
							excel.updateCellValue(row, TCCELL, tcName);
							firstRun = false;
							exportTCName = tcName;
						}
						result = "Source excel missing"; // line added for
															// status pass
						ResultsDBLogger.updateExcelStatus(confID, suiteName, tcName, result, snapshotName, path, logger); // line
						// added for db loging
						excel.updateCellValue(row, SNAPCELL, snapshotName);
						excel.updateCellValue(row, 3, "*Not found in source directory");
						row++;
					}
				} else {
					String result = null; // line added for db loging
					String snapshotName = null;
					result = "Baseline folder missing"; // line added for status
														// pass
					ResultsDBLogger.updateExcelStatus(confID, suiteName, tcName, result, snapshotName, path, logger); // line
																															// added
					// for db loging
					excel.updateCellValue(row, TCCELL, tcName); // line added on
																// 5th april
					excel.updateCellValue(row, SNAPCELL, snapshotName);
					excel.updateCellValue(row, 3, "Not found in Base directory");
					row++;
				}
			}

			/************
			 * Code written by suhel for handling if file is not present in
			 * source folder
			 **********************************/

			for (File basesuite : baseSuites) {
				if (suite.getName().equals(basesuite.getName())) {
					File[] basetestCases = getSubDirectories(basesuite);
					/*************************************************
					 * Code added by suhel if baseline and souce are same
					 ****************************************/

					String tempbaseTCName = null; // new line added for storing
													// temprary base name

					String result = null;

					for (File TC : basetestCases) {
						int i=0;
						String baseTCName = TC.getName();
						for (File tcsource : testCases) {
							if (!baseTCName.equals(tempbaseTCName)) {
								String sourceTCName = tcsource.getName();
								
								for (File tcsourcename : testCases) {
	/********************code is added(5-Apr-2017)****************************************************/								
									if (baseTCName.equals(tcsourcename.getName())) {
										i=1;
	
	/*******************code is added(5-May-2017)***************************************************/
										if(TC.listFiles().length!=tcsourcename.listFiles().length){
											File[] snapshots = getLeafNodes(TC);
											File[] sourceSnapshots = getLeafNodes(tcsourcename);
											for (File tcbase1 : snapshots) {
												for (File tcsoucename1 : sourceSnapshots) {
													if (!tcbase1.getName().equals(tcsoucename1.getName())) {
														// System.out.println("Source
														// Excel is missing");
														result = "Source excel missing"; // line
																							// added
																							// for

														ResultsDBLogger.updateExcelStatus(confID, suiteName, baseTCName,
																result, tcbase1.getName(), path, logger);

														 ResultsDBLogger.updateExcelStatus(confID,suiteName,
														 baseTCName,
														 result,tcbase1.getName(),
														 path,logger);
														 //line
														// added for db loging
														excel.updateCellValue(row, SNAPCELL, tcbase1.getName());
														excel.updateCellValue(row, 3, "*Not found in source directory");
														row++;
													}
												}
											}
										}
										
	/***********************************************************************************************/
										
										break;
									}
								}
								if(i==1){
									break;
								}
/*****************************************************************************************************/								
								if (!baseTCName.equals(sourceTCName)) {
									if (sourceTC.equals(baseTCName)) {
										break;
									} else if (baseTCName.equals(exportTCName)) {
										break;
									} else {
										for (File basesuit : baseTCS) {
											boolean matchFound = false;
											if (basesuit.getName().equals(suite.getName())) {
												for (int k = 0; k < rowContents.size(); k++) {
													RowContents rowContent = rowContents.get(k);
													String runId = rowContent.runId;
													String suitName = rowContent.suitName;
													String tcName = rowContent.tcName;
													String dbResult = rowContent.result;

													if (suitName.equals(suiteName) && tcName.equals(baseTCName)
															&& dbResult.equalsIgnoreCase("pass")) {
														matchFound = true;
														break;
													}
												}
												if (matchFound == true)
													break;
												excel.updateCellValue(row, TCCELL, baseTCName);
												excel.updateCellValue(row, 3, "Not found in source directory");
												row++;
												tempbaseTCName = baseTCName;
												result = "Source directory missing"; // line
																						// added
																						// for
																						// status
																						// pass
												ResultsDBLogger.updateExcelStatus(confID, suiteName, baseTCName, result,
														null, path, logger); // line
																				// added
												// for db loging
											}
										}
									}
								}
							} 
						}
						
					}
				
			}
			/******************************************************************************************************************************/

			row++;
		}
		}
		// System.exit(0);
		}catch(Exception e){
			System.err.println("BaslineExcel/Excels folder is not present for Excel Comparison");
	}
	}

	public static int compareSnapshots(File firstSnap, File secondSnap, File destSnap) throws FileNotFoundException {
		// Comparing First Excel and Second Excel, Please ignore method name as
		// comparesnapshots it is actully comparing the excels
		try {
			String s1 = firstSnap.getName();
			String s2 = secondSnap.getName();
			if (s1.equals(s2)) {
				System.out.println("Comparing Excel :" + s1);
				System.out.println("\n");
				FileInputStream excellFile1 = new FileInputStream(new File(firstSnap.getPath()));
				FileInputStream excellFile2 = new FileInputStream(new File(secondSnap.getPath()));
				HSSFWorkbook workbook1 = new HSSFWorkbook(excellFile1);
				HSSFWorkbook workbook2 = new HSSFWorkbook(excellFile2);

				int a1 = workbook1.getNumberOfSheets();
				int sheetfailcount = 0;
				for (int i = 0; i < a1; i++) {
					System.out.println("#Excel Sheet " + i + "Comparison Result-->");
					HSSFSheet sheet1 = workbook1.getSheetAt(i);
					HSSFSheet sheet2 = workbook2.getSheetAt(i);

					String t1 = sheet1.getSheetName();
					String t2 = sheet2.getSheetName();
					System.out.println("Comparing Excel Sheets :" + t1 + "  &  " + t2);

					if (compareTwoSheets(sheet1, sheet2)) {
						System.out.println("The two excel sheets are Same");
						System.out.println("\n");

					} else {
						System.out.println("The two excel sheets are Not Same");
						System.out.println("\n");
						sheetfailcount++;

					}

					excellFile1.close();
					excellFile2.close();
					// break;

				}
				if (sheetfailcount > 0) {
					return IMAGENOTEQUAL;
				} else {
					return IMAGEEQUAL;
				}

			}

			else {
				System.out.println("Excel are not same");
				return IMAGENOTEQUAL;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return -1;
	}

	// Compare Two Sheets Method
	public static boolean compareTwoSheets(HSSFSheet sheet1, HSSFSheet sheet2) throws IOException {

		int firstRow1 = sheet1.getFirstRowNum();
		int lastRow1 = sheet1.getLastRowNum();

		boolean equalSheets = true;
		for (int i = firstRow1; i <= lastRow1; i++) {

			// System.out.println("Comparing Row "+i);

			HSSFRow row1 = sheet1.getRow(i);
			HSSFRow row2 = sheet2.getRow(i);
			if (!compareTwoRows(row1, row2)) {
				equalSheets = false;
				System.out.println("***** Row " + i + "data is not matching *****");

				// break;
			} else {

				// System.out.println("Row "+i+" - Similar");
			}
		}
		return equalSheets;
	}

	// Comparing Two Rows

	public static boolean compareTwoRows(HSSFRow row1, HSSFRow row2) {
		if ((row1 == null) && (row2 == null)) {
			return true;
		} else if ((row1 == null) || (row2 == null)) {
			System.out.println("row not present");
			return false;
		}

		int firstCell1 = row1.getFirstCellNum();
		int lastCell1 = row1.getLastCellNum();

		boolean equalRows = true;

		// Compare all cells in a row
		for (int i = firstCell1; i <= lastCell1; i++) {
			HSSFCell cell1 = row1.getCell(i);
			HSSFCell cell2 = row2.getCell(i);
			if (!compareTwoCells(cell1, cell2)) {
				equalRows = false;

				System.out.println("*****Column " + (i + 1) + "data is not matching*****");

				// Displaying Cell Values

				if (cell2 != null && cell2.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					System.out.println("               Baseline Value : " + cell2.getNumericCellValue());

				} else if (cell2 != null && cell2.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					System.out.println("               Baseline Value : " + cell2.getStringCellValue());

				} else if (cell2 != null && cell2.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
					System.out.println("               Baseline Value : " + cell2.getBooleanCellValue());

				} else if (cell2 != null && cell2.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
					System.out.println("               Baseline Value : " + cell2.getErrorCellValue());

				} else if (cell2 != null && cell2.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					System.out.println("               Baseline Value : " + cell2.getCellFormula());

				} else if (cell2 == null) {
					System.out.println("               Baseline Excel Value : " + "Blank");
				} else {
					System.out.println("RowNumber" + row2.getRowNum() + "                2ndExcel Cell --> Blank");

				}

				if (cell1 != null && cell1.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {

					System.out.println("               Current Excel Value : " + cell1.getNumericCellValue());
				} else if (cell1 != null && cell1.getCellType() == HSSFCell.CELL_TYPE_STRING) {

					System.out.println("               Current Excel Value : " + cell1.getStringCellValue());
				} else if (cell1 != null && cell1.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
					System.out.println("               Current Excel Value : " + cell1.getBooleanCellValue());

				} else if (cell1 != null && cell1.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
					System.out.println("               Current Excel Value : " + cell1.getErrorCellValue());

				} else if (cell1 != null && cell1.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					System.out.println("               Current Excel Value : " + cell1.getCellFormula());

				} else if (cell1 == null) {
					System.out.println("               Current Excel Value : " + "Blank");
				} else {
					System.out.println("RowNumber" + row1.getRowNum() + "                    1stExcel Cell --> Blank");

				}

				// break;
			} else {

			}
		}
		return equalRows;
	}

	// Comparing Two Cells

	public static boolean compareTwoCells(HSSFCell cell1, HSSFCell cell2) {

		if ((cell1 == null) && (cell2 == null)) {
			return true;
		} else if ((cell1 == null) || (cell2 == null)) {
			return false;
		}

		if (cell1.getCellType() == HSSFCell.CELL_TYPE_NUMERIC && cell2.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (cell1.getNumericCellValue() == 0.0 && cell2.getNumericCellValue() == 0.0) {

				return true;
			}
		}
		if (cell1.getCellType() == HSSFCell.CELL_TYPE_STRING && cell2.getCellType() == HSSFCell.CELL_TYPE_STRING) {

			if (cell1.getStringCellValue().trim().equals("") && cell2.getStringCellValue().trim().equals("")) {
				return true;
			}
		}

		if (cell1.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN && cell2.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {

			if (cell1.getBooleanCellValue() == false && cell2.getBooleanCellValue() == false) {
				return true;
			}
		}
		boolean equalCells = false;
		int type1 = cell1.getCellType();
		int type2 = cell2.getCellType();
		if (type1 == type2) {
			// Compare cells based on its type
			switch (cell1.getCellType()) {
			case HSSFCell.CELL_TYPE_FORMULA:
				if (cell1.getCellFormula().equals(cell2.getCellFormula())) {
					equalCells = true;
				}
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:

				if (cell1.getNumericCellValue() == cell2.getNumericCellValue()) {
					equalCells = true;
				}
				break;
			case HSSFCell.CELL_TYPE_STRING:
				if (cell1.getStringCellValue().equals(cell2.getStringCellValue())) {
					equalCells = true;
				}
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				if (cell1.getCellType() == HSSFCell.CELL_TYPE_BLANK
						&& cell2.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
					equalCells = true;
				}
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				if (cell1.getBooleanCellValue() == cell2.getBooleanCellValue()) {
					equalCells = true;
				}
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				if (cell1.getErrorCellValue() == cell2.getErrorCellValue()) {
					equalCells = true;
				}
				break;
			default:
				if (cell1.getStringCellValue().equals(cell2.getStringCellValue())) {
					equalCells = true;
				}
				break;
			}

		} else {
			return false;
		}
		return equalCells;
	}

	public static File[] getSubDirectories(File parentDirectory) {
		File[] files = parentDirectory.listFiles();
		ArrayList<File> directories = new ArrayList<File>();

		for (File file : files) {
			if (file.isDirectory())
				directories.add(file);

		}

		return directories.toArray(new File[directories.size()]);
	}

	public static File[] getAllSubDirectories(File baseDir) {

		Collection<File> collection = FileUtils.listFilesAndDirs(baseDir, new IOFileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}

			@Override
			public boolean accept(File arg0, String arg1) {
				return false;
			}

		}, TrueFileFilter.INSTANCE);

		return collection.toArray(new File[collection.size()]);
	}

	public static File[] getLeafNodes(File directory) {
		File[] files = directory.listFiles();
		ArrayList<File> leafNodes = new ArrayList<File>();

		for (File file : files) {
			if (file.isFile())
				leafNodes.add(file);
		}

		return leafNodes.toArray(new File[leafNodes.size()]);
	}

}