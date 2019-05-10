package com.eqtechnologic.results;
/**
 * This class reads all the screenshots which are present at a location and compare them
 * with the baseline and logs the result of comparison in an excel sheet and database.
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import com.eqtechnologic.exceptions.ExcelConnectionException;
import com.eqtechnologic.util.ExcelProcessor;
import com.eqtechnologic.util.FrameworkLogger;
import com.eqtechnologic.util.PropertiesToSystem;

public class SuiteComparison {

	private static final int IMAGEEQUAL = 0;
	private static final int IMAGENOTEQUAL = 1;
	private static final int IMAGENOTFOUND = 2;
	private static final int SUITECELL = 0;
	private static final int TCCELL = 1;
	private static final int SNAPCELL = 2;
	public static String sourceTC=null; //line added by suhel for file not found in source directory
	public static String exportTCName=null; //line added by suhel for file not found in source directory source
	private static Logger logger = new FrameworkLogger("Results.log").getInstance();
	private static String failedPath=null; //line added by suhel for failed path
	private static int widthmismatch=0;
	private static String ipAddress=null;
	private static String completefailedPath=null;
	private static int basesnapWidth;
	private static int basesnapHeight;
	private static int sourcesnapWidth;
	private static int sourcesnapHeight;
	private static String snapshotName;
	/**
	 * The main method takes the following parameters into the command line and call various other static methods mentioned 
	 * below to compare the screenshots with the 
	 * @param args
	 * args[0] = Base Directory
	 * args[1] = Destination Directory
	 * args[2] = Configuration ID
	 * @throws ExcelConnectionException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String args[]) throws IOException, ExcelConnectionException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		PropertiesToSystem properties = new PropertiesToSystem("Configuration/config.properties");
		properties.load();
		try{
		
		File srcDirectory = new File(System.getProperty("snapshotBaseDirectory"));
		File baseDirectory = new File(args[0]);
		File destDirectory = new File(args[1]);
		String confID = args[2];
		//String confID = "cf1";
		
		/*File srcDirectory =  new File("E:\\For Siddharth EndTo_End_All\\Installation\\Snapshots");
		File baseDirectory = new File("E:\\For Siddharth EndTo_End_All\\Installation\\Baselines");
		File destDirectory = new File("E:\\For Siddharth EndTo_End_All\\Installation\\SnapshotResult");*/
		
		ExcelProcessor excel=null;
		/*(new File(destDirectory+"/Results.xls")).getParentFile().mkdirs();
		(new File(destDirectory+"/Results.xls")).createNewFile();*/
		excel = new ExcelProcessor();
		excel.createExcel(destDirectory+"/Results.xls", "comparison");
		
		File[] suites = getSubDirectories(srcDirectory);
		File[] baseTCS = getAllSubDirectories(baseDirectory);
		/************************************code added by suhel**********************************************/
		File[] baseSuites=getSubDirectories(baseDirectory);
		/*****************************************************************************************************/
		int row = 0;
		excel.updateCellValue(row, 0, "Suite Name");
		excel.updateCellValue(row, 1, "Test Case Name");
		excel.updateCellValue(row, 2, "Snapshot Name");
		excel.updateCellValue(row, 3, "Result");  //line added by suhel for adding one more result column
		excel.updateCellValue(row, 4, "Source Resolution");  //line added by suhel for adding one more result column
		excel.updateCellValue(row, 5, "Baseline Resolution");  //line added by suhel for adding one more result column
		row++;
		for(File suite:suites){
			//int compareStatus = -1;
			File[] testCases = getAllSubDirectories(suite);
			String suiteName = suite.getName();
			excel.updateCellValue(row, SUITECELL, suiteName);
			row++;
			for(File tc:testCases){
				String tcName = tc.getName();
					
				File[] snapshots = getLeafNodes(tc);
				
				File baseTC = null;
				boolean baseTCExist=false;
				
				for(File TC:baseTCS){
					
					if(TC.getName().equalsIgnoreCase(tcName)){
						baseTCExist = true;
						baseTC = TC;
						break;
					}
				}
				
				if(baseTCExist){
					boolean firstRun = true;
					for(File srcSnapshot:snapshots){
						int compareStatus = -1; //line added by suhel for reseting comapre status
						snapshotName = srcSnapshot.getName();
						String path = srcSnapshot.getAbsolutePath();
						File baseSnapshot = new File(baseTC+"/"+snapshotName);
						//line added by suhel to ignore failure snapshot in failed folder.
						if (srcSnapshot.getName().contains("FAILURE")==false){
							compareStatus = compareSnapshots(srcSnapshot,baseSnapshot,new File(destDirectory+"/"+suiteName+"/"+tcName+"/"+snapshotName));
						}
/*******************Code added by suhel  for the pass cases in case of snapshot comparision**********/

						if (compareStatus == IMAGEEQUAL) {
							String result=null; // line added by suhel for dblogin result
							if (firstRun) {
								excel.updateCellValue(row, TCCELL, tcName);
								row++;
								firstRun = false;
							}
							result="PASS";   //line added for status PASS
							ResultsDBLogger.updateStatus(confID,suiteName, tcName, snapshotName, "null",result,"null","null",logger);
							excel.updateCellValue(row, SNAPCELL,
									snapshotName);
							excel.updateCellValue(row, 3, "PASS");
							row++;
						}
				
/************************************************************************************************/						

						if(compareStatus == IMAGENOTEQUAL && widthmismatch==0){
							String result=null; // line added by suhel for dblogin result
							if(firstRun){
								excel.updateCellValue(row, TCCELL, tcName);
								row++;
								firstRun = false;
							}
							result="FAIL"; //line added for status pass
							ResultsDBLogger.updateStatus(confID,suiteName, tcName, snapshotName, failedPath,result,"null","null",logger);
							excel.updateCellValue(row, SNAPCELL, failedPath);
							excel.updateCellValue(row, 3, "FAIL");
							row++;
						}else if(compareStatus == IMAGENOTEQUAL && widthmismatch==1){
							String sourceResolution=String.valueOf(sourcesnapWidth)+"*"+String.valueOf(sourcesnapHeight);
							String baseResolution=String.valueOf(basesnapWidth)+"*"+String.valueOf(basesnapHeight);
							String result=null; // line added by suhel for dblogin result
							if(firstRun){
								excel.updateCellValue(row, TCCELL, tcName);
								row++;
								firstRun = false;
							}
							result="FAIL - Resolution is mismatched"; //line added for status pass
							//System.out.println(failedPath);
							ResultsDBLogger.updateStatus(confID,suiteName, tcName, snapshotName, failedPath,result,sourceResolution,baseResolution,logger);
							excel.updateCellValue(row, SNAPCELL, failedPath);
							excel.updateCellValue(row, 3, "FAIL - Resolution is mismatched");
							excel.updateCellValue(row, 4, sourceResolution);
							excel.updateCellValue(row, 5, baseResolution);
							row++;
						}
/*********************************Line added by suhel as image is not found in base directory******************************/						
						if(compareStatus == IMAGENOTFOUND){
							String result=null; // line added by suhel for dblogin result
							result="SKIP";
							logger.info("TestCase does not exist in base folder! : \""+tc.getAbsolutePath()+"\"");
							excel.updateCellValue(row, TCCELL, tcName);
							row++;
							excel.updateCellValue(row, SNAPCELL, snapshotName);
							excel.updateCellValue(row, 3, "Not found in Base directory");
							ResultsDBLogger.updateStatus(confID,suiteName, tcName, snapshotName, "null",result,"null","null",logger);
							row++;
						}
/****************************************************************************************************************************/						
					}
				}
				else{
					//line added by suhel to ignore failed folder
					if(tcName.equalsIgnoreCase("failed")== false){
					String result="SKIP";
					logger.info("TestCase does not exist in base folder! : \""+tc.getAbsolutePath()+"\"");
					excel.updateCellValue(row, TCCELL, tcName);
					excel.updateCellValue(row, 3, "Not found in Base directory");
					ResultsDBLogger.updateStatus(confID,suiteName, tcName, snapshotName, "null",result,"null","null",logger);
					row++;
					}
				}
			}
			
/************Code written by suhel for handling if file is not present in source folder**********************************/
			
	/*for (File basesuite : baseSuites) {
				if(suite.getName().equals(basesuite.getName())){
				File[] basetestCases = getSubDirectories(basesuite);
				
				String tempbaseTCName=null;  //new line added for storing temprary base name
				
				for (File TC : basetestCases) {
					String baseTCName = TC.getName();
					
					for (File tcsource : testCases) {
						if(!baseTCName.equals(tempbaseTCName)){
						String sourceTCName=tcsource.getName();
						if (!baseTCName.equals(sourceTCName)) {
							if(sourceTC.equals(baseTCName)){
								break;
							}else if(exportTCName.equals(baseTCName)){
								break;
							}else{
							for (File basesuit : baseTCS) {
								if (basesuit.getName().equals(suite.getName())) {
									excel.updateCellValue(row, TCCELL,
											baseTCName);
									excel.updateCellValue(row, 3,
											"Not found in source directory");
									row++;
									tempbaseTCName=baseTCName;
									break;
								}
							}
						}
						}
					}
					}
				}
		}
			}*/
/******************************************************************************************************************************/			
			
			row++;
		}	
		}catch(Exception e){
			System.err.println("Baselines/Snapshots folder is not present for Snapshot Comparison");
		}
	}
	
	/**
	 * This method will compare the latest snapshot with the baseline snapshot and copy the 
	 * snapshot to the a location if both the images are not same.
	 * @param firstSnap
	 * @param secondSnap
	 * @param destSnap
	 * @return
	 */
	public static int compareSnapshots(File firstSnap, File secondSnap, File destSnap){
			try {
				if(FileUtils.contentEquals(firstSnap, secondSnap)){
					logger.info("Files are equal");
					return IMAGEEQUAL;
				}else{
					if(!secondSnap.exists()){
						logger.info("Snapshot: \""+firstSnap.getAbsolutePath()+"\" could not be found in base directory");
						return IMAGENOTFOUND;
						}
					else{
						logger.info("Files are not equal: \""+firstSnap.getAbsolutePath()+"\"");
						BufferedImage bi1 = ImageIO.read(firstSnap);
						sourcesnapWidth=bi1.getWidth();
						sourcesnapHeight=bi1.getHeight();
						BufferedImage bi2 = ImageIO.read(secondSnap);
						basesnapWidth=bi2.getWidth();
						basesnapHeight=bi2.getHeight();
						BufferedImage differentImage = getDifferenceImage(bi1, bi2);
						destSnap.mkdirs();
						boolean imagepath=ImageIO.write(differentImage, "png", destSnap);
/************************line added by suhel(01-aug-2017) for machine path******************/
						ipAddress= Inet4Address.getLocalHost().getHostAddress();
						completefailedPath=destSnap.getAbsolutePath(); //line added by suhel fo failed path
						String extractPath=completefailedPath.substring(2);
						failedPath="\\"+"\\"+ipAddress+extractPath;
/*****************************************************************************/						
						
						
						//FileUtils.copyFileToDirectory(firstSnap, destDir, true);
						return IMAGENOTEQUAL;
					}
						
				}
			} catch (IOException e) {
				
				logger.info(e.getMessage());
			}
			return -1;
	}
	
	/**
	 * This method returns all the first level sub directories.
	 * @param parentDirectory
	 * @return
	 */
	public static File[] getSubDirectories(File parentDirectory){
		//File parentDirectory = new File("snapshots/NextGenPortal");
		File[] files = parentDirectory.listFiles();
		ArrayList<File> directories = new ArrayList<File>();
		
		for(File file:files){
			if(file.isDirectory())
				directories.add(file);
			
		}
		
		return directories.toArray(new File[directories.size()]);
	}
	/**
	 * This method returns all the sub directories at any level of the provided parent directory.
	 * @param baseDir
	 * @return
	 */
	public static File[] getAllSubDirectories(File baseDir){

		Collection<File> collection = FileUtils.listFilesAndDirs(baseDir, new IOFileFilter (){
				public boolean accept(File file){
					return file.isDirectory();
				}
	
				@Override
				public boolean accept(File arg0, String arg1) {
					return false;
				}
				
			}, TrueFileFilter.INSTANCE);
			
		return collection.toArray(new File[collection.size()]);
	}
		
	/**
	 * This method returns all the first level files. 
	 * @param parentDirectory
	 * @return
	 */
	public static File[] getLeafNodes(File directory){
		File[] files = directory.listFiles();
		ArrayList<File> leafNodes = new ArrayList<File>();
		
		for(File file:files){
			if(file.isFile())
				leafNodes.add(file);
		}
		
		return leafNodes.toArray(new File[leafNodes.size()]);
	}
	
	/**
	 * This method takes the following parameters and compare both images
	 * pixel by pixel. If any changes are observed it modifies the original image's 
	 * pixel to red pixel to highlight the changes.
	 * @param img1
	 * @param img2
	 * @return
	 */
	public static BufferedImage getDifferenceImage(BufferedImage img1,BufferedImage img2) {
		final int w = img1.getWidth(), h = img1.getHeight();
		widthmismatch=0;
	/********************Code added by suhel if height and width is totally mismatched (20-07-2017)***********************/
		final int w1 = img2.getWidth(), h1 = img2.getHeight();
		final int highlight =Color.RED.getRGB();
		final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
		if(w!=w1 || h!=h1){
			for (int i = 0; i < p1.length; i++) {
				p1[i] = highlight;
				//else
				//cnt++;
			}
			final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			out.setRGB(0, 0, w, h, p1, 0, w);
			widthmismatch=1;
			return  out;
	/**********************************************************************************************************/		
		}else{
		
		final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);
		//int cnt = 0;
		for (int i = 0; i < p1.length; i++) {
			if (p1[i] != p2[i])
				p1[i] = highlight;
			//else
			//cnt++;
		}

		final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		out.setRGB(0, 0, w, h, p1, 0, w);

		//if(cnt == (h * w))
		//	return out;
		return  out;
		//return (cnt == (h * w)) ? true : false;
		}
	}
	
}
