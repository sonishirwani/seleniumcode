	package com.eqtechnologic.testng.custom;

	import org.testng.*;
	import org.testng.collections.Lists;
	import org.testng.log4testng.Logger;
	import org.testng.xml.XmlSuite;
	import java.io.*;
	import java.text.DecimalFormat;
	import java.text.NumberFormat;
	import java.text.SimpleDateFormat;
	import java.util.*;

public class CustomGenerateReport  implements IReporter{
	


	    private static final Logger L = Logger.getLogger(CustomGenerateReport.class);

	    // ~ Instance fields ------------------------------------------------------

	    private PrintWriter out;
	    private int row;
	    private Integer testIndex;
	    private int methodIndex;
	    private Scanner scanner;

	    // ~ Methods --------------------------------------------------------------

	    /** Creates summary of the run */
	    @Override
	    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
	                               String outdir) {
	        try {
	            out = createWriter(outdir);
	        } catch (IOException e) {
	            L.error("output file", e);
	            return;
	        }

	        startHtml(out);
	        generateSuiteSummaryReport(suites);
	        endHtml(out);
	        out.flush();
	        out.close();
	    }

	    protected PrintWriter createWriter(String outdir) throws IOException {

	        new File(outdir).mkdirs();
	        return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, "Customized-emailable-report.html"))));

	    }

    private String millisToTimeConversion(long seconds) {

	        final int MINUTES_IN_AN_HOUR = 60;
	        final int SECONDS_IN_A_MINUTE = 60;

	        int minutes = (int) (seconds / SECONDS_IN_A_MINUTE);
	        seconds -= minutes * SECONDS_IN_A_MINUTE;

	        int hours = minutes / MINUTES_IN_AN_HOUR;
	        minutes -= hours * MINUTES_IN_AN_HOUR;

	        return prefixZeroToDigit(hours) + ":" + prefixZeroToDigit(minutes) + ":" + prefixZeroToDigit((int)seconds);
	    }

	    private String prefixZeroToDigit(int num){
	        int number=num;
	        if(number<=9){
	            String sNumber="0"+number;
	            return sNumber;
	        }
	        else
	            return ""+number;

	    }


	    /**
	     * Since the methods will be sorted chronologically, we want to return the
	     * ITestNGMethod from the invoked methods.
	     */
	    private Collection<ITestNGMethod> getMethodSet(IResultMap tests,
	                                                   ISuite suite) {
	        List<IInvokedMethod> r = Lists.newArrayList();
	        List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();
	        for (IInvokedMethod im : invokedMethods) {
	            if (tests.getAllMethods().contains(im.getTestMethod())) {
	                r.add(im);
	            }
	        }
	        Collections.sort(r,new TestSorter());
	        List<ITestNGMethod> result = Lists.newArrayList();
	        for (IInvokedMethod m : r) {
	            for(ITestNGMethod temp:result){
	                if(!temp.equals(m.getTestMethod()))
	                    result.add(m.getTestMethod());
	            }
	        }


	        Collection<ITestNGMethod> allMethodsCollection=tests.getAllMethods();
	        List<ITestNGMethod> allMethods=new ArrayList<ITestNGMethod>(allMethodsCollection);
		        Collections.sort(allMethods, new TestMethodSorter());
	           //for (ITestNGMethod m : tests.getAllMethods()) {
	        for (ITestNGMethod m : allMethods) {
	            //System.out.println("tests.getAllMethods()  .."+m);
	            if (!result.contains(m)) {
	                result.add(m);
	            }
	        }
	
	        return result;
	    }

	    @SuppressWarnings("unused")
	    public void generateSuiteSummaryReport(List<ISuite> suites) {
	        tableStart("testOverview", null);
	        out.print("<tr>");
	        tableColumnStart("Suite Name");
	        tableColumnStart("#No of Test Cases");
	        tableColumnStart("#Passed");
	        tableColumnStart("#Skipped");
	        tableColumnStart("#Failed");
	        tableColumnStart("Total Time(hh:mm:ss)");
	        tableColumnStart("Start Time");
	        tableColumnStart("End Time");
	     
	

	        out.println("</tr>");
	        NumberFormat formatter = new DecimalFormat("#,##0.0");
	        int qty_tests = 0;
	        int qty_pass_m = 0;
	        int qty_pass_s = 0;
	        int qty_skip = 0;
	        int qty_fail = 0;
	        int qty_all=0;
	        long time_start = Long.MAX_VALUE;
	        long time_end = Long.MIN_VALUE;
	        testIndex = 1;
	        for (ISuite suite : suites) {
	            if (suites.size() >= 1) {
	                titleRow(suite.getName(), 9);
	            }
	            Map<String, ISuiteResult> tests = suite.getResults();
	            for (ISuiteResult r : tests.values()) {
	            	
	                qty_tests += 1;
	                ITestContext overview = r.getTestContext();
	                startSummaryRow(overview.getName());
	                
	                int qty_total= overview.getPassedTests().size()+overview.getSkippedTests().size()+ overview.getFailedTests().size();
	                
	               
	          
	                summaryCell(qty_total, Integer.MAX_VALUE);
	                qty_all+=qty_total;
	            
	                int q = overview.getPassedTests().size();
	                qty_pass_m += q;
	                summaryCell(q, Integer.MAX_VALUE);
	         
		             q = overview.getSkippedTests().size();
		        
	                qty_skip += q;
	                summaryCell(q, 0);
	                q = overview.getFailedTests().size();
	        
	                qty_fail += q;
	                summaryCell(q, 0);
	                
	                summaryCell(millisToTimeConversion((overview.getEndDate().getTime() - overview
	                        .getStartDate().getTime()) / 1000), true);

	                SimpleDateFormat summaryFormat = new SimpleDateFormat("kk:mm:ss");
	                summaryCell(summaryFormat.format(overview.getStartDate()),true);
	                out.println("</td>");

	                summaryCell(summaryFormat.format(overview.getEndDate()),true);
	                out.println("</td>");

	                time_start = Math.min(overview.getStartDate().getTime(),
	                        time_start);
	                time_end = Math.max(overview.getEndDate().getTime(), time_end);
          

	                out.println("</tr>");
	                testIndex++;
	            }
	        }
	        if (qty_tests > 1) {
	            out.println("<tr class=\"total\"><td>Total</td>");
	            summaryCell(qty_all, Integer.MAX_VALUE);
	            summaryCell(qty_pass_m, Integer.MAX_VALUE);
	            //summaryCell(qty_pass_s, Integer.MAX_VALUE);
	            summaryCell(qty_skip, 0);
	            summaryCell(qty_fail, 0);
	            //summaryCell(" ", true);
	            summaryCell(millisToTimeConversion(((time_end - time_start) / 1000)), true);
	            summaryCell(" ", true);
	            summaryCell(" ", true);
	 	        }
	        out.println("</table>");
	    }


	    private void summaryCell(String[] val) {
	        StringBuffer b = new StringBuffer();
	        for (String v : val) {
	            b.append(v + " ");
	        }
	        summaryCell(b.toString(), true);
	    }

	    private void summaryCell(String v, boolean isgood) {
	        out.print("<td class=\"numi" + (isgood ? "" : "_attn") + "\">" + v
	                + "</td>");
	    }

	    private void startSummaryRow(String label) {
	        row += 1;
	        out.print("<tr"
	                + (row % 2 == 0 ? " class=\"stripe\"" : "")
	                + "><td style=\"text-align:left;padding-right:2em\"><b>" + label + "</b>" + "</td>");

	    }

	    private void summaryCell(int v, int maxexpected) {
	        summaryCell(String.valueOf(v), v <= maxexpected);
	    }

	    private void tableStart(String cssclass, String id) {
	        out.println("<table cellspacing=\"0\" cellpadding=\"0\""
	                + (cssclass != null ? " class=\"" + cssclass + "\""
	                : " style=\"padding-bottom:2em\"")
	                + (id != null ? " id=\"" + id + "\"" : "") + ">");
	        row = 0;
	    }

	    private void tableColumnStart(String label) {
	        out.print("<th>" + label + "</th>");
	    }

	    private void titleRow(String label, int cq) {
	        titleRow(label, cq, null);
	    }

	    private void titleRow(String label, int cq, String id) {
	        out.print("<tr");
	        if (id != null) {
	            out.print(" id=\"" + id + "\"");
	        }
	        out.println("><th colspan=\"" + cq + "\">" + label + "</th></tr>");
	        row = 0;
	    }

	    /** Starts HTML stream */
	    protected void startHtml(PrintWriter out) {
	        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
	        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	        out.println("<head>");
	        out.println("<title>TestNG Report</title>");
	        out.println("<style type=\"text/css\">");
	        out.println("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
	        out.println("td,th {border:1px solid #009;padding:.25em .5em}");
	        out.println(".result th {vertical-align:bottom}");
	        out.println(".param th {padding-left:1em;padding-right:1em}");
	        out.println(".param td {padding-left:.5em;padding-right:2em}");
	        out.println(".stripe td,.stripe th {background-color: #E6EBF9}");
	        out.println(".numi,.numi_attn {text-align:right}");
	        out.println(".total td {font-weight:bold}");
	        out.println(".stacktrace {white-space:pre;font-family:monospace}");
	        out.println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
	        out.println("</style>");
	        out.println("</head>");
	        out.println("<body>");
	    }

	    /** Finishes HTML stream */
	    protected void endHtml(PrintWriter out) {
	       // out.println("<center> Report customized </center>");
	        out.println("</body></html>");
	    }

	    // ~ Inner Classes --------------------------------------------------------
	    /** Arranges methods by classname and method name */
	    private class TestSorter implements Comparator<IInvokedMethod> {
	        // ~ Methods
	        // -------------------------------------------------------------

	        /** Arranges methods by classname and method name */
	        @Override
	        public int compare(IInvokedMethod o1, IInvokedMethod o2) {
	            int r =o1.getTestMethod().getTestClass().getName().compareTo(o2.getTestMethod().getTestClass().getName());
	 
	            if (r == 0) {
	                r=o1.getTestMethod().compareTo(o2.getTestMethod());

	            }
	            return r;
	        }

	    }

	    private class TestMethodSorter implements Comparator<ITestNGMethod> {
	        @Override
	        public int compare(ITestNGMethod o1, ITestNGMethod o2) {
	            int r =o1.getTestClass().getName().compareTo(o2.getTestClass().getName());
	   
	            if (r == 0) {
	  	                r=o1.getMethodName().compareTo(o2.getMethodName());
	            }
	            return r;
	        }
	    }

	}


