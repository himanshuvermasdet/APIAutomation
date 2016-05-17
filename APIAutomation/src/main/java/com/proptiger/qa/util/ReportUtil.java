package com.proptiger.qa.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.proptiger.qa.listener.TestListener;

/**
 * User: Himanshu.Verma
 */

public class ReportUtil {

	public static File extractResultTable(String environment,String fileName) {

		File file= null;
		try {
			file =new File(System.getProperty("user.dir")+System.getProperty("file.separator")+PropertiesUtil.getConstantProperty("TempFile"));
			if(!file.exists())
				file.createNewFile();
			
			ArrayList<HashMap <String, HashMap <String, Integer>>> status = TestListener.reportStatus;
			
			int pass = 0;
			int fail = 0;
			int skip = 0;
			
			HashMap<String, String> finalReportStatus = new HashMap<String, String>();
			
			for (int i = 0; i < status.size(); i++) {
				
				StringBuffer failSkipCount = new StringBuffer();
				
				for (String reportNameKey : status.get(i).keySet()){
					String reportName = reportNameKey;
					reportName = reportName.substring(reportName.lastIndexOf("/") + 1);
					
					for (String statusKey : status.get(i).get(reportNameKey).keySet()){
						
						if (statusKey.equalsIgnoreCase("pass")){
							if (status.get(i).get(reportNameKey).get(statusKey) > 0){
								pass = pass + status.get(i).get(reportNameKey).get(statusKey);
							}
						}
						else if (statusKey.equalsIgnoreCase("fail")){
							if (status.get(i).get(reportNameKey).get(statusKey) > 0){
								fail = fail + status.get(i).get(reportNameKey).get(statusKey);
								failSkipCount.append(status.get(i).get(reportNameKey).get(statusKey)).append("/");
								finalReportStatus.put(reportName, failSkipCount.toString());
							}
						}
						else if(statusKey.equalsIgnoreCase("skip")){
							if (status.get(i).get(reportNameKey).get(statusKey) > 0){
								skip = skip + status.get(i).get(reportNameKey).get(statusKey);
								failSkipCount.append(status.get(i).get(reportNameKey).get(statusKey)).append("/");
								finalReportStatus.put(reportName, failSkipCount.toString());
							}
						}
					}
				}
			}
			
			Document doc = Jsoup.parse("<table border=\"1\" align=\"center\"><tbody></tbody></table>", "UTF-8");
			Element table = doc.select("table").first();
			int totalTestRun = pass + fail + skip;
			
			
//			for (int i = 0; i <= 3; i++) {
//				Element tableRow = doc.select("table tr:eq(1)").first();
//			    tableRow.remove();
//			}
//			
//			Element tableRow = doc.select("table tr:eq(0)").first();
//		    tableRow.remove();
			
		    if (!finalReportStatus.isEmpty()){
		    	table.append("<tr><td>Failed TestCases in the File</td><td></td></tr>");
		    	for (String key : finalReportStatus.keySet()){
		    		table.append("<tr style='color:Red;font-weight:bold;'><td>"+key+" :: Failed/Skiped Count</td><td>"+finalReportStatus.get(key).substring(0, finalReportStatus.get(key).length() - 1)+"</td></tr>");
		    	}
		    }
		    
		    table.prepend("<tr><td>Finished on:</td><td>"+TestListener.endTime+"</td></tr>");
		    table.prepend("<tr><td>Started on:</td><td>"+TestListener.startTime+"</td></tr>");
		    table.prepend("<tr><td>Tests passed/Failed/Skipped:</td><td>"+pass+"/"+"<font color='Red'>"+fail+"</font>"+"/"+skip+"</td></tr>");
			table.prepend("<tr><td>Total Tests Run</td><td>"+totalTestRun+"</td></tr>");	
			table.prepend("<tr><td>Environment</td><td>"+environment+"</td></tr>");

			String html = table.outerHtml();
			FileWriter writer = new FileWriter(file, true);
			BufferedWriter buf = new BufferedWriter(writer);
			buf.write(html);
			buf.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
}
