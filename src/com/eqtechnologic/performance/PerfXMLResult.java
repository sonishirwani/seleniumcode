package com.eqtechnologic.performance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.eqtechnologic.framework.TestSuite;

public class PerfXMLResult {
	
	
	
	public void createReport() throws IOException
	{
		if(TestSuite.testMap!=null)
		{
			File f = new File("./testng/test-output/PerformanceResult.xml");
		
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			bw.write("\t <SUITE>\n");
			bw.write("\t <BUILD> Jenkin Build No- "+ System.getProperty("buildNo")+" </BUILD>\n");				
			try {
				

				 for(Map.Entry<String, ArrayList<timeLog>> entry:TestSuite.testMap.entrySet()){    
				        String key=entry.getKey();
				        bw.write("\t \t<TESTCASE name=\""+key+"\">\n");
				        
				        ArrayList<timeLog> al=entry.getValue();  
				        Iterator<timeLog> itr=al.iterator();  
	
				        while(itr.hasNext()){  
				        	timeLog st=(timeLog)itr.next();  
				        	
				            
				            bw.write("\t \t \t<OPERATION name=\""+st.getOperation()+"\">\n");
				            bw.write("\t \t \t<STARTTIME>"+st.getStartTime()+"</STARTTIME>\n");
				            bw.write("\t \t \t<ENDTIME>"+st.getEndTime()+"</ENDTIME>\n");
				            bw.write("\t \t \t<DIFFERENCE>"+st.getDiff()+"</DIFFERENCE>\n");
				            bw.write("\t \t \t</OPERATION>\n");
				            
				          }  
				        bw.write("\t \t</TESTCASE>\n");   
				        
				    }    
				
				
				
				
			} catch (Exception e) {
			e.printStackTrace();
			}
			bw.write("\t </SUITE>");
			bw.close();
		}
	}
	
	public void createHTMLReport()
	{
		try {
	        TransformerFactory tFactory=TransformerFactory.newInstance();
	        File xslfile = new File("./testng/xsltOfPerf.xsl");
	        Source xslDoc=new StreamSource(xslfile);
	        File xmlfile = new File("./testng/test-output/PerformanceResult.xml");
	        Source xmlDoc=new StreamSource(xmlfile);
	        String buildNo=System.getProperty("buildNo");

	        File outputFileName=new File("./testng/test-output/PerformanceJenkinBuild"+buildNo+".html");

	        OutputStream htmlFile=new FileOutputStream(outputFileName);
	        Transformer trasform=tFactory.newTransformer(xslDoc);
	        trasform.transform(xmlDoc, new StreamResult(htmlFile));
	        
	    } 
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }
	    catch (TransformerConfigurationException e) 
	    {
	        e.printStackTrace();
	    }
	    catch (TransformerFactoryConfigurationError e) 
	    {
	        e.printStackTrace();
	    }
	    catch (TransformerException e) 
	    {
	        e.printStackTrace();
	    }
	     
	}
}
