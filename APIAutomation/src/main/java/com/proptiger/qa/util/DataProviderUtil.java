package com.proptiger.qa.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.qa.api.dto.RequestDTO;
import com.proptiger.qa.apibase.APITestBase;

/**
 * User: Himanshu.Verma
 */

public class DataProviderUtil {

	public enum mapKeys {
		dpName,reqDTO,groupName,preReqSheet,preReqId,testSpecificData1,testSpecificData2,testSpecificData3,testSpecificData4,testSpecificData5,testSpecificData6
	};
	
	private static Map<String, String[][]> dataProvider = new HashMap<String, String[][]>();
	
	public static Object [][] provideDataMap(String fileName, String sheetName, String dataProviderName, boolean useRunnableTag){
		
		Object [][] tabArray = provideDataMap(fileName,sheetName,dataProviderName);
		
		if(useRunnableTag && tabArray != null)
			tabArray = extractDataForRunnableTrue(tabArray);
		return tabArray;
	}


		public static Object [][] provideDataMap(String fileName, String sheetName, String dataProviderName){
			Object [][] tabArray = null;

			try {
				tabArray = ExcelUtil.readExcelDatatoMap(fileName, sheetName);
				
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			tabArray = extractDataForDataProvider(tabArray, dataProviderName);
			return tabArray;
		}
		
		@SuppressWarnings("unchecked")
		public static Object [][] extractDataForRunnableTrue(Object [][] tabArray){
		
			ArrayList<Object[]> retList = new ArrayList<Object[]>();
			
			for(int i=0;i<tabArray.length;i++){
				
				if(((HashMap<String, String>) tabArray[i][0]).containsKey("Runnable")){
					String isRunnable = ((HashMap<String, String>) tabArray[i][0]).get("Runnable");
					//System.out.println("isRunnable -"+isRunnable);
					if(isRunnable != null && isRunnable.equalsIgnoreCase("yes") || isRunnable.equalsIgnoreCase("true") ||isRunnable.equalsIgnoreCase("1")){
						retList.add(tabArray[i]);
					}
				}
			}	
			
			if (retList.size() ==0){
				System.out.println("No TestCase found in sheet with runnable=yes");
				return null;
			}
				
			Object [][] retArr = new Object[retList.size()][1];
			
			for(int j=0;j<retList.size();j++){
				retArr[j] = (Object[]) retList.get(j);
			}
			
			return retArr;
		}
		
	
		//For below function the 0th element of the array must contain a map - (this map is the zipped row as row from some data source
		// Contained map must contain the 'DataProviderID' as the key for dataProvider value
		@SuppressWarnings("unchecked")
		private static Object[][]  extractDataForDataProvider(Object [][] tabArray, String dataProviderName){

			ArrayList<Object []> rowsToKeep = new ArrayList<Object[]>();
			String[] group = APITestBase.groups.split(",");
			for( int i =0; i<tabArray.length; i++){
				
				if (APITestBase.groups.equalsIgnoreCase("all")){
					if (((HashMap<String, String>)tabArray[i][0]).get("DataProviderID") != null && 
							((HashMap<String, String>)tabArray[i][0]).get("DataProviderID").equals(dataProviderName)){
						rowsToKeep.add(tabArray[i]);
					}
				}else{
					if ( ((HashMap<String, String>)tabArray[i][0]).get("DataProviderID") != null && 
							((HashMap<String, String>)tabArray[i][0]).get("DataProviderID").equals(dataProviderName) ){
						for (int j = 0; j < group.length; j++) {
							if (dataProviderName.startsWith("preReq")){
								rowsToKeep.add(tabArray[i]);
							}else{
								if ( ((HashMap<String, String>)tabArray[i][0]).get("Group") != null && ((HashMap<String, String>)tabArray[i][0]).get("Group").equalsIgnoreCase(group[j]) ){
									rowsToKeep.add(tabArray[i]);
								}
							}
						}
					}
				}
			}
				
			int len = rowsToKeep.size();
			if(len>0){
				Object [][] retArray = new Object[len][];
				for(int i=0; i < len; i++){
					retArray[i] = rowsToKeep.get(i);
				}
				return retArray;
			}
			else return null;
			
		}
		
	public static synchronized void clearTestData(){
		dataProvider.clear();
	}

	@SuppressWarnings("unchecked")
	public static Object[][] sheetMapToDPMap(Object [][] sheetArray) throws Exception{

		Object[][] newSheetArray = new Object[sheetArray.length][2];
		String testName = null;

		try {
			for(int i=0;i<sheetArray.length;i++){

				RequestDTO requestDTO = new RequestDTO();
				Map<String, Object> reqDtoMap = new HashMap<String, Object>();
				
				HashMap<String, String> inputMap = new HashMap(((HashMap<String, String>)sheetArray[i][0]));
				testName = inputMap.remove("TestName");
				reqDtoMap.put(mapKeys.dpName.toString(), inputMap.remove("DataProviderID"));
				reqDtoMap.put(mapKeys.groupName.toString(), inputMap.remove("Group"));
				reqDtoMap.put(mapKeys.preReqSheet.toString(), inputMap.remove("preRequisiteSheet"));
				reqDtoMap.put(mapKeys.preReqId.toString(), inputMap.remove("preRequisiteIdentifier"));
				reqDtoMap.put(mapKeys.testSpecificData1.toString(), inputMap.remove("TestData1")); //TestSpecific Data1
				reqDtoMap.put(mapKeys.testSpecificData2.toString(), inputMap.remove("TestData2"));
				reqDtoMap.put(mapKeys.testSpecificData3.toString(), inputMap.remove("TestData3"));
				reqDtoMap.put(mapKeys.testSpecificData4.toString(), inputMap.remove("TestData4"));
				reqDtoMap.put(mapKeys.testSpecificData5.toString(), inputMap.remove("TestData5"));
				reqDtoMap.put(mapKeys.testSpecificData6.toString(), inputMap.remove("TestData6"));
				reqDtoMap.put(mapKeys.reqDTO.toString(),requestDTO);
				inputMap.remove("Runnable");
				
				requestDTO.setApiPath(inputMap.remove("apipath"));
				requestDTO.setMethodType(inputMap.remove("method"));
				requestDTO.setHeaderJson(inputMap.remove("headers"));
				requestDTO.setQueryParamJson(inputMap.remove("query params"));
				requestDTO.setPathParamJson(inputMap.remove("path params"));
				requestDTO.setRequestBody(inputMap.remove("request body"));
				requestDTO.setResponseCode(Integer.parseInt(inputMap.remove("status")));
				requestDTO.setExpectedResponseJson(inputMap.remove("expected response"));
				requestDTO.setExpectedDB(inputMap.remove("expectedDB"));
				
				newSheetArray[i][0]=testName;
				newSheetArray[i][1]=reqDtoMap;
				
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw e;
		}
		return newSheetArray;

	}
}
