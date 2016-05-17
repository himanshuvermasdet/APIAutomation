package com.proptiger.qa.apibase;

/**
 * User: Himanshu.Verma
 */

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import com.proptiger.qa.listener.TestListener;
import com.proptiger.qa.util.DBConnectionUtil;
import com.proptiger.qa.util.DataProviderUtil;
import com.proptiger.qa.util.EmailUtil;
import com.proptiger.qa.util.LoggerUtil;
import com.proptiger.qa.util.PropertiesUtil;
import com.proptiger.qa.util.ReportUtil;
import com.proptiger.qa.util.LoggerUtil.LogLevel;

public class APITestBase implements ITest{

	protected String env = null;
	protected String verticalForAps = null;
	public static String emailForReporting = null;
	public static String groups = null;
    protected CloseableHttpClient httpClient;
    String testName = null;


	//list to upload files
	public static ArrayList<Object> uploadList= new ArrayList<Object>(); 
	public static SoftAssert softAssert = new SoftAssert();


	@BeforeSuite(alwaysRun = true)
	public void setUp() throws Exception {
//		apiHelper = new ApiHelper();
		System.out.println("Preparing to Load Constant file..");
		PropertiesUtil.loadConstantFile("Constant.cfg");
		groups=PropertiesUtil.getConstantProperty("RunningGroup");

	}

	@BeforeTest(alwaysRun = true)
	@Parameters({"environment"})
	public void setUpEnvironment(@Optional("Beta")String environment) throws Exception {
		env = environment;
		String fileName = environment+".cfg";
		System.out.println("Preparing to Load " + fileName + " file..");
		PropertiesUtil.loadEnvConfigFile(fileName);
		
		// Connecting DB
		DBConnectionUtil.createDBConnection();
		LoggerUtil.setlog(LogLevel.ONLYLOGS, "DB Connection established successfully");

		
	}

	@BeforeClass
	@Parameters({"os","vertical","email","group"})
	public void beforeEveryClass(@Optional("mac")String os,@Optional("MIDL")String vertical,@Optional("himanshu.verma@proptiger.com")String email,@Optional("all")String group) throws Exception{

		verticalForAps = vertical;
		if(emailForReporting == null)
			emailForReporting = email;
		if(groups == null)
			groups = group;
		
		// PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	      //  cm.setMaxTotal(100);

	     //   httpClient = HttpClients.custom() .setConnectionManager(cm).build();
		//httpClient = HttpClients.createDefault();

	}

	
	@BeforeMethod
	public void beforeEveryMethod(Object testdata[]) throws SQLException{ 
		if (testdata != null)
			this.testName = (String) testdata[0];
		
		//Deleting previous Data if ANY
		LinkedHashMap<String, ArrayList<Object>> deleteImageHash = new LinkedHashMap<String, ArrayList<Object>>();
		//HashValues of all the images used
		ArrayList<Object> deleteImageHashValue = new ArrayList<Object>();
		deleteImageHashValue.add("27bef1edbb9895c24695388247f44b11");
		deleteImageHashValue.add("9a159672f4f78a74bb62f149ff66bfed");
		deleteImageHashValue.add("850783e70df987a856b7b38b8dcda0e2");
		deleteImageHash.put("original_hash", deleteImageHashValue);
		DBConnectionUtil.deleteQuerywithFilters("proptiger", "Image", deleteImageHash);
	}

	@AfterClass
	public void afterEveryClass() {
		// clear cache prepared from test data sheet
		DataProviderUtil.clearTestData();
	}

	@AfterTest
	public void afterEachTestCycle() {
		
		//closing DB connection
		DBConnectionUtil.closeDBConnection();
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {

		File tempFile=null;
		try {
			if(httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			tempFile = ReportUtil.extractResultTable(env,TestListener.fileName);
			ArrayList<HashMap <String, HashMap <String, Integer>>> testReportCompleteName = TestListener.reportStatus;
			String reportName = "";
			
			String emailSubject = null;
			//if(testReportCompleteName.size() > 1)
				emailSubject = PropertiesUtil.getConstantProperty("EmailReportSubject");
			//else
				//emailSubject = groups.equalsIgnoreCase("smoke") ? "Smoke" +TestListener.subject.substring(10) : TestListener.subject;
			
			File f = null;
			for (int i = 0; i < testReportCompleteName.size(); i++) {
				for (String reportNameKey : testReportCompleteName.get(i).keySet()){
					reportName = reportNameKey;
					f = new File(reportName);
					if (!f.exists()){
						System.out.println("REPORT FILE NOT CREATED" +reportName);
					}
				}
			}
			
//			File f = new File(testReportCompleteName);
			
			if(f.exists()){
				EmailUtil.sendEmail(testReportCompleteName,
						emailForReporting+ ","+ PropertiesUtil.getConstantProperty("EmailReciever"),
						PropertiesUtil.getConstantProperty("EmailSenderGmail"),
						PropertiesUtil.getConstantProperty("EmailPasswordGmail"),
						emailSubject,
						PropertiesUtil.getConstantProperty("EmailReportBody"),tempFile);
			}else{
				System.out.println("REPORT FILE NOT CREATED");
			}
			tempFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			tempFile.delete();
			
		}

	}

	@Override
	public String getTestName() {
		
		return this.testName;
	}
}
