package com.eqtechnologic.util;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class CallAntTask {
	public  static boolean executeAntTask(String buildXmlFileFullPath, String target)
	{
		  boolean success = false;
	        DefaultLogger consoleLogger = getConsoleLogger();
	       // Prepare Ant project
		 Project project = new Project();
	        File buildFile = new File(buildXmlFileFullPath);
	        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
	        project.addBuildListener(consoleLogger);
	        
	        // Capture event for Ant script build start / stop / failure
	        try {
	            project.fireBuildStarted();
	            project.init();
	            ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
	            project.addReference("ant.projectHelper", projectHelper);
	            projectHelper.parse(project, buildFile);
		          
	   
	            // If no target specified then default target will be executed.
	            String targetToExecute = (target != null && target.trim().length() > 0) ? target.trim() : project.getDefaultTarget();
	            project.executeTarget(targetToExecute);
	            project.fireBuildFinished(null);
	            success = true;
	        } catch (BuildException buildException) {
	            project.fireBuildFinished(buildException);
	            throw new RuntimeException("!!! Unable to restart the IEHS App !!!", buildException);
	        }
    	return success;
	}
	 public  static DefaultLogger getConsoleLogger() {
	        DefaultLogger consoleLogger = new DefaultLogger();
	        consoleLogger.setErrorPrintStream(System.err);
	        consoleLogger.setOutputPrintStream(System.out);
	        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
	         
	        return consoleLogger;
	    }
}
