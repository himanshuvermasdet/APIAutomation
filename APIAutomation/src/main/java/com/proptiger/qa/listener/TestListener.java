package com.proptiger.qa.listener;

/**
 * User: Himanshu.Verma
 */

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.proptiger.qa.util.DateUtil;
import com.proptiger.qa.util.ExtentManager;
import com.proptiger.qa.util.PropertiesUtil;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.model.Test;

public class TestListener implements ITestListener,ISuiteListener,IInvokedMethodListener {

	// public static String screen = null;
	private static String exception ;
	private ExtentReports extentReport = null;
	//	private static ExtentTest test = null;

	public static ArrayList<String> testReportCompleteName = null;
	public static ArrayList<HashMap <String, HashMap <String, Integer>>> reportStatus = new ArrayList<HashMap <String, HashMap <String, Integer>>>();
	
	HashMap<String, Integer> status = new HashMap<String, Integer>();
	
	String testReportName = "";
	
	public static Test testName = null;
	public static String fileName = null;
	public static String startTime = null;
	public static String endTime = null;
	
	public static String subject = null;
	
	private static ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<ExtentTest>();

	private String testNameFromXml = null;
	
	public void onStart(ITestContext context) {

		try {
			File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + PropertiesUtil.getConstantProperty("ReportPath"));

			if (!file.exists()){
				file.mkdir();
			}
			
			testReportCompleteName = new ArrayList<String>();
			
			status.put("pass",0);
			status.put("fail",0);
			status.put("skip",0);
			
			DateFormat df = new SimpleDateFormat("dd_MM_yy_HHmmss");
			Date dateobj = new Date();
			String reportName =context.getName()+"_" + df.format(dateobj) ;
			fileName = context.getSuite().getName()+ System.getProperty("file.separator")+context.getName();
			
			subject = context.getSuite().getName()+ " Report for "+context.getName();
			
			String currentReport = reportName + ".html";
			testReportName = System.getProperty("user.dir") + System.getProperty("file.separator") + PropertiesUtil.getConstantProperty("ReportPath") +  System.getProperty("file.separator") + currentReport;
			testReportCompleteName.add(testReportName); 

			System.out.println("testReportCompleteName="+ testReportCompleteName);

			extentReport = ExtentManager.getInstance(testReportCompleteName.get(testReportCompleteName.size() - 1));

			extentReport.config().reportName("Report :: ");
			extentReport.config().reportHeadline(PropertiesUtil.getConstantProperty("ReportHeadline"));
			
			testNameFromXml = context.getName();
			//System.out.println("testNameFromXml "+testNameFromXml); 
			extentTestThreadLocal.set(extentReport.startTest("Pre Condition ", "Test Run on " + testNameFromXml));
			extentTestThreadLocal.get().log(LogStatus.INFO, " INFO");
			testName = extentTestThreadLocal.get().getTest();
//			suiteName = context.getSuite().getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onTestStart(ITestResult result) {
		try {
			System.out.println("Started Test: " + result.getName());
			//		test = extentReport.startTest(result.getName(), "Test Run on " + testNameFromXml);
			if (testName == extentTestThreadLocal.get().getTest()){
				extentReport.endTest(extentTestThreadLocal.get());
			}
			extentTestThreadLocal.set(extentReport.startTest(result.getName(), "Test Run on " + testNameFromXml));
			extentTestThreadLocal.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onTestSuccess(ITestResult result) {
		System.out.println("Finished Test: " + result.getName() + " :PASSED");
		extentTestThreadLocal.get().log(LogStatus.PASS, result.getName() + " Passed");
		extentReport.endTest(extentTestThreadLocal.get());
		status.put("pass", status.get("pass")+1);
	}

	public void onTestFailure(ITestResult result) {
		System.out.println("Finished Test: " + result.getName() + " :FAILED");
		if (exception != "")
			extentTestThreadLocal.get().log(LogStatus.FAIL, result.getName() + "<pre><br>" + exception + "</br></pre>" + "Failed");
		else
			extentTestThreadLocal.get().log(LogStatus.FAIL, result.getName() + " :: Failed");
		// test.addScreenCapture(screen);
		extentReport.endTest(extentTestThreadLocal.get());
		status.put("fail", status.get("fail")+1);
	}

	public void onTestSkipped(ITestResult result) {
		System.out.println("Finished Test: " + result.getName() + " :SKIPPED");
		extentTestThreadLocal.get().log(LogStatus.SKIP, result.getName() + " Skipped");
		extentReport.endTest(extentTestThreadLocal.get());
		status.put("skip", status.get("skip")+1);
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println("Finished Test: " + result.getName() + " :FAILED BUT WITHIN SUCCESS PERCENTAGE");
		extentTestThreadLocal.get().log(LogStatus.WARNING, result.getName() + " Failed");
		extentReport.endTest(extentTestThreadLocal.get());
	}

	public void onFinish(ITestContext context) {
		
		if (extentReport != null){
			HashMap<String, HashMap <String, Integer>> reportData = new HashMap<>();
			HashMap<String, Integer> temp = new HashMap<>(status);
			reportData.put(testReportName, temp);
			
			reportStatus.add(reportData);
			
			extentReport.flush();
		}
	}

	public static String setException(String message, StackTraceElement[] stackTrace) {

		for(StackTraceElement stackTraceElement : stackTrace) {                         
			message = message + System.lineSeparator() + stackTraceElement;
		}
		exception = message;
		return exception;
	}

	public static void clearAllException() {

		exception = "";
	}
	
	public static void setMessage(String message) {

		exception = message;
	}
	
	public static void setLog(LogStatus type, String logMessage){
		extentTestThreadLocal.get().log(type, logMessage);
	}

	public static void setLogsForTestData(String scenario, Map<String, String> inputMap, String expected ){
		try{
			inputMap.remove("testdata");
		}catch(Exception ex){

		}
		//setLog(LogStatus.INFO, "TEST SCENARIO - " + scenario);
		setLog(LogStatus.INFO, "TEST DATA - " + inputMap);
		//setLog(LogStatus.INFO, "EXPECTATION - " + expected);
	}

	@Override
	public void onStart(ISuite suite) {
		startTime = DateUtil.getCurrentDateAndTime();
	}

	@Override
	public void onFinish(ISuite suite) {
		endTime = DateUtil.getCurrentDateAndTime();
	}

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		endTime = DateUtil.getCurrentDateAndTime();
	}


}
