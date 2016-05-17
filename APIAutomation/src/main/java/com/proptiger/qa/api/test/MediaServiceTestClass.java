package com.proptiger.qa.api.test;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.proptiger.qa.util.DBConnectionUtil;
import com.proptiger.qa.util.DataProviderUtil;
import com.proptiger.qa.util.JavaUtil;
import com.proptiger.qa.util.JsonUtil;
import com.proptiger.qa.util.PropertiesUtil;
import com.proptiger.qa.util.RequestUtil;
import com.proptiger.qa.util.DataProviderUtil.mapKeys;
import com.proptiger.qa.api.dto.RequestDTO;
import com.proptiger.qa.apihelper.ApiHelper;
import com.proptiger.qa.apihelper.MediaServiceHelper;
import com.proptiger.qa.util.LoggerUtil;
import com.proptiger.qa.util.LoggerUtil.LogLevel;
import com.proptiger.qa.apibase.APITestBase;
import com.proptiger.qa.listener.TestListener;

@Listeners(TestListener.class)
public class MediaServiceTestClass extends APITestBase {
	ApiHelper apihelper = null;
	HttpResponse httpResponse = null;
	RequestDTO requestDTO = null;
	static String baseUrl = null;
	String apiResponse = null;
	public static int domainId = 2;
	public static String absolutePath = "https://im.proptiger-ws.com/";
	boolean deleteFlag = true;
	LinkedHashMap<String, ArrayList<Object>> imageIdMap = null;
	ArrayList<Object> imageIdList = new ArrayList<Object>();
	int responseCode = 200;
	int failureCount = 0;

	@BeforeClass
	public void initializeClassCustomerLoganBFF() throws NumberFormatException, Exception {
		LoggerUtil.setlog(LogLevel.ONLYLOGS, "Initializing class variables for Media Services MIDL");
		apihelper = new ApiHelper();
		baseUrl = PropertiesUtil.getEnvConfigProperty("MediaServiceUrl");
	}

	@BeforeTest
	public void createIDList() {
		imageIdMap = new LinkedHashMap<String, ArrayList<Object>>();
	}

	@BeforeMethod
	public void beforeMethod() {
		failureCount = 0;
	}

	@Test(dataProvider = "v1postImage", priority = 1, enabled = true)
	public void postImage(String testName, HashMap<String, Object> requestDtoMap) throws Exception {

		String objectType = null, imageType = null, sourceDomain = "Proptiger", URL = null;
		long objectId = 0;
		String preReqobjectType = null, preReqimageType = null, preReqsourceDomain = "Proptiger";
		long preReqobjectId = 0;
		int count = 0;
		ArrayList<String> objectTypes = new ArrayList<String>();
		LinkedHashMap<String, ArrayList<String>> imageTypeIds = new LinkedHashMap<String, ArrayList<String>>();
		LinkedHashMap<String, ArrayList<String>> preReqimageTypeIds = new LinkedHashMap<String, ArrayList<String>>();
		ArrayList<Object> objectIds = new ArrayList<Object>();

		requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
		String origRequestBody = requestDTO.getRequestBody();
		String origResponseBody = requestDTO.getExpectedResponseJson();
		String testSpecificData1 = (String) requestDtoMap.get(mapKeys.testSpecificData1.toString());
		String testSpecificData2 = (String) requestDtoMap.get(mapKeys.testSpecificData2.toString());
		String testSpecificData3 = (String) requestDtoMap.get(mapKeys.testSpecificData3.toString());
		String testSpecificData4 = (String) requestDtoMap.get(mapKeys.testSpecificData4.toString());
		String testSpecificData5 = (String) requestDtoMap.get(mapKeys.testSpecificData5.toString());
		String testSpecificData6 = (String) requestDtoMap.get(mapKeys.testSpecificData6.toString());
		String preRequisiteSheet = (String) requestDtoMap.get(mapKeys.preReqSheet.toString());
		String preRequisiteIdentifier = (String) requestDtoMap.get(mapKeys.preReqId.toString());

		HashMap<Object, Object> testSpecificDataMap = new HashMap<Object, Object>();

		URL = "http://cdn.home-designing.com/wp-content/uploads/2014/10/simple-luxury-bedroom-design.jpeg";
		File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator")
				+ PropertiesUtil.getConstantProperty("TempImage"));
		testSpecificDataMap.put("URL", URL);
		testSpecificDataMap.put("image", file);

		LinkedHashMap<String, String> replaceRequestBody = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> replacePreRequisiteRequestBody = new LinkedHashMap<String, String>();

		// change domain
		if (testSpecificData1 != null
				&& (testSpecificData1.equalsIgnoreCase("yes") || testSpecificData1.equalsIgnoreCase("true"))) {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Makaan");
			domainId = 1;
			sourceDomain = "Makaan";
			absolutePath = "http://content.makaan-ws.com/";
			testSpecificDataMap.put("image", file);

		}
		else{
			domainId = 2;
			absolutePath = "https://im.proptiger-ws.com/";
		}
		testSpecificDataMap.put("sourceDomain", sourceDomain);
		replaceRequestBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());
		replacePreRequisiteRequestBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());

		// change source as SQL or limited values
		if (testSpecificData2 != null
				&& (testSpecificData2.equalsIgnoreCase("yes") || testSpecificData2.equalsIgnoreCase("true"))) {

			// objectTypes.add("state");
			// objectTypes.add("city");
			LoggerUtil.setlog(LogLevel.INFO, "Running for Multiple ObjectTypes and ImageType");
			objectTypes.add("project");
			// objectTypes.add("project");
			for (String objectType1 : objectTypes) {
				imageTypeIds = MediaServiceHelper.getImageIDsSQL(objectType1, domainId, imageTypeIds);
			}
			// System.out.println("imageTypeIds "+imageTypeIds.get("city"));

		} else {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Single ObjectType and ImageType");
			objectType = "project";
			imageType = "clusterPlan";
			objectId = 111222;
			preReqobjectType = "project";
			preReqimageType = "clusterPlan";
			preReqobjectId = 111222;
			// origRequestBody for __pattern__
			ArrayList<String> imageTypeId = new ArrayList<String>();
			imageTypeId.add("2");
			imageTypeIds.put(objectType, imageTypeId);
		}

		// change upload to URL/Image
		if (testSpecificData3 != null
				&& (testSpecificData3.equalsIgnoreCase("yes") || testSpecificData3.equalsIgnoreCase("true"))) {
			replaceRequestBody.put("image", testSpecificDataMap.get("image").toString());
			replacePreRequisiteRequestBody.put("image", testSpecificDataMap.get("image").toString());
		} else {
			replaceRequestBody.put("URL", testSpecificDataMap.get("URL").toString());
			replacePreRequisiteRequestBody.put("URL", testSpecificDataMap.get("URL").toString());
		}

		// User HardCoded Data
		if (testSpecificData4 != null
				&& (testSpecificData4.equalsIgnoreCase("yes") || testSpecificData4.equalsIgnoreCase("true"))) {
			deleteFlag = false;
		}

		// Low Aspect Ratio Image
		if (testSpecificData5 != null
				&& (testSpecificData5.equalsIgnoreCase("yes") || testSpecificData5.equalsIgnoreCase("true"))) {
			URL = "http://content.makaan-ws.com/4/0/463/1646616.jpeg";
			objectType = "project";
			ArrayList<String> imageTypeId = new ArrayList<String>();
			// imageType Id for "main" as it is storing aspect ration 1.33
			imageTypeId.add("6");
			imageTypeIds.put(objectType, imageTypeId);
			testSpecificDataMap.put("URL", URL);
			replaceRequestBody.put("URL", testSpecificDataMap.get("URL").toString());
		}

		// Checking Duplicacy Rules
		if (testSpecificData6 != null) {
			if (testSpecificData6.equalsIgnoreCase("ND")) {
				// NO_DUPLICATES ID:1

				preReqobjectType = "project";
				preReqimageType = "clusterPlan";
				preReqobjectId = 501829;
				ArrayList<String> preReqimageTypeId = new ArrayList<String>();
				preReqimageTypeId.add("2");
				preReqimageTypeIds.put(preReqobjectType, preReqimageTypeId);

				objectType = "landmark";
				ArrayList<String> imageTypeId = new ArrayList<String>();
				// imageType Id for "main" as it is storing aspect ration 1.33
				imageTypeId.add("75");
				imageTypeIds.put(objectType, imageTypeId);

			}
			if (testSpecificData6.equalsIgnoreCase("NDI")) {
				// NO_DUPLICATES_WITH_SAME_IMAGE_TYPE:2

				preReqobjectType = "city";
				preReqimageType = "clusterPlan";
				preReqobjectId = 501829;
				ArrayList<String> preReqimageTypeId = new ArrayList<String>();
				preReqimageTypeId.add("2");
				preReqimageTypeIds.put(preReqobjectType, preReqimageTypeId);

				objectType = "landmark";
				ArrayList<String> imageTypeId = new ArrayList<String>();
				// imageType Id for "main" as it is storing aspect ration 1.33
				imageTypeId.add("75");
				imageTypeIds.put(objectType, imageTypeId);
			}

		}

		// Running Prerequisite Single ImageType
		if (preRequisiteSheet != null) {
			if (preRequisiteIdentifier != null) {
				/*
				 * String[] preRequisiteSheetValue =
				 * preRequisiteSheet.split(","); String[]
				 * preRequisiteIdentifierValue =
				 * preRequisiteIdentifier.split(",");
				 * 
				 * for (int i = 0; i < preRequisiteIdentifierValue.length; i++)
				 * {
				 * 
				 * Object[][] excelDataForPreReq =
				 * DataProviderUtil.provideDataMap(PropertiesUtil.
				 * getConstantProperty("TestData_API_Promo"),
				 * preRequisiteSheetValue[0], preRequisiteIdentifierValue[i]);
				 */
				RequestDTO preRequestDTO = new RequestDTO();
				Object[][] excelDataForPreReq = DataProviderUtil.provideDataMap(
						PropertiesUtil.getConstantProperty("TestData_API_Media"), preRequisiteSheet,
						preRequisiteIdentifier, true);
				preRequestDTO = RequestUtil.createRequestDTO(excelDataForPreReq, preRequestDTO);
				replacePreRequisiteRequestBody.put("objectType", preReqobjectType);
				replacePreRequisiteRequestBody.put("imageType", preReqimageType);
				replacePreRequisiteRequestBody.put("objectId", String.valueOf(preReqobjectId));
				replacePreRequisiteRequestBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());
				// origRequestBody for __pattern__
				String preRequestBody = JavaUtil.replacePreRequisite(replacePreRequisiteRequestBody,
						preRequestDTO.getRequestBody());
				if (preRequestBody != null)
					preRequestDTO.setRequestBody(preRequestBody);

				LinkedHashMap<String, String> preReplaceQueryBody = new LinkedHashMap<String, String>();
				preReplaceQueryBody.put("debug", "true");
				String preQueryBody = JavaUtil.replacePreRequisite(preReplaceQueryBody,
						preRequestDTO.getQueryParamJson());
				if (preQueryBody != null)
					preRequestDTO.setQueryParamJson(preQueryBody);

				httpResponse = apihelper.createRequest(preRequestDTO, baseUrl, httpClient, true);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				if (responseCode == 200) {
					LoggerUtil.setlog(LogLevel.INFO, "PreRequisite Completed Successfully");
				} else {
					LoggerUtil.setlog(LogLevel.INFO, "PreRequisite Not Completed Successfully");
				}
			}
		}
		// Getting objectTypes
		for (String objectType1 : imageTypeIds.keySet()) {
			// System.out.println("objectType1 " + objectType1);
			testSpecificDataMap.put("objectType", objectType1);
			for (String imageID : imageTypeIds.get(objectType1)) {
				// System.out.println("imageID "+i+" : "+imageID);
				objectIds = MediaServiceHelper.getObjectIdsSQL(imageID);
				count = (int) objectIds.get(0);
				if (count != 0) {
					// System.out.println("objectIds.get(1) "
					// +objectIds.get(1));
					testSpecificDataMap.put("objectId", objectIds.get(1));
					String imageTypeName = MediaServiceHelper.getImageTypesSQL(imageID).get("type").toString()
							.replace("[", "").replace("]", "");
					String imageTypeDisplayName = MediaServiceHelper.getImageTypesSQL(imageID).get("display_name")
							.toString().replace("[", "").replace("]", "");
					// System.out.println("imageTypeName "+imageTypeName);
					// System.out.println("imageTypeDisplayName
					// "+imageTypeDisplayName);

					testSpecificDataMap.put("imageType", imageTypeName);
					testSpecificDataMap.put("imageTypeDisplayName", imageTypeDisplayName);

				} else
					continue;

				replaceRequestBody.put("objectType", testSpecificDataMap.get("objectType").toString());
				replaceRequestBody.put("imageType", testSpecificDataMap.get("imageType").toString());
				replaceRequestBody.put("objectId", String.valueOf(testSpecificDataMap.get("objectId")));

				// origRequestBody for __pattern__
				String requestBody = JavaUtil.replacePreRequisite(replaceRequestBody, origRequestBody);
				if (requestBody != null)
					requestDTO.setRequestBody(requestBody);

				// uploading image
				uploadList.add("image");

				LinkedHashMap<String, String> replaceQueryBody = new LinkedHashMap<String, String>();
				replaceQueryBody.put("debug", "true");
				String queryBody = JavaUtil.replacePreRequisite(replaceQueryBody, requestDTO.getQueryParamJson());
				if (queryBody != null)
					requestDTO.setQueryParamJson(queryBody);

				// value to get from API Response
				LinkedHashMap<String, Object> valueToGetFromResponseMap = new LinkedHashMap<String, Object>();
				valueToGetFromResponseMap.put("id", "");

				LoggerUtil.setlog(LogLevel.INFO, "Sending Test Request");
				LinkedHashMap<Object, Object> whereQuery = new LinkedHashMap<Object, Object>();
				try {
					/*
					 * Creates API Request and Validates the API Response Code
					 */
					httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, true);
					responseCode = httpResponse.getStatusLine().getStatusCode();

				} catch (Exception | Error e) {
					TestListener.setException(e.getMessage(), e.getStackTrace());
					e.printStackTrace();
					failureCount++;
					// APITestBase.softAssert.assertTrue(false);
					continue;
				}
				if (responseCode == 200) {
					try {
						apiResponse = apihelper.getResponse(requestDTO, httpResponse);
						System.out.println(apiResponse);
						JsonUtil.returnValueFromResponse(apiResponse, valueToGetFromResponseMap);
						String id = valueToGetFromResponseMap.get("id").toString();
						LoggerUtil.setlog(LogLevel.INFO, "Image Id Created: " + id);
						whereQuery.put("id", id);
						imageIdList.add(id);

						LinkedHashMap<String, String> replaceResponseBody = MediaServiceHelper
								.replaceResponseBody(whereQuery, testSpecificDataMap, "Image");

						// origResponseBody for __pattern__
						String responseBody = JavaUtil.replacePreRequisite(replaceResponseBody, origResponseBody);
						// System.out.println("responseBody "+responseBody);
						if (responseBody != null)
							requestDTO.setExpectedResponseJson(responseBody);

						LoggerUtil.setlog(LogLevel.INFO, "Validating Test Request");

						/* Validates the JSON Nodes */
						apihelper.validateRequest(requestDTO, apiResponse);
					} catch (Exception | Error e) {
						TestListener.setException(e.getMessage(), e.getStackTrace());
						e.printStackTrace();
						failureCount++;
						// APITestBase.softAssert.assertTrue(false);
						continue;
					} finally {
						imageIdMap.put("id", imageIdList);
						// Deleting Image after creating
						if (deleteFlag && responseCode == 200) {
							LoggerUtil.setlog(LogLevel.INFO, "Deleting Posted Images");
							DBConnectionUtil.deleteQuerywithFilters("proptiger", "Image", imageIdMap);
						}
					}
				}

			}
		}

		Assert.assertEquals(failureCount, 0, "One of the API's got failed ");
		// APITestBase.softAssert.assertAll();
	}

	@Test(dataProvider = "v1getImage", priority = 2, enabled = true)
	public void getImage(String testName, HashMap<String, Object> requestDtoMap) throws Exception {

		String objectType = null, imageType = null, sourceDomain = "Proptiger", URL = null;
		long objectId = 0;
		int count = 0;
		ArrayList<String> objectTypes = new ArrayList<String>();
		LinkedHashMap<String, ArrayList<String>> imageTypeIds = new LinkedHashMap<String, ArrayList<String>>();
		ArrayList<Object> objectIds = new ArrayList<Object>();
		boolean preReqFlag = false;
		LinkedHashMap<String, Object> valueToGetFromResponseMap = new LinkedHashMap<String, Object>();

		String imageId = null;

		requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
		String origQueryBody = requestDTO.getQueryParamJson();
		String origResponseBody = requestDTO.getExpectedResponseJson();
		String testSpecificData1 = (String) requestDtoMap.get(mapKeys.testSpecificData1.toString());
		String testSpecificData2 = (String) requestDtoMap.get(mapKeys.testSpecificData2.toString());
		String testSpecificData3 = (String) requestDtoMap.get(mapKeys.testSpecificData3.toString());
		String testSpecificData4 = (String) requestDtoMap.get(mapKeys.testSpecificData4.toString());
		String preRequisiteSheet = (String) requestDtoMap.get(mapKeys.preReqSheet.toString());
		String preRequisiteIdentifier = (String) requestDtoMap.get(mapKeys.preReqId.toString());

		HashMap<Object, Object> testSpecificDataMap = new HashMap<Object, Object>();

		URL = "http://cdn.home-designing.com/wp-content/uploads/2014/10/simple-luxury-bedroom-design.jpeg";
		File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator")
				+ PropertiesUtil.getConstantProperty("TempImage"));
		testSpecificDataMap.put("URL", URL);
		testSpecificDataMap.put("image", file);

		LinkedHashMap<String, String> replacePreRequisiteRequestBody = new LinkedHashMap<String, String>();

		// change domain
		if (testSpecificData1 != null
				&& (testSpecificData1.equalsIgnoreCase("yes") || testSpecificData1.equalsIgnoreCase("true"))) {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Makaan");
			domainId = 1;
			sourceDomain = "Makaan";
			absolutePath = "http://content.makaan-ws.com/";
		}
		else{
			domainId = 2;
			absolutePath = "https://im.proptiger-ws.com/";
		}

		testSpecificDataMap.put("sourceDomain", sourceDomain);
		replacePreRequisiteRequestBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());

		// change source as SQL or limited values
		if (testSpecificData2 != null
				&& (testSpecificData2.equalsIgnoreCase("yes") || testSpecificData2.equalsIgnoreCase("true"))) {

			// objectTypes.add("state");
			// objectTypes.add("city");
			LoggerUtil.setlog(LogLevel.INFO, "Running for Multiple ObjectTypes and ImageType");
			objectTypes.add("project");
			// objectTypes.add("project");
			for (String objectType1 : objectTypes) {
				imageTypeIds = MediaServiceHelper.getImageIDsSQL(objectType1, domainId, imageTypeIds);
			}
			// System.out.println("imageTypeIds "+imageTypeIds.get("city"));

		} else {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Single ObjectType and ImageType");
			objectType = "project";
			imageType = "clusterPlan";
			objectId = 111222;
			// origRequestBody for __pattern__
			ArrayList<String> imageTypeId = new ArrayList<String>();
			imageTypeId.add("2");
			imageTypeIds.put(objectType, imageTypeId);
		}

		// change upload to URL/Image
		if (testSpecificData3 != null
				&& (testSpecificData3.equalsIgnoreCase("yes") || testSpecificData3.equalsIgnoreCase("true"))) {
			replacePreRequisiteRequestBody.put("image", testSpecificDataMap.get("image").toString());
		} else {
			replacePreRequisiteRequestBody.put("URL", testSpecificDataMap.get("URL").toString());
		}

		// User HardCoded Data
		if (testSpecificData4 != null
				&& (testSpecificData4.equalsIgnoreCase("yes") || testSpecificData4.equalsIgnoreCase("true"))) {
			deleteFlag = false;
		}

		// Running Prerequisite Single ImageType
		if (preRequisiteSheet != null) {
			if (preRequisiteIdentifier != null) {
				preReqFlag = true;
				RequestDTO preRequestDTO = new RequestDTO();
				Object[][] excelDataForPreReq = DataProviderUtil.provideDataMap(
						PropertiesUtil.getConstantProperty("TestData_API_Media"), preRequisiteSheet,
						preRequisiteIdentifier, true);
				preRequestDTO = RequestUtil.createRequestDTO(excelDataForPreReq, preRequestDTO);
				replacePreRequisiteRequestBody.put("objectType", objectType);
				replacePreRequisiteRequestBody.put("imageType", imageType);
				replacePreRequisiteRequestBody.put("objectId", String.valueOf(objectId));
				// origRequestBody for __pattern__
				String preRequestBody = JavaUtil.replacePreRequisite(replacePreRequisiteRequestBody,
						preRequestDTO.getRequestBody());
				if (preRequestBody != null)
					preRequestDTO.setRequestBody(preRequestBody);

				LinkedHashMap<String, String> preReplaceQueryBody = new LinkedHashMap<String, String>();
				preReplaceQueryBody.put("debug", "true");
				String preQueryBody = JavaUtil.replacePreRequisite(preReplaceQueryBody,
						preRequestDTO.getQueryParamJson());
				if (preQueryBody != null)
					preRequestDTO.setQueryParamJson(preQueryBody);

				httpResponse = apihelper.createRequest(preRequestDTO, baseUrl, httpClient, true);
				responseCode = httpResponse.getStatusLine().getStatusCode();

				if (responseCode == 200) {
					LoggerUtil.setlog(LogLevel.INFO, "PreRequisite Completed Successfully");
					apiResponse = apihelper.getResponse(preRequestDTO, httpResponse);

					// value to get from API Response
					valueToGetFromResponseMap.put("id", "");
					JsonUtil.returnValueFromResponse(apiResponse, valueToGetFromResponseMap);
					imageId = valueToGetFromResponseMap.get("id").toString();
					LoggerUtil.setlog(LogLevel.INFO, "Image Id Created: " + imageId);
				} else {
					LoggerUtil.setlog(LogLevel.INFO, "PreRequisite Not Completed Successfully");
				}
			}
		}

		// Getting objectTypes
		for (String objectType1 : imageTypeIds.keySet()) {
			// System.out.println("objectType1 " + objectType1);
			testSpecificDataMap.put("objectType", objectType1);
			for (String imageID : imageTypeIds.get(objectType1)) {
				// System.out.println("imageID "+i+" : "+imageID);
				if(preReqFlag){
					objectIds = MediaServiceHelper.getpreReqActiveObjectIdsSQL(imageID,objectId);
				}
				else{
					objectIds = MediaServiceHelper.getActiveObjectIdsSQL(imageID);
				}
				count = (int) objectIds.get(0);
				if (count != 0) {
					// System.out.println("objectIds.get(1) "
					// +objectIds.get(1));
					testSpecificDataMap.put("objectId", objectIds.get(1));
					String imageTypeName = MediaServiceHelper.getImageTypesSQL(imageID).get("type").toString()
							.replace("[", "").replace("]", "");
					String imageTypeDisplayName = MediaServiceHelper.getImageTypesSQL(imageID).get("display_name")
							.toString().replace("[", "").replace("]", "");
					// System.out.println("imageTypeName "+imageTypeName);
					// System.out.println("imageTypeDisplayName
					// "+imageTypeDisplayName);

					testSpecificDataMap.put("imageType", imageTypeName);
					testSpecificDataMap.put("imageTypeDisplayName", imageTypeDisplayName);

				} else
					continue;

				LinkedHashMap<String, String> replaceQueryBody = new LinkedHashMap<String, String>();
				replaceQueryBody.put("debug", "true");
				replaceQueryBody.put("objectType", testSpecificDataMap.get("objectType").toString());
				replaceQueryBody.put("imageType", testSpecificDataMap.get("imageType").toString());
				replaceQueryBody.put("objectId", String.valueOf(testSpecificDataMap.get("objectId")));
				replaceQueryBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());
				String queryBody = JavaUtil.replacePreRequisite(replaceQueryBody, origQueryBody);
				if (queryBody != null)
					requestDTO.setQueryParamJson(queryBody);

				// value to get from API Response
				// LinkedHashMap<String, Object> valueToGetFromResponseMap = new
				// LinkedHashMap<String, Object>();
				// valueToGetFromResponseMap.put("id", "");

				LoggerUtil.setlog(LogLevel.INFO, "Sending Test Request");
				LinkedHashMap<Object, Object> whereQuery = new LinkedHashMap<Object, Object>();
				try {
					/*
					 * Creates API Request and Validates the API Response Code
					 */
					httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, false);
					responseCode = httpResponse.getStatusLine().getStatusCode();

				} catch (Exception | Error e) {
					TestListener.setException(e.getMessage(), e.getStackTrace());
					e.printStackTrace();
					failureCount++;
					// APITestBase.softAssert.assertTrue(false);
					continue;
				}
				if (responseCode == 200) {
					try {
						apiResponse = apihelper.getResponse(requestDTO, httpResponse);
						System.out.println(apiResponse);
						if (!preReqFlag) {
							valueToGetFromResponseMap.put("id", "");
							JsonUtil.returnValueFromResponse(apiResponse, valueToGetFromResponseMap);
							imageId = valueToGetFromResponseMap.get("id").toString();
						}

						whereQuery.put("id", imageId);
						imageIdList.add(imageId);

						LinkedHashMap<String, String> replaceResponseBody = MediaServiceHelper
								.replaceResponseBody(whereQuery, testSpecificDataMap, "Image");

						// origResponseBody for __pattern__
						String responseBody = JavaUtil.replacePreRequisite(replaceResponseBody, origResponseBody);
						// System.out.println("responseBody "+responseBody);
						if (responseBody != null)
							requestDTO.setExpectedResponseJson(responseBody);

						LoggerUtil.setlog(LogLevel.INFO, "Validating Test Request");

						/* Validates the JSON Nodes */
						apihelper.validateRequest(requestDTO, apiResponse);
					} catch (Exception | Error e) {
						TestListener.setException(e.getMessage(), e.getStackTrace());
						e.printStackTrace();
						failureCount++;
						// APITestBase.softAssert.assertTrue(false);
						continue;
					} /*
						 * finally { imageIdMap.put("id", imageIdList); //
						 * Deleting Image after creating if (deleteFlag &&
						 * responseCode == 200) { System.out.println(
						 * "Deleting Posted Images");
						 * DBConnectionUtil.deleteQuerywithFilters("proptiger",
						 * "Image", imageIdMap); } }
						 */
				}

			}
		}

		Assert.assertEquals(failureCount, 0, "One of the API's got failed ");
		// APITestBase.softAssert.assertAll();

	}

	@Test(dataProvider = "v1getImageResolutions", priority = 3, enabled = false)
	public void getImageResolutions(String testName, HashMap<String, Object> requestDtoMap) throws Exception {

		requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());

		LoggerUtil.setlog(LogLevel.INFO, "Sending Test Request");

		LinkedHashMap<String, Object> valueToGetFromResponseMap = new LinkedHashMap<String, Object>();
		valueToGetFromResponseMap.put("data", "");
		String originalResponse = requestDTO.getExpectedResponseJson();
		System.out.println(originalResponse);
		LinkedHashMap<String, String> replaceResponseBody = new LinkedHashMap<String, String>();

		try {
			/*
			 * Creates API Request and Validates the API Response Code
			 */
			httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, false);
			responseCode = httpResponse.getStatusLine().getStatusCode();

		} catch (Exception | Error e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			failureCount++;
		}
		if (responseCode == 200) {
			try {
				apiResponse = apihelper.getResponse(requestDTO, httpResponse);
				ResultSet rs1 = null;

				String sql1 = "select resolution, width, height from proptiger.image_resolutions;";
				rs1 = DBConnectionUtil.runQuery(sql1);
				JSONArray jsonArray = MediaServiceHelper.convertResultSetIntoJSON(rs1);

				replaceResponseBody.put("data", jsonArray.toString().replace("\\", "").replace("\"[", "[")
						.replace("]\"", "]").replace("\"{", "{").replace("}\"", "}"));
				String responseBody = JavaUtil.replacePreRequisite(replaceResponseBody,
						requestDTO.getExpectedResponseJson());
				if (responseBody != null)
					requestDTO.setExpectedResponseJson(responseBody);

				LoggerUtil.setlog(LogLevel.INFO, "Validating Test Request");

				/* Validates the JSON Nodes */
				apihelper.validateRequest(requestDTO, apiResponse);
			} catch (Exception | Error e) {
				TestListener.setException(e.getMessage(), e.getStackTrace());
				e.printStackTrace();
				failureCount++;
			}
		}

		Assert.assertEquals(failureCount, 0, "One of the API's got failed ");
		// APITestBase.softAssert.assertAll();

	}

	@Test(dataProvider = "v1deleteImage", priority = 4, enabled = true)
	public void deleteImage(String testName, HashMap<String, Object> requestDtoMap) throws Exception {

		String objectType = null, imageType = null, sourceDomain = "Proptiger", URL = null;
		long objectId = 0;
		ArrayList<String> objectTypes = new ArrayList<String>();
		LinkedHashMap<String, ArrayList<String>> imageTypeIds = new LinkedHashMap<String, ArrayList<String>>();
		LinkedHashMap<String, Object> valueToGetFromResponseMap = new LinkedHashMap<String, Object>();
		String imageId = null;
		String activeFlag = null;
		boolean domainFlag=true;
		requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());

		HashMap<Object, Object> testSpecificDataMap = new HashMap<Object, Object>();

		String testSpecificData1 = (String) requestDtoMap.get(mapKeys.testSpecificData1.toString());
		String testSpecificData2 = (String) requestDtoMap.get(mapKeys.testSpecificData2.toString());
		String testSpecificData3 = (String) requestDtoMap.get(mapKeys.testSpecificData3.toString());
		String testSpecificData4 = (String) requestDtoMap.get(mapKeys.testSpecificData4.toString());
		String preRequisiteSheet = (String) requestDtoMap.get(mapKeys.preReqSheet.toString());
		String preRequisiteIdentifier = (String) requestDtoMap.get(mapKeys.preReqId.toString());

		URL = "http://cdn.home-designing.com/wp-content/uploads/2014/10/simple-luxury-bedroom-design.jpeg";
		File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator")
				+ PropertiesUtil.getConstantProperty("TempImage"));
		testSpecificDataMap.put("URL", URL);
		testSpecificDataMap.put("image", file);

		LinkedHashMap<String, String> replacePreRequisiteRequestBody = new LinkedHashMap<String, String>();

		// change domain
		if (testSpecificData1 != null
				&& (testSpecificData1.equalsIgnoreCase("yes") || testSpecificData1.equalsIgnoreCase("true"))) {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Makaan");
			domainId = 1;
			sourceDomain = "Makaan";
			absolutePath = "http://content.makaan-ws.com/";
		}
		else{
			domainId = 2;
			absolutePath = "https://im.proptiger-ws.com/";
		}

		testSpecificDataMap.put("sourceDomain", sourceDomain);
		replacePreRequisiteRequestBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());

		// change source as SQL or limited values
		if (testSpecificData2 != null
				&& (testSpecificData2.equalsIgnoreCase("yes") || testSpecificData2.equalsIgnoreCase("true"))) {

			// objectTypes.add("state");
			// objectTypes.add("city");
			LoggerUtil.setlog(LogLevel.INFO, "Running for Multiple ObjectTypes and ImageType");
			objectTypes.add("project");
			// objectTypes.add("project");
			for (String objectType1 : objectTypes) {
				imageTypeIds = MediaServiceHelper.getImageIDsSQL(objectType1, domainId, imageTypeIds);
			}
			// System.out.println("imageTypeIds "+imageTypeIds.get("city"));

		} else {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Single ObjectType and ImageType");
			objectType = "project";
			imageType = "clusterPlan";
			objectId = 111222;
			// origRequestBody for __pattern__
			ArrayList<String> imageTypeId = new ArrayList<String>();
			imageTypeId.add("2");
			imageTypeIds.put(objectType, imageTypeId);
		}

		// change upload to URL/Image
		if (testSpecificData3 != null
				&& (testSpecificData3.equalsIgnoreCase("yes") || testSpecificData3.equalsIgnoreCase("true"))) {
			replacePreRequisiteRequestBody.put("image", testSpecificDataMap.get("image").toString());
		} else {
			replacePreRequisiteRequestBody.put("URL", testSpecificDataMap.get("URL").toString());
		}
		
		// Source Domain Not to be Present in Request Body
				if (testSpecificData4 != null
						&& (testSpecificData4.equalsIgnoreCase("yes") || testSpecificData4.equalsIgnoreCase("true"))) {
					domainFlag=false;
				}
				
		// Running Prerequisite Single ImageType
		if (preRequisiteSheet != null) {
			if (preRequisiteIdentifier != null) {
				RequestDTO preRequestDTO = new RequestDTO();
				Object[][] excelDataForPreReq = DataProviderUtil.provideDataMap(
						PropertiesUtil.getConstantProperty("TestData_API_Media"), preRequisiteSheet,
						preRequisiteIdentifier, true);
				preRequestDTO = RequestUtil.createRequestDTO(excelDataForPreReq, preRequestDTO);
				replacePreRequisiteRequestBody.put("objectType", objectType);
				replacePreRequisiteRequestBody.put("imageType", imageType);
				replacePreRequisiteRequestBody.put("objectId", String.valueOf(objectId));
				// origRequestBody for __pattern__
				String preRequestBody = JavaUtil.replacePreRequisite(replacePreRequisiteRequestBody,
						preRequestDTO.getRequestBody());
				if (preRequestBody != null)
					preRequestDTO.setRequestBody(preRequestBody);

				LinkedHashMap<String, String> preReplaceQueryBody = new LinkedHashMap<String, String>();
				preReplaceQueryBody.put("debug", "true");
				String preQueryBody = JavaUtil.replacePreRequisite(preReplaceQueryBody,
						preRequestDTO.getQueryParamJson());
				if (preQueryBody != null)
					preRequestDTO.setQueryParamJson(preQueryBody);

				// uploading image
				uploadList.add("image");

				httpResponse = apihelper.createRequest(preRequestDTO, baseUrl, httpClient, true);
				responseCode = httpResponse.getStatusLine().getStatusCode();

				if (responseCode == 200) {
					LoggerUtil.setlog(LogLevel.INFO, "PreRequisite Completed Successfully");
					apiResponse = apihelper.getResponse(preRequestDTO, httpResponse);

					// value to get from API Response
					valueToGetFromResponseMap.put("id", "");
					valueToGetFromResponseMap.put("active", "");
					JsonUtil.returnValueFromResponse(apiResponse, valueToGetFromResponseMap);
					imageId = valueToGetFromResponseMap.get("id").toString();
					activeFlag = valueToGetFromResponseMap.get("active").toString();
					LoggerUtil.setlog(LogLevel.INFO, "Image Id Created: " + imageId);
					if (activeFlag.equalsIgnoreCase("true")) {
						LoggerUtil.setlog(LogLevel.INFO, "Created Image is Active");
					} else {
						LoggerUtil.setlog(LogLevel.INFO, "Created Image is InActive");
						LoggerUtil.setlog(LogLevel.INFO, "----PreRequisite Not Completed Successfully----");

					}
				} else {
					LoggerUtil.setlog(LogLevel.INFO, "----PreRequisite Not Completed Successfully----");
				}
			}
		}

		LinkedHashMap<String, String> replacePathBody = new LinkedHashMap<String, String>();
		replacePathBody.put("imageId", imageId);
		String pathBody = JavaUtil.replacePreRequisite(replacePathBody, requestDTO.getPathParamJson());
		if (pathBody != null)
			requestDTO.setPathParamJson(pathBody);

		LinkedHashMap<String, String> replaceQueryBody = new LinkedHashMap<String, String>();
		replaceQueryBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());
		String queryBody = JavaUtil.replacePreRequisite(replaceQueryBody, requestDTO.getQueryParamJson());
		if (queryBody != null)
			requestDTO.setQueryParamJson(queryBody);

		LoggerUtil.setlog(LogLevel.INFO, "Sending Test Request");
		try {
			/*
			 * Creates API Request and Validates the API Response Code
			 */
			httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, false);
			responseCode = httpResponse.getStatusLine().getStatusCode();

		} catch (Exception | Error e) {
			failureCount++;
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Delete API Request NOT Successful" + "</pre>");
			throw new RuntimeException(e);
		}
		if (responseCode == 200) {
			try {
				apiResponse = apihelper.getResponse(requestDTO, httpResponse);
				System.out.println(apiResponse);
				LoggerUtil.setlog(LogLevel.INFO, "Validating Test Request");

				/* Validates the JSON Nodes */
				apihelper.validateRequest(requestDTO, apiResponse);
				if(domainFlag){
				Assert.assertEquals(MediaServiceHelper.getImageActiveStatusSQL(imageId), 0,
						"Delete NOT Successful. Image status still active");
				}
				else{
					Assert.assertEquals(MediaServiceHelper.getImageActiveStatusSQL(imageId), 1,
							"Delete should not be Successful");
				}
			} catch (Exception | Error e) {
				failureCount++;
				TestListener.setException(e.getMessage(), e.getStackTrace());
				e.printStackTrace();
				// LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Delete API NOT
				// Successful" + "</pre>");
				throw new RuntimeException(e);

			}
		}

		Assert.assertEquals(failureCount, 0, "One of the API's got failed ");

	}

	@Test(dataProvider = "v1updateImage", priority = 5, enabled = true)
	public void updateImage(String testName, HashMap<String, Object> requestDtoMap) throws Exception {

		String objectType = null, imageType = null, sourceDomain = "Proptiger", URL = null;
		long objectId = 0;
		ArrayList<String> objectTypes = new ArrayList<String>();
		LinkedHashMap<String, ArrayList<String>> imageTypeIds = new LinkedHashMap<String, ArrayList<String>>();
		
		String imageId = null;
		String activeFlag = null;

		requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());

		HashMap<Object, Object> testSpecificDataMap = new HashMap<Object, Object>();
		LinkedHashMap<Object, Object> whereQuery = new LinkedHashMap<Object, Object>();

		String testSpecificData1 = (String) requestDtoMap.get(mapKeys.testSpecificData1.toString());
		String testSpecificData2 = (String) requestDtoMap.get(mapKeys.testSpecificData2.toString());
		String testSpecificData3 = (String) requestDtoMap.get(mapKeys.testSpecificData3.toString());
		String testSpecificData4 = (String) requestDtoMap.get(mapKeys.testSpecificData4.toString());
		String preRequisiteSheet = (String) requestDtoMap.get(mapKeys.preReqSheet.toString());
		String preRequisiteIdentifier = (String) requestDtoMap.get(mapKeys.preReqId.toString());

		URL = "http://cdn.home-designing.com/wp-content/uploads/2014/10/simple-luxury-bedroom-design.jpeg";
		File file = new File(System.getProperty("user.dir") + System.getProperty("file.separator")
				+ PropertiesUtil.getConstantProperty("TempImage"));
		testSpecificDataMap.put("preURL", URL);
		testSpecificDataMap.put("preImage", file);

		LinkedHashMap<String, String> replacePreRequisiteRequestBody = new LinkedHashMap<String, String>();

		// change domain
		if (testSpecificData1 != null
				&& (testSpecificData1.equalsIgnoreCase("yes") || testSpecificData1.equalsIgnoreCase("true"))) {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Makaan");
			domainId = 1;
			sourceDomain = "Makaan";
			absolutePath = "http://content.makaan-ws.com/";
		}
		else{
			domainId = 2;
			absolutePath = "https://im.proptiger-ws.com/";
		}

		testSpecificDataMap.put("sourceDomain", sourceDomain);
		replacePreRequisiteRequestBody.put("sourceDomain", testSpecificDataMap.get("sourceDomain").toString());

		// change source as SQL or limited values
		if (testSpecificData2 != null
				&& (testSpecificData2.equalsIgnoreCase("yes") || testSpecificData2.equalsIgnoreCase("true"))) {

			// objectTypes.add("state");
			// objectTypes.add("city");
			LoggerUtil.setlog(LogLevel.INFO, "Running for Multiple ObjectTypes and ImageType");
			objectTypes.add("project");
			// objectTypes.add("project");
			for (String objectType1 : objectTypes) {
				imageTypeIds = MediaServiceHelper.getImageIDsSQL(objectType1, domainId, imageTypeIds);
			}
			// System.out.println("imageTypeIds "+imageTypeIds.get("city"));

		} else {
			LoggerUtil.setlog(LogLevel.INFO, "Running for Single ObjectType and ImageType");
			objectType = "project";
			imageType = "clusterPlan";
			objectId = 111222;
			// origRequestBody for __pattern__
			ArrayList<String> imageTypeId = new ArrayList<String>();
			imageTypeId.add("2");
			imageTypeIds.put(objectType, imageTypeId);
		}
		testSpecificDataMap.put("objectType", objectType);
		testSpecificDataMap.put("imageType", imageType);
		
		// changing uploaded image
		if (testSpecificData3 != null
				&& (testSpecificData3.equalsIgnoreCase("yes") || testSpecificData3.equalsIgnoreCase("true"))) {
			testSpecificDataMap.put("URL", URL);
			testSpecificDataMap.put("image", file);
			
		} else {
			URL = "http://content.makaan-ws.com/4/0/463/1646616.jpeg";
			File file1 = new File(System.getProperty("user.dir") + System.getProperty("file.separator")
					+ PropertiesUtil.getConstantProperty("TempImage1"));
			testSpecificDataMap.put("URL", URL);
			testSpecificDataMap.put("image", file1);
		}
		
		// change upload to URL/Image
		if (testSpecificData4 != null
				&& (testSpecificData4.equalsIgnoreCase("yes") || testSpecificData4.equalsIgnoreCase("true"))) {
			replacePreRequisiteRequestBody.put("image", testSpecificDataMap.get("preImage").toString());
		} else {
			replacePreRequisiteRequestBody.put("URL", testSpecificDataMap.get("preURL").toString());
		}
		
		// Running Prerequisite Single ImageType
		if (preRequisiteSheet != null) {
			if (preRequisiteIdentifier != null) {
				RequestDTO preRequestDTO = new RequestDTO();
				Object[][] excelDataForPreReq = DataProviderUtil.provideDataMap(
						PropertiesUtil.getConstantProperty("TestData_API_Media"), preRequisiteSheet,
						preRequisiteIdentifier, true);
				preRequestDTO = RequestUtil.createRequestDTO(excelDataForPreReq, preRequestDTO);
				replacePreRequisiteRequestBody.put("objectType", objectType);
				replacePreRequisiteRequestBody.put("imageType", imageType);
				replacePreRequisiteRequestBody.put("objectId", String.valueOf(objectId));
				// origRequestBody for __pattern__
				String preRequestBody = JavaUtil.replacePreRequisite(replacePreRequisiteRequestBody,
						preRequestDTO.getRequestBody());
				if (preRequestBody != null)
					preRequestDTO.setRequestBody(preRequestBody);

				LinkedHashMap<String, String> preReplaceQueryBody = new LinkedHashMap<String, String>();
				preReplaceQueryBody.put("debug", "true");
				String preQueryBody = JavaUtil.replacePreRequisite(preReplaceQueryBody,
						preRequestDTO.getQueryParamJson());
				if (preQueryBody != null)
					preRequestDTO.setQueryParamJson(preQueryBody);

				// uploading image
				uploadList.add("image");

				httpResponse = apihelper.createRequest(preRequestDTO, baseUrl, httpClient, true);
				responseCode = httpResponse.getStatusLine().getStatusCode();

				if (responseCode == 200) {
					LoggerUtil.setlog(LogLevel.INFO, "PreRequisite Completed Successfully");
					apiResponse = apihelper.getResponse(preRequestDTO, httpResponse);

					// value to get from API Response
					LinkedHashMap<String, Object> valueToGetFromResponseMap = new LinkedHashMap<String, Object>();
					valueToGetFromResponseMap.put("id", "");
					valueToGetFromResponseMap.put("active", "");
					String imageTypeName = MediaServiceHelper.getImageTypesSQL(imageTypeIds.get(objectType).get(0)).get("type").toString()
							.replace("[", "").replace("]", "");
					String imageTypeDisplayName = MediaServiceHelper.getImageTypesSQL(imageTypeIds.get(objectType).get(0)).get("display_name")
							.toString().replace("[", "").replace("]", "");
					// System.out.println("imageTypeName "+imageTypeName);
					// System.out.println("imageTypeDisplayName
					// "+imageTypeDisplayName);

					testSpecificDataMap.put("imageType", imageTypeName);
					testSpecificDataMap.put("imageTypeDisplayName", imageTypeDisplayName);

					JsonUtil.returnValueFromResponse(apiResponse, valueToGetFromResponseMap);
					imageId = valueToGetFromResponseMap.get("id").toString();
					activeFlag = valueToGetFromResponseMap.get("active").toString();
					LoggerUtil.setlog(LogLevel.INFO, "Image Id Created: " + imageId);
					if (activeFlag.equalsIgnoreCase("true")) {
						LoggerUtil.setlog(LogLevel.INFO, "Created Image is Active");
					} else {
						LoggerUtil.setlog(LogLevel.INFO, "Created Image is InActive");
						LoggerUtil.setlog(LogLevel.INFO, "----PreRequisite Not Completed Successfully----");

					}
				} else {
					LoggerUtil.setlog(LogLevel.INFO, "----PreRequisite Not Completed Successfully----");
				}
			}
		}

		LinkedHashMap<String, String> replacePathBody = new LinkedHashMap<String, String>();
		replacePathBody.put("imageId", imageId);
		String pathBody = JavaUtil.replacePreRequisite(replacePathBody, requestDTO.getPathParamJson());
		if (pathBody != null)
			requestDTO.setPathParamJson(pathBody);

		LinkedHashMap<String, String> replaceQueryBody = new LinkedHashMap<String, String>();
		replaceQueryBody.put("debug", "true");
		String queryBody = JavaUtil.replacePreRequisite(replaceQueryBody, requestDTO.getQueryParamJson());
		if (queryBody != null)
			requestDTO.setQueryParamJson(queryBody);

		LinkedHashMap<String, String> replaceRequestBody = new LinkedHashMap<String, String>();
		replaceRequestBody.put("priority", String.valueOf(2));
		replaceRequestBody.put("description", "Updating description of " + imageId);
		replaceRequestBody.put("altText", "altText-of");
		replaceRequestBody.put("latitude", String.valueOf(100));
		replaceRequestBody.put("longitude", String.valueOf(11));
		replaceRequestBody.put("imageURL", testSpecificDataMap.get("URL").toString());

		String requestBody = JavaUtil.replacePreRequisite(replaceRequestBody, requestDTO.getRequestBody());
		if (requestBody != null)
			requestDTO.setRequestBody(requestBody);

		LoggerUtil.setlog(LogLevel.INFO, "Sending Test Request");
		try {
			/*
			 * Creates API Request and Validates the API Response Code
			 */
			httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, true);
			responseCode = httpResponse.getStatusLine().getStatusCode();

		} catch (Exception | Error e) {
			failureCount++;
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Delete API Request NOT Successful" + "</pre>");
			throw new RuntimeException(e);
		}
		if (responseCode == 200) {
			try {
				apiResponse = apihelper.getResponse(requestDTO, httpResponse);
				System.out.println(apiResponse);
				LinkedHashMap<String, Object> valueToGetFromResponseMap = new LinkedHashMap<String, Object>();
				valueToGetFromResponseMap.put("id", "");
				JsonUtil.returnValueFromResponse(apiResponse, valueToGetFromResponseMap);
				imageId = valueToGetFromResponseMap.get("id").toString();
				whereQuery.put("id", imageId);

				/* Validates the JSON Nodes */
				LinkedHashMap<String, String> replaceResponseBody = MediaServiceHelper
						.replaceResponseBody(whereQuery, testSpecificDataMap, "Image");

				// origResponseBody for __pattern__
				String responseBody = JavaUtil.replacePreRequisite(replaceResponseBody, requestDTO.getExpectedResponseJson());
				// System.out.println("responseBody "+responseBody);
				if (responseBody != null)
					requestDTO.setExpectedResponseJson(responseBody);

				LoggerUtil.setlog(LogLevel.INFO, "Validating Test Request");

				/* Validates the JSON Nodes */
				apihelper.validateRequest(requestDTO, apiResponse);
			} catch (Exception | Error e) {
				failureCount++;
				TestListener.setException(e.getMessage(), e.getStackTrace());
				e.printStackTrace();
				throw new RuntimeException(e);

			}
		}

		Assert.assertEquals(failureCount, 0, "One of the API's got failed ");

	}

	@AfterMethod
	public void deleteImageIds() throws SQLException {

	}

	@DataProvider(name = "v1postImage", parallel = false)
	public Object[][] postImage() throws Exception {
		Object[][] sheetArray = DataProviderUtil.provideDataMap(
				PropertiesUtil.getConstantProperty("TestData_API_Media"), "postImage_v1", "postImage_v1", true);
		return DataProviderUtil.sheetMapToDPMap(sheetArray);
	}

	@DataProvider(name = "v1getImage", parallel = false)
	public Object[][] getImage() throws Exception {
		Object[][] sheetArray = DataProviderUtil.provideDataMap(
				PropertiesUtil.getConstantProperty("TestData_API_Media"), "getImage_v1", "getImage_v1", true);
		return DataProviderUtil.sheetMapToDPMap(sheetArray);
	}

	@DataProvider(name = "v1getImageResolutions", parallel = false)
	public Object[][] getImageResolutions() throws Exception {
		Object[][] sheetArray = DataProviderUtil.provideDataMap(
				PropertiesUtil.getConstantProperty("TestData_API_Media"), "getImageResolutions_v1",
				"getImageResolutions_v1", true);
		return DataProviderUtil.sheetMapToDPMap(sheetArray);
	}

	@DataProvider(name = "v1deleteImage", parallel = false)
	public Object[][] deleteImage() throws Exception {
		Object[][] sheetArray = DataProviderUtil.provideDataMap(
				PropertiesUtil.getConstantProperty("TestData_API_Media"), "deleteImage_v1", "deleteImage_v1", true);
		return DataProviderUtil.sheetMapToDPMap(sheetArray);
	}

	@DataProvider(name = "v1updateImage", parallel = false)
	public Object[][] updateImage() throws Exception {
		Object[][] sheetArray = DataProviderUtil.provideDataMap(
				PropertiesUtil.getConstantProperty("TestData_API_Media"), "updateImage_v1", "updateImage_v1", true);
		return DataProviderUtil.sheetMapToDPMap(sheetArray);
	}
}
